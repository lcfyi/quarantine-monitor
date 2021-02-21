const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");

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
        .select('username deviceToken stationid lastCoords locationMap');
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
 *	DELETE request to delete a user.
 */
router.delete("/:userid", getUser, (req, res) => {
	res.user.remove();
	res.send("Deleted user");
	logger("Deleted user");
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
			user.lastCoords = req.body.location;
            let currentUnix = new Date().getTime().toString();
            let locMapEntry = JSON.parse("{\"time\":" + currentUnix + ",\"coordinates\": [" + req.body.coordinates.toString() + "]}");
            user.locationMap.push(locMapEntry);
        }

		await user.save();
		res.status(200).send("Successfully updated user details");
	} catch (err) {
		res.status(400).send(err.message);
	}
});

module.exports = router;
