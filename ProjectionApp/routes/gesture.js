
var express = require('express');
var router = express.Router();

var data = "{ \"x\": 100, \"y\": 100, \"action\": {\"gesture\":\"tap\",\"color\":\"red\"} }";

router.post('/', function(req, res, next) {
  data = req.body.data
  console.log(data);
  res.send("okay"); 
});

router.get('/', function(req, res, next) {
  //console.log(data);
  res.send(data);
});

module.exports = router;
