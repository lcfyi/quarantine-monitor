const debug = require("debug");
const logger = debug("db:error");
const express = require("express");
const app = express();
const bodyParser = require("body-parser");
const algorithm = require("./algorithm");

app.use(express.json());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Route to relevant files
const usersRouter = require("./routes/users/users");
const stationsRouter = require("./routes/stations");
const testsRouter = require("./routes/tests");

app.use("/users", usersRouter);
app.use("/stations", stationsRouter);
app.use("/tests", testsRouter);

// Default redirect
app.get('/', async function(req, res) {
    console.log(algorithm.randomizedTimes([0, 61]));
    algorithm.handleTests()
    res.send('Hello World!');
})

module.exports = app;
