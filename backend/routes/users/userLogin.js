const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const passwordHelper = require("./passwordHelper");

/*
 *	POST request to login a user. 
 *  Takes in username, password, and device token. Returns the userid.
 */
router.post("/", async (req, res) => {
	try {
		const username = req.body.username;
		const user = await User.findOne({ "username": username });

		const inputPassword = req.body.password;
		const salt = user.salt;
		const encryptedPassword = user.password;
		const hashedPassword = passwordHelper.checkHashPassword(inputPassword, salt).passwordHash;
		if (encryptedPassword === hashedPassword) {
			user.deviceToken = req.body.token;
			logger(req.body.token);
			await user.save();
			res.status(201).send({"userid": user._id, "admin": user.admin, "endtime": user.endTime});
			logger({"userid": user._id});
		} else {
			res.status(400).send("Incorrect Password");
			logger("Incorrect Password");
		}
	} catch (err) {
		res.status(400).json({ message: err.message });
	}
});

module.exports = router;
