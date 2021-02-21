const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const passwordHelper = require("./passwordHelper");

/*
 *	POST request to register a user. 
 *  Takes in username, password, and current location and returns user object.
 */
router.post("/", async (req, res) => {
	try {
		let hashData = passwordHelper.saltHashPassword(req.body.password);
        let currentUnix = new Date().getTime().toString();
        let locMapEntry = JSON.parse("{\"time\":" + currentUnix + ",\"coordinates\": [" + req.body.coordinates.toString() + "]}");
        const user = new User({
			username: req.body.username,
			deviceToken: "",
			password: hashData.passwordHash,
			salt: hashData.salt,
            stationid: "",
            lastCoords: req.body.coordinates,
            locationMap: [locMapEntry]
		});

        // Only create user if username does not exist, else return 400
		User.countDocuments({ "username": req.body.username }, async function (err, count) {

			if (count !== 0) {
				res.status(400).send("User already exists with that username");
				logger("User already exists with that username");
			} else {
				await user.save();
				logger("User created");
				res.status(201).json(user);
			}

		});
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;
