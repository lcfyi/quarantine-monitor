const app = require("../index");
const User = require("../models/user");
const Station = require("../models/station");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer;

describe('testing user registration', () => {

  beforeAll(async () => {
    mongoServer = new MongoMemoryServer();
    const mongoUri = await mongoServer.getUri();
    await mongoose.connect(mongoUri, {}, (err) => {
        if (err) console.error(err);
    });
    
    let station = new Station({
        _id: "1",
        user: "",
        seqnum: 0,
        admin: "admin_uuid"
    })
    await station.save();
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should register new user with username, password, current location, availability, and valid station id", async(done) => {
    let res = await request.post("/users")
        .send({"username": "test_user", "password": "pass", "coordinates": [0,0], "availability": [0, 60], "stationid": "1", "token": "test_token"})
        .expect(201);

    expect(res.body.admin).toBe(false);
    expect(res.body.endTime).toBeGreaterThan(0);

    const user = await User.findOne({username: "test_user"});
    const station = await Station.findOne({_id: "1"});

    // Confirm that the user object contains all the valid keys
    expect(user.stationid).toBe("1");
    expect(res.body._id.toString()).toBe(user._id.toString());
    expect(user.lastCoords.toObject()).toStrictEqual([0,0]);
    expect(user.deviceToken).toBe("test_token");
    expect(user.startTime).toBeGreaterThan(0);
    expect(user.status).toBe(true);
    expect(user.availability.toObject()).toStrictEqual([0,60]);
    expect(user.scheduledTests).toBeDefined();
    expect(user.locationMap).toBeDefined();
    expect(user.salt).toBeDefined();  
    expect(user.password).toBeDefined();
    
    // Confirm that the created user is put into the corresponding station model
    expect(station.user.toString()).toBe(user._id.toString());
    done()
  });

  it("should 400 user with existing username", async(done) => {
    let res = await request.post("/users")
        .send({"username": "test_user", "password": "pass", "coordinates": [0,0], "availability": [0, 60], "stationid": "1", "token": "test_token"})
        .expect(400);
    // Relies on the user being created in the previous test suite
    expect(res.text).toBe("User already exists with that username");
    done()
  });

  it("should 400 user with invalid/missing required field", async (done) => {
    let res = await request.post("/users")
        .send({"username": "test_user", "coordinates": [0,0], "availability": [0, 60], "stationid": "1"})
        .expect(400);
    done();
  });
});


