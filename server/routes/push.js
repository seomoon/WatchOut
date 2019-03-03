const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool.js');
const aws = require('aws-sdk');
//aws.config.loadFromPath('../../config/aws_config.json');
const moment = require('moment');
const FCM = require('fcm-push');
var serverKey = require('../config/serverKey').serverKey;
var fcm = new FCM(serverKey);

router.post('/', async(req, res) => {
  try {
    //데이터 안넣으면 오류발생처리
    if(!(req.body.content))
      res.status(403).send({ message: 'please input all of content'});
    //데이터 다 넣으면 실행
    else {
      var connection = await pool.getConnection();

       //알람정보입력
       let query1='insert into attack_info set ?';
       let record = {
         content : req.body.content,
         date : moment(new Date()).format('YYYY-MM-DD'),
         time : moment().format('LT'),
         dId : req.body.dId
         };
        await connection.query(query1, record);

        //dId 에 등록된 대표자의 정보 받아오기
        let query2 ='select user.* from watchout.user'+
        ' join watchout.device'+
        ' where device.userId=user.userId'+
        ' and device.dId = ?'
        let result = await connection.query(query2, req.body.dId);

        //대표자 아이디 조회
        let query3 = 'select userId from user where deviceToken = ?';
        let userId = await connection.query(query3, result[0].deviceToken);
        //대표자(pId)의 가족 정보 받아오기
        let query4 = 'select * from user where pId = ?';
        let family_result = await connection.query(query4, userId[0].userId);


        //정보중에 알람 비동의자는 push 보내지 않음
        //tokens에 알림을 뿌릴 token들 저장
        let ids=[];
        if(result[0].push==1){
          ids.push(result[0].deviceToken);
        }
        for(var i in family_result){
          if(family_result[i] && family_result[i].push==1){
            ids.push(family_result[i].deviceToken);
          }
        }

        console.log("ids.length : "+ids.length);

        //알림 보내기
        //만약 ids길이가 1이면 to
        if(ids.length==1){
          var message = {
              to : ids[0], // required fill with device token or topics
              notification: {
                  title: 'WatchOut',
                  body: req.body.content
              }
          };
        }else if(ids.length>1){
        //1보다 크면 registration_ids
          var message = {
            registration_ids : ids, // required fill with device token or topics
            notification: {
                title: 'WatchOut',
                body: req.body.content
            }
        };
        }

        // console.log(serverKey);

        //fcm에 메세지 보내기
        fcm.send(message)
          .then(function(response){
              console.log(message);
              console.log("Successfully sent with response: ", response);
          })
          .catch(function(err){
              console.log("Something has gone wrong!");
              console.error(err);
          });


        //성공시
        res.status(200).send({message:"sucess in send message"});

    }//end of else
  } //end of try
  catch(err){
      console.log(err);
      res.status(500).send({message: "syntax error :" [err]});
      await connection.rollback();
  }
  finally{
      pool.releaseConnection(connection);
  }
});

module.exports = router;
