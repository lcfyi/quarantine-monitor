const express = require("express");
const router = express.Router();
const Station = require("../models/station");

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
router.post("/", async (req, res) => {
    try {
        const station = new Station({
            _id: req.body.stationid,
            users: [],
            baseCoords: [],
            admin: "603ac255c4e167649cab494b"
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

/*
 *	GET request for users for a specific station.
 */
router.get("/:stationid/users", getStation, (req, res) => {
	res.json(res.station.users);
});

/*
 *	POST request for users for a specific station which accepts userid.
 */
router.post("/:stationid/users", async (req, res) => {
    try {
        let station = await Station.findById(req.params.stationid);
        if (!station.users.includes(req.body.userid)) {
            station.users.push(req.body.userid);
            await station.save();
        }
        res.status(201).send("Successfully added member");
    } catch (err) {
        res.status(400).send(err.message);
    }  
});

/*
 *	DELETE request for users for a specific station which accepts userid.
 */
router.delete("/:stationid/users", async (req, res) => {
    try {
        let station = await Station.findById(req.params.stationid);
        if (station.users.includes(req.body.userid)) {
            station.users.pull(req.body.userid);
            await station.save();
        }
        res.status(200).send("Successfully removed member");
    } catch (err) {
        res.status(400).send(err.message);
    }  
});

module.exports = router;
