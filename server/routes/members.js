const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool.js');
//const aws = require('aws-sdk');
//aws.config.loadFromPath('../../config/aws_config.json');
//const s3 = new aws.S3();
//const bcrypt = require('bcrypt-nodejs');
//const jwt = require('jsonwebtoken');
//const saltRounds = 10;

//회원가입
/*사용자아이디, 비밀번호, 핸즈폰번호, 본인(1)과 가족(0)를 구분짓는 type값을 post*/
router.post('/join', async(req, res) => {
  try {
    //데이터 안넣으면 오류발생처리
    if(!(req.body.userId&&req.body.pwd&&req.body.phone&&req.body.type))
      res.status(403).send({ message: 'please input all of userId, pwd, phone, type'});
    //데이터 다 넣으면 실행
    else {
      var connection = await pool.getConnection();

       //회원가입정보입력
       let query1='insert into user set ?';
       let record = {
            userId : req.body.userId,
            pwd : req.body.pwd,
            phone : req.body.phone,
            type : req.body.type,
            push : 1,
            pId : req.body.pId,
            deviceToken :  req.body.deviceToken
         };
        await connection.query(query1, record);

        //성공시
        res.status(200).send({message:"sucess in join"});
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

//아이디중복확인
router.get('/dup/:userId', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        //아이디중복확인
        let query1='select userId from user';
        let result = await connection.query(query1);
        let flag = 1; //아이디가 중복되면 1, 아니면 0인 변수
        const userId = req.params.userId;
        //아이디가 중복되면 flag변수 0으로 세팅
        for(var i in result){
          if(result[i].userId == userId)
            flag = 0;
        }
      //중복이면 400에러, 중복이 아니면 ok 메세지
       if(flag==0)
           res.status(400).send({"message": "duplicated ID"});
       else
          res.status(200).send({"message" : "available ID"});
    }//end of try
    catch(err){
        console.log(err);
        res.status(500).send({
          "message": "syntax error : " [err]
        });
        await connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }
});

//기기등록
router.post('/register', async(req, res) => {
  try {
    //데이터 안넣으면 오류발생처리
    if(!(req.body.userId&&req.body.dId))
      res.status(403).send({ message: 'please input all of userId, dId'});
    //데이터 다 넣으면 실행
    else {
      var connection = await pool.getConnection();
      var userId = req.body.userId;
      var dId = req.body.dId;

       //등록된기기가 맞으면 userId정보 입력
      let query = 'select * from device where dId=?'
          let deviceInfo = await connection.query(query, dId);
          //없는 디바이스 인경우
          console.log(deviceInfo);
          if (deviceInfo[0] == null ) {
             res.status(402).send({ message: 'no exist the device'});
          }else if(deviceInfo[0].userId == null){
            //디바이스가 아무 등록도 안된경우 등록하면 됨
            let query='update device set ? where dId=?';
            let record = {
                  userId : userId,
                  connect : 1
            };
            await connection.query(query, [record, dId]);
            res.status(200).send({message: "sucess in register"});

          }else if(deviceInfo[0].userId != null){
            //the device is already using
             res.status(406).send({ message: 'the device is already using'});
          }
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

//대표자아이디확인
router.get('/confirm/:userId', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        let query = "select userId from user where userId=?";
        var userId =  await connection.query(query, req.params.userId);
        console.log(userId);
        if(userId[0] == null){
         res.status(401).send({"message":"This ID does not exist"});
       }else{
          res.status(200).send({
              "message" : "ID exists",
              "result" : userId[0]
          });
        }//end of else
    }//end of try
    catch(err){
        console.log(err);
        res.status(500).send({
          "message": "syntax error : " [err]
        });
        await connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }
});

//로그인
router.post('/login', async function(req, res){
    try {
        var connection = await pool.getConnection();
        const userId = req.body.userId;
        const pwd = req.body.pwd;
        let query1 = 'select userId, pwd from user where userId=?';
        let user_info = await connection.query(query1, userId) || null;

        if(pwd!=user_info[0].pwd) res.status(401).send({message: 'wrong password'});
        else {
          //클라에서 token 보낼 경우만 갱신
            if(req.body.deviceToken){
              let query2 ='update user set deviceToken=? where userId=?';
              await connection.query(query2, [req.body.deviceToken, userId]);
            }

          //jwt 발급하고 성공메세지 보내주기
          // let option = {
          //   algorithm : 'HS256',
          //   expiresIn: 60 * 60 * 24 * 30 //토큰 발행 후 유효기간 지정(30일)
          // }
          // let payload = {
          //   userId: user_info[0].userId
          // };
          // let token = jwt.sign(payload, req.app.get('jwt-secret'), option);
          //유저의 정보 조회하기
          let query3 = 'select * from user where userId=?'
          let result = await connection.query(query3, userId);

          //대표자 아이디인 경우
          if(result[0].pId==null){
            //device정보 조회하기
            let query4 = 'select * from device where userId=?'
            var device_result = await connection.query(query4, userId);
          }else{
            //가족회원인경우
            //가족회원의 pId얻어내서
            let query5 = 'select pId from user where userId=?'
            let pId = await connection.query(query5, userId);
            //디바이스테이블의 내용 뽑아오기
            let query6 = 'select * from device where userId=?'
            var device_result = await connection.query(query6, pId[0].pId);
          }

        res.status(200).send({
          message:'login is successful',
          //token: token,
          userInfo: result[0],
          deviceInfo: device_result[0]
        });


        }
    }
    catch(err) {
        console.log(err);
        res.status(500).send({message: 'server err: '+err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

//아이디찾기
router.get('/id/:phone', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        let query = "select userId from user where phone=?";
        var userId =  await connection.query(query, req.params.phone);
        console.log(userId);
        if(userId[0] == null){
         res.status(401).send({"message":"ID does not exist"});
       }else{
          res.status(200).send({
              "message" : "ID exists",
              "result" : userId[0]
          });
        }//end of else
    }//end of try
    catch(err){
        console.log(err);
        res.status(500).send({
          "message": "syntax error : " [err]
        });
        await connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }
});

//폰번호로 계정 확인
router.get('/phone/:phone', async (req, res)=>{
  try{
    var connection = await pool.getConnection();
    let query = 'select * from user where phone=?';
    var info=await connection.query(query, req.params.phone);
    if(info[0] == null)
      res.status(400).send({message:'This member does not exist'});
    else
      res.status(200).send({message:'This member exists. Please change your password'});
  }
  catch(err){
    res.status(500).send({message:'server err', err});
  }
  finally{
    pool.releaseConnection(connection);
  }
});

//비밀번호수정
router.put('/pwd/:phone', async (req, res)=>{
  try{
    if(!req.body.pwd)
      res.status(403).send({message: 'please input pwd'});
    else{
      var connection = await pool.getConnection();
      let query = 'update user set pwd=? where phone=?';
      await connection.query(query, [req.body.pwd, req.params.phone]);
      res.status(200).send({message: "password change successful"});
    }//end of else
  }//end of try
  catch (err){
    res.status(500).send({message:'server err :'+err});
  }
  finally{
    pool.releaseConnection(connection);
  }
});

module.exports = router;
