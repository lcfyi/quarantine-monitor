const mongoose = require("mongoose");

//Defines the schema for a user
const userSchema = new mongoose.Schema({
	username: { 
        type: String
    },
    password: { 
        type: String
    },
    salt: {
        type: String
    },
    deviceToken: {
        type: String
    },
	stationid: { 
        type: String
    },
    admin: {
        type: Boolean
    },
    status: {
        type: Boolean
    },
    lastCoords: [Number], //Change to last record
    locationMap: [{
        time: {
            type: Number
        },
        coordinates: [Number]
    }]
}, { collection: "userDB" });

module.exports = mongoose.model("User", userSchema);
