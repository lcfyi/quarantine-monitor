const mongoose = require("mongoose");

//Defines the schema for a station
const stationSchema = new mongoose.Schema({
    _id: String,
    users: [],
    baseCoords: [Number]
}, { collection: "stationDB" });

module.exports = mongoose.model("Station", stationSchema);
