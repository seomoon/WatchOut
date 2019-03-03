const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool.js');
//const aws = require('aws-sdk');
//aws.config.loadFromPath('../../config/aws_config.json');
//const s3 = new aws.S3();
//const bcrypt = require('bcrypt-nodejs');
//const jwt = require('jsonwebtoken');
//const saltRounds = 10;

router.get('/:dId', async (req, res) => {
    try {
        var connection = await pool.getConnection();

        const dId = req.params.dId;
        //let query1='select * from attack_info where dId=?';
        //let result = await connection.query(query1, dId);
        var week = ['일', '월', '화', '수', '목', '금', '토'];
        var d = new Date();
        //var nowDate = d.getDate();
        var nowYoil = week[d.getDay()];
        var mDate = new Date();
        var mDate2 = new Date();
        var mDate3 = new Date();
        var mDate4 = new Date();
        var mDate5 = new Date();
        var mDate6 = new Date();
        var dayofMonth = d.getDate();
        mDate.setDate(dayofMonth-1);
        mDate2.setDate(dayofMonth-2);
        mDate3.setDate(dayofMonth-3);
        mDate4.setDate(dayofMonth-4);
        mDate5.setDate(dayofMonth-5);
        mDate6.setDate(dayofMonth-6);

        var date, date_1, date_2, date_3, date_4, date_5, date_6;
        var dd = d.getDate(); var mm = d.getMonth()+1;
        var dd2 = mDate.getDate(); var mm2 = mDate.getMonth()+1;
        var dd3 = mDate2.getDate(); var mm3 = mDate2.getMonth()+1;
        var dd4 = mDate3.getDate(); var mm4 = mDate3.getMonth()+1;
        var dd5 = mDate4.getDate(); var mm5 = mDate4.getMonth()+1;
        var dd6 = mDate5.getDate(); var mm6 = mDate5.getMonth()+1;
        var dd7 = mDate6.getDate(); var mm7 = mDate6.getMonth()+1;
        if(dd<10) {dd = '0'+dd}
        if(mm<10) {mm='0'+mm}
        if(dd2<10) {dd2 = '0'+dd2}
        if(mm2<10) {mm2='0'+mm2}
        if(dd3<10) {dd3 = '0'+dd3}
        if(mm3<10) {mm3='0'+mm3}
        if(dd4<10) {dd4 = '0'+dd4}
        if(mm4<10) {mm4='0'+mm4}
        if(dd5<10) {dd5 = '0'+dd5}
        if(mm5<10) {mm5='0'+mm5}
        if(dd6<10) {dd6 = '0'+dd6}
        if(mm6<10) {mm6='0'+mm6}
        if(dd7<10) {dd7 = '0'+dd7}
        if(mm7<10) {mm7='0'+mm7}

        var date = d.getFullYear()+'-'+mm+'-'+dd;
        var date_1 = mDate.getFullYear()+'-'+mm2+'-'+dd2;
        var date_2 = mDate2.getFullYear()+'-'+mm3+'-'+dd3;
        var date_3 = mDate3.getFullYear()+'-'+mm4+'-'+dd4;
        var date_4 = mDate4.getFullYear()+'-'+mm5+'-'+dd5;
        var date_5 = mDate5.getFullYear()+'-'+mm6+'-'+dd6;
        var date_6 = mDate6.getFullYear()+'-'+mm7+'-'+dd7;

        console.log("date"+ date);
        console.log("date_5"+ date_5);
        console.log("dId" + dId);
        console.log("요일"+nowYoil);
        console.log("nDate"+mDate5);
        console.log("d"+d);

        let query='select content, count(distinct time) as count from attack_info '+
        'where dId=? and date=? group by content';
        
        switch(nowYoil){
          case "일" : let result = await connection.query(query, [dId, date]);
                      let result_1 = await connection.query(query, [dId, date_1]);
                      let result_2 = await connection.query(query, [dId, date_2]);
                      let result_3 = await connection.query(query, [dId, date_3]);
                      let result_4 = await connection.query(query, [dId, date_4]);
                      let result_5 = await connection.query(query, [dId, date_5]);
                      let result_6 = await connection.query(query, [dId, date_6]);


                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_6,
                               "tuesday" : result_5,
                               "wednesday" : result_4,
                               "thursday" : result_3,
                               "friday" : result_2,
                               "saturday" : result_1,
                               "sunday" : result
                       });


                    break;
          case "월" : let result_00 = await connection.query(query, [dId, date]);
                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_00
                       });

                    break;

          case "화" : let result_10 = await connection.query(query, [dId, date]);
                      let result_11 = await connection.query(query, [dId, date_1]);
                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_11,
                               "tuesday" : result_10
                       });


                    break;
          case "수" : let  result_20 = await connection.query(query, [dId, date]);
                      let result_21 = await connection.query(query, [dId, date_1]);
                      let result_22 = await connection.query(query, [dId, date_2]);

                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_22,
                               "tuesday" : result_21,
                               "wednesday" : result_20
                       });


                    break;
          case "목" :  let result_30 = await connection.query(query, [dId, date]);
                       let result_31 = await connection.query(query, [dId, date_1]);
                       let result_32 = await connection.query(query, [dId, date_2]);
                       let result_33 = await connection.query(query, [dId, date_3]);

                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_33,
                               "tuesday" : result_32,
                               "wednesday" : result_31,
                               "thursday" : result_30
                       });


                    break;
          case "금" : let result_40 = await connection.query(query, [dId, date]); //금
                      let result_41 = await connection.query(query, [dId, date_1]); //목
                      let result_42 = await connection.query(query, [dId, date_2]); //수
                      let result_43 = await connection.query(query, [dId, date_3]); //화
                      let result_44 = await connection.query(query, [dId, date_4]); //월

                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_44,
                               "tuesday" : result_43,
                               "wednesday" : result_42,
                               "thursday" : result_41,
                               "friday" : result_40,


                       });



                    break;
          case "토" : let result_50 = await connection.query(query, [dId, date]);
                      let result_51 = await connection.query(query, [dId, date_1]);
                      let result_52 = await connection.query(query, [dId, date_2]);
                      let result_53 = await connection.query(query, [dId, date_3]);
                      let result_54 = await connection.query(query, [dId, date_4]);
                      let result_55 = await connection.query(query, [dId, date_5]);


                      res.status(200).send({
                              "message" : "success stats",
                               "monday" : result_55,
                               "tuesday" : result_54,
                               "wednesday" : result_53,
                               "thursday" : result_52,
                               "friday" : result_51,
                               "saturday" : result_50,

                       });

                    break;
          default : res.status(403).send({
                            "message" : "date error",
                    });


        }


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


module.exports = router;
