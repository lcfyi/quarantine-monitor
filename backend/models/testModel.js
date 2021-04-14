const mongoose = require("mongoose");

//Defines the schema for a test
const testSchema = new mongoose.Schema({
	userid: { 
        type: String
    },
    stationid: {
        type: String
    } ,
	time: { 
        type: Number
    },
    status: {
        type: Number
    },
    adminid: {
        type: String
    }
}, { collection: "testDB" });

module.exports = mongoose.model("Test", testSchema);
