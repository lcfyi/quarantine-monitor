const express = require("express");
const router = express.Router();
const Station = require("../models/station");
const User = require("../models/user");
const Test = require("../models/test");
const sendPushNotification = require("../pushnotification");
const crypto = require("crypto");

/*
 *	Helper function to get a station by stationid.
 */
async function getStation(req, res, next) {
	let station;
	try {
		station = await Station.findById(req.params.stationid);
        
        if (station == null)
            throw "Not Found";
	} catch (err) {
		return res.status(404).send("Error 404: " + err.message);
	}
	res.station = station;
	next();
}

/*
 *  GET request used by De1 to test the connection
 */
router.get("/", (req, res) => {
    res.status(200).send("OK");
});

/*
 *  Ingestion endpoint that will receive data from all stations, and respond accordingly
 */
router.post("/", async (req, res) => {
    try {
        let [json, checksum] = req.body.split(';');
        
        // Determine if payload received is corrupted or not via the checksum
        if (crypto.createHash("SHA256").update(json).digest("hex") === checksum) {

            // Confirm that the header contains base and token
            if (req.headers.base !== undefined) {

                let station = await Station.findById(req.headers.base);
                let user = await User.findById(station.user) // Relies on station object having associated user added by front end

                let jsonObj = JSON.parse(json);

                // Get the sequence number and verify that it is one plus the previous one
                if (parseInt(jsonObj.h) === station.seqnum) {
                    user.status = (jsonObj.s.b === 1);
                    user.save();
                    
                    // If the f flag is set to 1, we signal the test sent within 10 minutes to be successful
                    if (jsonObj.s.f !== undefined) {

                        const now = new Date().getTime();
                        let test = await Test.findOne({"stationid": station.stationid, "status": 0, "time" : {$gte: now - 600000}});
        
                        if (test !== undefined) {
                            test.status = 2;
                            test.save();
                        }
                    }
                    
                    // Signal that the accelerometer has moved to the admin
                    if (jsonObj.s.a === 0) {
                        const admin = await User.findById(station.admin);

                        const body = "User " + station.user + " connected to station " + station._id + " flagged for base station movement.";
                        sendPushNotification(admin.deviceToken, {"key": "3", "title": "Base Station Moved", "body": body});
                    } 
                    
                } else {
                    const admin = await User.findById(station.admin);

                    const body = "User " + station.user + " connected to station " + station._id + " flagged for base station tampering";
                    sendPushNotification(admin.deviceToken, {"key": "3", "title": "Base Station Tampered", "body": body});
                }
                station.seqnum = parseInt(jsonObj.h) + 1;
                station.save();
            }
            res.send("OK"); 
        } 
        res.send("ERROR"); 
    } catch (e) {
        res.send("ERROR" + e.message);
        console.error(e);
    }
});

/*
 *	GET request for a specific station.
 */
router.get("/:stationid", getStation, (req, res) => {
	res.json(res.station);
});

/*
 *	DELETE request to delete a station.
 */
router.delete("/:stationid", getStation, (req, res) => {
	res.station.remove();
	res.send("Deleted station");
});

/*
 *	POST request to create a station. 
 *  Takes in stationid, and creates the station.
 */
router.post("/create", async (req, res) => {
    try {
        const station = new Station({
            _id: req.body.stationid,
            user: "",
            baseCoords: [],
            seqnum: 0,
            admin: "60654257ab0eea000aadadcb"
        });

         // Only create station if does not exist, else return 400
		Station.countDocuments({ "_id": req.body.stationid }, async function (err, count) {

			if (count !== 0) {
				res.status(400).send("Station already exists");
			} else {
				await station.save()
                res.status(201).send("Station created");
			}

		});
    } catch (err) {
        res.status(400).send(err.message);
    }
});

/*
 *	UPDATE request on base station location
 */
router.put("/:stationid/location", async (req, res) => {
	try {
		let station = await Station.findById(req.params.stationid);
        
		if (req.body.coordinates != null) {
			station.baseCoords = req.body.coordinates;
        }

		await station.save();
		res.status(200).send("Successfully updated station location");
	} catch (err) {
		res.status(400).send(err.message);
	}
});

module.exports = router;
