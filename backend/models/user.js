const mongoose = require("mongoose");

//Defines the schema for a user
const userSchema = new mongoose.Schema({
	username: { 
        type: String,
    },
    password: { 
        type: String,
    },
    salt: {
        type: String
    },
    deviceToken: {
        type: String
    },
	stationid: { 
        type: String,
    },
    lastCoords: [Number],
    locationMap: [{
        time: {
            type: Number
        },
        coordinates: [Number]
    }]
}, { collection: "userDB" });

module.exports = mongoose.model("User", userSchema);
