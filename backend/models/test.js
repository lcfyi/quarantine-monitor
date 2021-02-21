const mongoose = require("mongoose");

//Defines the schema for a test
const testSchema = new mongoose.Schema({
	userid: { 
        type: String,
    },
    stationid: {
        type: String,
    } ,
	time: { 
        type: Date,
    },
    status: {
        type: Number
    }
}, { collection: "testDB" });

module.exports = mongoose.model("Test", testSchema);
