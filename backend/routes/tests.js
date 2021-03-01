const express = require("express");
const router = express.Router();
const Test = require("../models/test");


// /*
//  *	GET request for a specific station.
//  *  Can be queried by userids, stationids starttime, endtime, or status
//  */
// router.get("/", async (req, res) => {

// 	try {

//         req.query.userid;

// 		test = Test.find({}, async function(err, tests) {

//         });

//         if (tests == null)
//             throw "Not Found";
// 	} catch (err) {
// 		return res.status(404).send("Error 404: " + err.message);
// 	}


// 	res.json(tests);
// });

// /*
//  *	POST request to create a test. 
//  *  Takes in stationid, and creates the station.
//  */
// router.post("/", async (req, res) => {
//     try {
//         const station = new Station({
//             _id: req.body.stationid,
//             users: [],
//             baseCoords: []
//         });

//          // Only create station if does not exist, else return 400
// 		Station.countDocuments({ "_id": req.body.stationid }, async function (err, count) {

// 			if (count !== 0) {
// 				res.status(400).send("Station already exists");
// 			} else {
// 				await station.save()
//                 res.status(201).send("Station created");
// 			}

// 		});
//     } catch (err) {
//         res.status(400).send(err.message);
//     }
// });

module.exports = router;
