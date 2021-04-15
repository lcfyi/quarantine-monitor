const app = require("../index");
const User = require("../models/user");
const Station = require("../models/station");
const Test = require("../models/testModel");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');
jest.mock("../pushnotification");
const pushNotification = require("../pushnotification");

let mongoServer, mockUser, mockStation, adminUser;

describe('testing handler to send and complete tests', () => {

  beforeAll(async () => {
    mongoServer = new MongoMemoryServer();
    const mongoUri = await mongoServer.getUri();
    await mongoose.connect(mongoUri, {useUnifiedTopology: true, useNewUrlParser: true}, (err) => {
        if (err) console.error(err);
    });

    let user1 = new User({
			username: "admin",
			deviceToken: "admin_token",
			password: "pass2",
			salt: "salt2",
      stationid: "",
      lastCoords: [],
			startTime: 1,
			endTime: 2,
      locationMap: [],
			admin: true,
			status: true,
			availability: [], 
			scheduledTests: []
		});

    await user1.save().then((user) => {adminUser = user});

    user1 = new User({
			username: "test_user",
			deviceToken: "test_token",
			password: "pass1",
			salt: "salt1",
      stationid: "1",
      lastCoords: [0,0],
			startTime: 1,
			endTime: new Date().getTime() + 1000000,
      locationMap: [{coordinates: [0,0], time: 1, status: true}],
			admin: false,
			status: false,
			availability: [0,60], 
			scheduledTests: [1,2]
		});

    await user1.save().then((user) => {mockUser = user});

    station1 = new Station({
      _id: "1",
      user: mockUser._id,
      seqnum: 0,
      admin: adminUser._id
    });

    await station1.save().then((station) => {mockStation = station});
  });

  beforeEach(async () => {
    pushNotification.mockImplementation();
    jest.clearAllMocks();
  })

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should submit test to the current user", async(done) => {

    await request.get("/users/" + mockUser._id + "/sendtest");

    expect(pushNotification).toHaveBeenCalledTimes(2);
    expect(pushNotification.mock.calls[0][0]).toBe("test_token");
    expect(pushNotification.mock.calls[0][1].title).toBe("Verify Identity");

    let test = await Test.findOne({"userid": mockUser._id});
    expect(test.stationid).toBe("1");
    expect(test.status).toBe(0);
    expect(test.adminid.toString()).toBe(adminUser._id.toString());

    done()
  });

  it("should signal current test as incomplete", async(done) => {

    await request.get("/users/" + mockUser._id + "/sendtest2");

    expect(pushNotification).toHaveBeenCalledTimes(1);
    expect(pushNotification.mock.calls[0][0]).toBe("admin_token");
    expect(pushNotification.mock.calls[0][1].title).toBe("Test Failure");

    let test = await Test.findOne({"userid": mockUser._id});
    expect(test.status).toBe(3);
    
    done()
  });

});


