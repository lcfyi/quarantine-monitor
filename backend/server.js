const debug = require("debug");
const logger = debug("db:error");
const config = require("./config");
const app = require("./index.js");
const mongoose = require("mongoose");

// Establish connection to the MongoDB database
mongoose.connect(config.DB_ADDR, {
    useNewUrlParser: false,
    useUnifiedTopology: false,
    user: config.DB_USER,
    pass: config.DB_PASS
});

var db = mongoose.connection;
db.on("error", (error) => logger(error));
db.once("open", () => logger("Database connected"));

port = config.PORT || 8080;
app.listen(port, () => logger("Server started"));
