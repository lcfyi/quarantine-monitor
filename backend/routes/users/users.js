const debug = require("debug");
const logger = debug("app:error");
const express = require("express");
const router = express.Router();
const User = require("../../models/user");
const Station = require("../../models/station");

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
 *	GET request for user location data.
 */
router.get("/:userid/plotmap", getUser, (req, res) => {
	res.json(res.user.locationMap);
});

/*
 *	GET request for user station. Returns the JSON object for the station.
 */
router.get("/:userid/station", getUser, async (req, res) => {
    try {
		let station = await Station.find({"stationid": res.user.stationid});
		res.status(200).json(station);
	} catch (err) {
		res.status(404).send("Station not found.");
	}
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
