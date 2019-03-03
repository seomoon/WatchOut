const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool.js');

//알람받기여부수정
router.put('/alarm/:userId', async(req, res)=>{
  try{
    var connection = await pool.getConnection();
    const userId = req.params.userId;

      let query1 = 'update user set push=-1*push where userId=? ';
      await connection.query(query1, userId);
      let query2 = 'select push from user where userId=? ';
      let push = await connection.query(query2, userId);

      res.status(200).send({
          message:'push alarm update ok',
          pushInfo:push[0].push
        });
  }
  catch (err){
    console.log(err);
    res.status(500).send({message:'server err: '+err});
  }
  finally{
    pool.releaseConnection(connection);
  }
});

//기기 on/off (학생의경우만)
router.put('/device/:userId', async(req, res)=>{
  try{
    var connection = await pool.getConnection();
    const userId = req.params.userId;

      let query1 = 'update device set connect=-1*connect where userId=? ';
      await connection.query(query1, userId);

      let query2 = 'select connect from device where userId=? ';
      let connect = await connection.query(query2, userId);

      res.status(200).send({
        message:'device connection update ok',
        connectInfo : connect[0].connect
      });
  }
  catch (err){
    console.log(err);
    res.status(500).send({message:'server err: '+err});
  }
  finally{
    pool.releaseConnection(connection);
  }
});

//회원탈퇴
//대표자가 탈퇴하는경우 디바이스의 정보와 가족들의 정보도 다 cascade로 삭제
//알람정보도삭제
router.delete('/withdraw/:userId', async (req, res)=>{
  try{
    var connection = await pool.getConnection();
    const userId = req.params.userId;

    let query1 = 'select pId from user where userId=?';
    let pId = await connection.query(query1, userId);
    console.log(pId[0].pId); //pId확인

    //탈퇴회원이 대표자인경우 -> pId가 있으면 가족회원
    if(pId[0].pId==null){
      //대표자 니까 pId가 userId인 데이터 삭제
      let query1 = 'delete from user where pId=?';
      await connection.query(query1, userId);

    }
    //회원정보삭제
    let query2 = 'delete from user where userId=?';
    await connection.query(query2, userId);
    res.status(200).send({message:'user withdraw success'});
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
