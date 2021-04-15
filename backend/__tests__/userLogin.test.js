const app = require("../index");
const User = require("../models/user");
const supertest = require('supertest');
const request = supertest(app);
const passwordHelper = require("../routes/users/passwordHelper");
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer;

describe('testing user login', () => {

  beforeAll(async () => {
    mongoServer = new MongoMemoryServer();
    const mongoUri = await mongoServer.getUri();
    await mongoose.connect(mongoUri, {useUnifiedTopology: true, useNewUrlParser: true}, (err) => {
        if (err) console.error(err);
    });
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should login with correct username, password, and valid token", async(done) => {
    
    let hashData = passwordHelper.saltHashPassword("pass");
    const mockUser = new User({
			username: "test_user",
			deviceToken: "test_token",
			password: hashData.passwordHash,
			salt: hashData.salt,
      stationid: "1",
      lastCoords: [0,0],
			startTime: 1,
			endTime: 2,
      locationMap: [],
			admin: false,
			status: true,
			availability: [0,60], 
			scheduledTests: [1,2]
		});

    let user1;
    await mockUser.save().then((user) => {user1 = user});

    let res = await request.post("/users/login")
      .send({"username": user1.username, "password": "pass", "token": user1.token})
      .expect(201);

    expect(res.body.admin).toBe(user1.admin);
    expect(res.body.endTime).toBe(user1.endTime);
    expect(res.body._id.toString()).toBe(user1._id.toString());

    done()
  });

  it("should 400 when login with incorrect password", async(done) => {
    // Relies on mock user being saved in the first suite
    let res = await request.post("/users/login")
    .send({"username": "test_user", "password": "incorrect_pass", "token": "test_token"})
    .expect(400);
    expect(res.text).toBe("Incorrect Password");
    
    done()
  });

  it("should 400 if no user exists for that email", async (done) => {
    // Relies on mock user being saved in the first suite
    let res = await request.post("/users/login")
      .send({"username": "fake_user", "password": "pass", "token": "test_token"})
      .expect(400);

    done();
  });

  it("should 400 user with invalid/missing required field", async (done) => {
    // Relies on mock user being saved in the first suite
    let res = await request.post("/users/login")
      .send({"username": "test_user", "token": "test_token"})
      .expect(400);

    done();
  });
});


