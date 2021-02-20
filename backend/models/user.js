const mongoose = require("mongoose");

//Defines the schema for a user
const userSchema = new mongoose.Schema({
	username: { 
        type: String,
    },
    password: { 
        type: String,
    },
	station: { 
        type: String,
    },
    lastLocation: {
        type: {
            type: String
        },
        coordinates: [Number]
    },
    locationMap: [{
        time: {
            type: Date
        },
        type: {
            type: String
        },
        coordinates: [Number]
    }]
}, { collection: "userDB" });

module.exports = mongoose.model("User", userSchema);
