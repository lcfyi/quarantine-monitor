const express = require("express");
const router = express.Router();
const Test = require("../models/testModel");


/*
 *	GET request for a specific tests.
 *  Can be queried by status, userid
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

		tests = await Test.find(query);
        if (tests != null)
            res.json(tests); 
	} catch (err) {
		return res.status(404).send("Error 404: " + err.message);
	}
});



module.exports = router;
