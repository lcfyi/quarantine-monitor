const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const Station = require("../../models/station");
const sendPushNotification = require("../../pushnotification");
const algorithm = require("../../algorithm");

// Routers for Register/Login functionality
const userRegisterRouter = require("./userRegister");
const userLoginRouter = require("./userLogin");
router.use("/", userRegisterRouter);
router.use("/login", userLoginRouter);

/*
 *	Helper function to get a user by userid.
 */
async function getUser(req, res, next) {
	let user;
	try {
		user = await User.findById(req.params.userid)
        .select('username deviceToken stationid lastCoords locationMap admin status availability scheduledTests endTime startTime');

        if (user == null)
            throw "Not Found";
	} catch (err) {
		return res.status(404).send("Error 404: " + err.message);
	}
	res.user = user;
	next();
}

/*
 *	GET request for a specific user.
 */
router.get("/:userid", getUser, (req, res) => {
	res.json(res.user);
});

/*
 *	GET request to return list of active users.
 */
 router.get("/:userid/active", async (req, res) => {
	try {
		const now = new Date();
        let users = await User.find({"admin": false, "endTime": { $gt: now.getTime()}}).select('_id lastCoords startTime endTime');
		res.status(200).json(users);
	} catch (err) {
		res.status(404).json([]);
	}
});

/*
 *	GET request for a specific user.
 */
router.get("/:userid/requestlocation", getUser, (req, res) => {
	sendPushNotification(res.user.deviceToken, {"key": "0"});
	res.send("Submitted silent push notification!");
});

/*
 *	DELETE request for the devicetoken
 */
router.delete("/:userid/devicetoken", async (req, res) => {
	try {
		let user = await User.findById(req.params.userid);

		user.deviceToken = "";
		await user.save();
		res.status(200).send("Successfully signed out");
	} catch(err) {
		res.status(400).send(err.message);
	}
	
})

/*
 *	Test request to test the polling loops.
 */
 router.get("/:userid/sendtest/", getUser, async (req, res) => {
	await algorithm.handleTests(true, res.user._id.toString());
	res.send("Executed loop 1");
});

/*
 *	Test request to test the polling loop 2.
 */
 router.get("/:userid/sendtest2/", getUser, async (req, res) => {
	await algorithm.handleTests(false, "");
	res.send("Executed loop 2");
});

/*
 *	GET request for user location data.
 */
router.get("/:userid/plotmap", getUser, (req, res) => {
	res.json(res.user.locationMap);
});

/*
 *	UPDATE request on fields of user
 */
router.put("/:userid", async (req, res) => {
	try {
		let user = await User.findById(req.params.userid)
        
		if (req.body.stationid != null) {
			user.stationid = req.body.stationid;
        }
        if (req.body.coordinates != null) {
			user.lastCoords = req.body.coordinates;
            let currentUnix = new Date().getTime().toString();
            let locMapEntry = JSON.parse("{\"time\":" + currentUnix + ",\"coordinates\": [" + req.body.coordinates.toString() + "], \"status\": " + user.status + "}");
            user.locationMap.push(locMapEntry);
        }

		if (req.body.availability != null) {
			user.availability = req.body.availability;
			user.scheduledTests = algorithm.randomizedTimes(req.body.availability);		
		}

		if (req.body.endTime != null) {
			user.endTime = req.body.endTime
		}

		if (req.body.token != null) {
			user.deviceToken = req.body.token;
		}

		await user.save();
		res.status(200).json({"message": "Successfully updated user details"});
	} catch (err) {
		res.status(400).json(err.message);
	}
});

module.exports = router;
