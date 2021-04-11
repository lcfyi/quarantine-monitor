const mongoose = require("mongoose");

//Defines the schema for a station
const stationSchema = new mongoose.Schema({
    _id: String,
    user: {
        type: String
    },
    baseCoords: [Number],
    seqnum: {
        type: Number
    },
    admin: {
        type: String
    }
}, { collection: "stationDB" });

module.exports = mongoose.model("Station", stationSchema);
