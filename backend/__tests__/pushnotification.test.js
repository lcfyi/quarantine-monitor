const app = require("../index");
const User = require("../models/user");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');
const admin = require("firebase-admin");

let mongoServer, mockUser, firebaseMock;

describe('testing formatting push notification', () => {

  beforeAll(async () => {
    mongoServer = new MongoMemoryServer();
    const mongoUri = await mongoServer.getUri();
    await mongoose.connect(mongoUri, {useUnifiedTopology: true, useNewUrlParser: true}, (err) => {
        if (err) console.error(err);
    });

    let user1 = new User({
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
    firebaseMock = jest.spyOn(admin, 'messaging').mockImplementation();
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should call firebase API to send notification", async(done) => {

    let res = await request.get("/users/" + mockUser._id + "/requestlocation");
    expect(firebaseMock).toHaveBeenCalledTimes(1);

    done()
  });
});


