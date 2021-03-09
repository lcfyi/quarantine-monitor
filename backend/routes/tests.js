const express = require("express");
const router = express.Router();
const Test = require("../models/test");


/*
 *	GET request for a specific station.
 *  Can be queried by admin, stationid, starttime, endtime, or status
 *  Status can be 0 (Sent), 1 (Passed), 2 (Failed), 3 (Incomplete)
 */
router.get("/", async (req, res) => {
	try {
		tests = await Test.find({});
        console.log(tests);
        if (tests != null)
            res.json(tests); 
	} catch (err) {
		return res.status(404).send("Error 404: " + err.message);
	}
});

/*
 *	POST request on status of test
 *  Requires userid, stationid, status, and time
 */
 router.post("/update", async (req, res) => {
	try {

        if (req.body.userid == null || req.body.stationid == null || req.body.status == null) {
            throw "Not Found";
        }

        let test = await Test.findOne({"userid": req.body.userid, "stationid": req.body.stationid, "time" : {$gte: req.body.time - 600}})
        
        console.log(test);
        if (test == null) {
            throw "Not Found"
        }

        test.status = req.body.status;
		await test.save();
		res.status(200).send("Successfully updated test status");
	} catch (err) {
		res.status(400).send(err.message);
	}
});



module.exports = router;
