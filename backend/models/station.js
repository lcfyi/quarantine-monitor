const mongoose = require("mongoose");

//Defines the schema for a station
const stationSchema = new mongoose.Schema({
	stationid: { 
        type: String,
    },
    users: [],
	station: { 
        type: String,
    },
    baseLocation: {
        type: {
            type: String
        },
        coordinates: [Number]
    }
}, { collection: "stationDB" });

module.exports = mongoose.model("Station", stationSchema);
