const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool.js');

//알람 정보 조회
router.get('/:userId', async (req, res)=>{
  try{
    var connection = await pool.getConnection();

    const userId = req.params.userId;

    //pId 얻기
    let query1 = 'select pId from user where userId=?';
    let pId = await connection.query(query1, userId);
    console.log(pId[0].pId);

    //회원이 대표자인 경우
    if(pId[0].pId==null){
      //대표자의 알림정보 검색
      let query2 = 'select distinct attack_info.content, attack_info.date, attack_info.time, attack_info.dId from attack_info '+
      'natural join device where userId=? ORDER BY info_num desc limit 20';
      var result = await connection.query(query2, userId);
    }else if (pId[0].pId){
      //회원이 가족인 경우
      let query3 = 'select distinct attack_info.content, attack_info.date, attack_info.time, attack_info.dId from attack_info '+
      'natural join device where userId=? ORDER BY info_num desc limit 20';
      var result = await connection.query(query3, pId[0].pId);
    }

    res.status(200).send({message:'alarm info successful', ret:result});
  }
  catch (err){
    res.status(500).send({message:'server err:'+err});
  }
  finally{
    pool.releaseConnection(connection);
  }
});

//알림정보삭제
//전체삭제밖에 안도ㅐ
router.delete('/:userId', async (req, res)=>{
  try{
    var connection = await pool.getConnection();
    const userId = req.params.userId;

    //pId 얻기
    let query1 = 'select pId from user where userId=?';
    let pId = await connection.query(query1, userId);
    console.log(pId[0].pId);

    //회원이 대표자인 경우
    if(pId[0].pId==null){
      //대표자의 알림정보 검색
      let query2 = 'delete attack_info from attack_info natural join device where userId=?';
      var result = await connection.query(query2, userId);
    }else{
      //회원이 가족인 경우
      let query3 = 'delete attack_info from attack_info natural join device where userId=?';
      var result = await connection.query(query3, pId[0].pId);
    }

    res.status(200).send({message:'alarm info delete success'});

  }
  catch(err){
    console.log(err);
    res.status(500).send({message:'server err: '+err});
    await connection.rollback();
  }
  finally{
    pool.releaseConnection(connection);
  }
});

module.exports = router;
