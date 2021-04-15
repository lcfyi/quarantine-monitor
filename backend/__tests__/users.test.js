const app = require("../index");
const User = require("../models/user");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer, mockUser;

describe('testing user database queries', () => {

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
			status: true,
			availability: [0,60], 
			scheduledTests: [1,2]
		});

    await user1.save().then((user) => {mockUser = user});

    user1 = new User({
			username: "test_user2",
			deviceToken: "test_token2",
			password: "pass2",
			salt: "salt2",
      stationid: "2",
      lastCoords: [0,0],
			startTime: 1,
			endTime: new Date().getTime() - 40000,
      locationMap: [],
			admin: false,
			status: false,
			availability: [0,60], 
			scheduledTests: [1,2]
		});

    await user1.save();
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should get user information with valid id", async(done) => {

    let res = await request.get("/users/" + mockUser._id)

    expect(res.body.username).toBe(mockUser.username);
    expect(res.body.deviceToken).toBe(mockUser.deviceToken);
    expect(res.body.stationid).toBe(mockUser.stationid);
    expect(res.body.lastCoords).toBeDefined();
    expect(res.body.locationMap).toBeDefined();
    expect(res.body.scheduledTests).toBeDefined();
    expect(res.body.availability).toBeDefined();
    expect(res.body.admin).toBe(mockUser.admin);
    expect(res.body.status).toBe(mockUser.status);
    expect(res.body.startTime).toBe(mockUser.startTime);
    expect(res.body.endTime).toBe(mockUser.endTime);
    expect(res.body.salt).toBeUndefined();
    expect(res.body.password).toBeUndefined();

    done()
  });

  it("should 404 when trying to fetch user info with invalid id", async(done) => {
    let res = await request.get("/users/invalidid")
      .expect(404)
    expect(res.text.substring(0,10)).toBe("Error 404:");
    
    done()
  });

  it("should fetch active users and select _id, lastCoords, startTime, and endTime", async (done) => {
    let res = await request.get("/users/dummyid/active")
      .expect(200);

    // Should only receive the active user
    expect(res.body.length).toBe(1);
    expect(res.body[0]._id.toString()).toBe(mockUser._id.toString());
    expect(res.body[0].lastCoords).toStrictEqual(mockUser.lastCoords.toObject());
    expect(res.body[0].startTime).toBe(mockUser.startTime);
    expect(res.body[0].endTime).toBe(mockUser.endTime);
    done();
  });

  it("should 400 when deleting token with invalid user id", async(done) => {

    let res = await request.delete("/users/invalidid/devicetoken").expect(400);

    let user = await User.findOne({"_id": mockUser._id});
    expect(user.deviceToken).toBe(mockUser.deviceToken);

    done()
  });

  it("should delete device token stored for existing user id", async(done) => {

    let res = await request.delete("/users/" + mockUser._id + "/devicetoken").expect(200);
    expect(res.text).toBe("Successfully signed out");
    
    let user = await User.findOne({"_id": mockUser._id});
    expect(user.deviceToken).toBe("");

    done()
  });

  it("should fetch plotmap for exising user id", async(done) => {

    let res = await request.get("/users/" + mockUser._id + "/plotmap");

    expect(res.body.length).toBe(1);
    expect(res.body[0].coordinates).toStrictEqual([0,0]);
    expect(res.body[0].time).toBe(1);
    expect(res.body[0].status).toBe(true);

    done()
  });

  it("should 400 when trying to update fields of invalid user", async(done) => {

    await request.put("/users/invalidid")
      .send({"token": "new_token"})
      .expect(400);

    done()
  });

  it("should 400 when trying to update fields of invalid user", async(done) => {

    await request.put("/users/invalidid").send({"token": "new_token"}).expect(400);

    done()
  });

  it("should update fields stationid, coordinates, availability, endTime, and token of valid user", async(done) => {

    await request.put("/users/" + mockUser._id)
      .send({"stationid": "new_station", "coordinates": [1,1], "availability": [3,3], "endTime": 5, "token": "new_token"})
      .expect(200);

    user = await User.findOne({_id: mockUser._id});

    expect(user.stationid).toBe("new_station");
    expect(user.lastCoords.toObject()).toStrictEqual([1,1]);
    expect(user.availability.toObject()).toStrictEqual([3,3]);
    expect(user.endTime).toBe(5);
    expect(user.deviceToken).toBe("new_token");
    
    done()
  });

});


