const express = require("express");
const router = express.Router();
const Test = require("../models/test");


/*
 *	GET request for a specific tests.
 *  Can be queried by status, userid, range
 *  Status can be 0 (Sent), 1 (Passed), 2 (Failed), 3 (Incomplete)
 */
router.get("/", async (req, res) => {
	try {
        let query = {};
        
        if (req.query.userid !== undefined) {
            query.userid = req.query.userid;
        }

        if (req.query.status !== undefined) {
            query.status = req.query.status;
        }

        if (req.query.range !== undefined) {
            const now = new Date();
            query.time = {$gte: now.getTime() - range*1000}
        }

		tests = await Test.find(query);
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

        if (req.body.stationid == null) {
            throw "Not Found";
        }

        let test = await Test.findOne({"stationid": req.body.stationid, "time" : {$gte: req.body.time - 600000}});
        
        console.log(test);
        if (test == null) {
            throw "Not Found"
        }

        test.status = 1;
		await test.save();
		res.status(200).send("Successfully updated test status");
	} catch (err) {
		res.status(400).send(err.message);
	}
});



module.exports = router;
