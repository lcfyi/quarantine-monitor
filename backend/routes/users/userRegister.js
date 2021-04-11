const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const Station = require("../../models/station");
const passwordHelper = require("./passwordHelper");
const algorithm = require("../../algorithm");

/*
 *	POST request to register a user. 
 *  Takes in username, password, and current location, availability, and stationid and returns user object.
 */
router.post("/", async (req, res) => {
	try {
		let hashData = passwordHelper.saltHashPassword(req.body.password);
        let currentUnix = new Date().getTime();
        let locMapEntry = JSON.parse("{\"time\":" + currentUnix.toString() + ",\"coordinates\": [" + req.body.coordinates.toString() + "], \"status\": true}");
        const user = new User({
			username: req.body.username,
			deviceToken: "",
			password: hashData.passwordHash,
			salt: hashData.salt,
            stationid: req.body.stationid,
            lastCoords: req.body.coordinates,
			startTime: currentUnix,
			endTime: currentUnix + 1209600000, // 2 weeks after user created
            locationMap: [locMapEntry],
			admin: false,
			status: true,
			availability: req.body.availability, 
			scheduledTests: algorithm.randomizedTimes(req.body.availability) 
		});



        // Only create user if username does not exist, else return 400
		User.countDocuments({ "username": req.body.username }, async function (err, count) {

			if (count !== 0) {
				res.status(400).send("User already exists with that username");
				logger("User already exists with that username");
			} else {
				await user.save();
				logger("User created");

				let station = await Station.findById(req.body.stationid);
				station.user = user._id;
				await station.save();

				res.status(201).send({"_id": user._id, "admin": user.admin, "endTime": user.endTime});
			}

		});
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;
