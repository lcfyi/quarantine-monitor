const debug = require("debug");
const logger = debug("db:error");
const config = require("./config");
const app = require("./index.js");
const mongoose = require("mongoose");
const sendPushNotification = require("./pushnotification");

// Establish connection to the MongoDB database
mongoose.connect("mongodb+srv://admin:" + config.DB_PASS + "@qmonitor.ffncm.mongodb.net/qmonitor?retryWrites=true&w=majority", { useNewUrlParser: false, useUnifiedTopology: true });
var db = mongoose.connection;
db.on("error", (error) => logger(error));
db.once("open", () => logger("Database connected"));

port = config.PORT || 8080;
app.listen(port, () => logger("Server started"));
