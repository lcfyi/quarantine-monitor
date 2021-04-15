const app = require("../index");
const User = require("../models/user");
const Station = require("../models/station");
const Test = require("../models/testModel");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const crypto = require("crypto");
const { MongoMemoryServer } = require('mongodb-memory-server');
jest.mock("../pushnotification");
const pushNotification = require("../pushnotification");

let mongoServer, mockUser, mockStation, mockTest, adminUser;

describe('testing station queries with de1', () => {

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

    const now = new Date();
    test1 = new Test({
      userid: mockUser._id,
      stationid: "1",
	    time: now.getTime(),
      status: 0 
    });

    await test1.save().then((test) => {mockTest = test});
  });

  beforeEach(async () => {
    const hashMock = {
      update: jest.fn().mockReturnThis(),
      digest: jest.fn().mockReturnValueOnce("checksum")
    };
    createHashMock = jest.spyOn(crypto, 'createHash').mockImplementationOnce(() => hashMock);

    pushNotification.mockImplementation();
    jest.clearAllMocks();
  })

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should return ERROR with invalid checksum", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":0, \"s\":{\"a\":0, \"b\":0}};wrongchecksum")
      .set({"base": "1", "content-type": "text/plain"})

    expect(res.text).toBe("ERROR");

    done()
  });

  it("should return OK with valid checksum but missing base", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":0, \"s\":{\"a\":0, \"b\":0}};checksum")
      .set({"content-type": "text/plain"})

    expect(res.text).toBe("OK");

    done()
  });

  it("should return ERROR with valid checksum, but invalid base", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":0, \"s\":{\"a\":0, \"b\":0}};checksum")
      .set({"content-type": "text/plain", "base": "100"})

    expect(res.text.includes("ERROR")).toBeTruthy();

    done()
  });

  
  it("should return OK and send tampered notif with valid checksum, base but invalid seqnum", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":-1, \"s\":{\"a\":1, \"b\":1}};checksum")
      .set({"content-type": "text/plain", "base": "1"})

    user = await User.findOne({_id: mockUser._id});
    expect(user.status).toBeFalsy();

    expect(pushNotification).toHaveBeenCalledTimes(1);
    expect(pushNotification.mock.calls[0][0]).toBe("admin_token");
    expect(pushNotification.mock.calls[0][1].title).toBe("Base Station Tampered");

    expect(res.text).toBe("OK");

    done()
  });

  it("should return OK with valid checksum, base, and seqnum given", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":0, \"s\":{\"a\":1, \"b\":1}};checksum")
      .set({"content-type": "text/plain", "base": "1"})

    user = await User.findOne({_id: mockUser._id});
    expect(user.status).toBeTruthy();

    expect(pushNotification).toHaveBeenCalledTimes(0);

    expect(res.text).toBe("OK");

    done()
  });

  it("should return OK on get", async(done) => {

    let res = await request.get("/stations/");

    expect(res.text).toBe("OK");

    done()
  });

  it("should return OK with valid checksum, base, and seqnum given and the b flag", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":1, \"s\":{\"a\":1, \"b\":0}};checksum")
      .set({"content-type": "text/plain", "base": "1"})

    user = await User.findOne({_id: mockUser._id});

    expect(pushNotification).toHaveBeenCalledTimes(1);
    expect(pushNotification.mock.calls[0][0]).toBe("admin_token");
    expect(pushNotification.mock.calls[0][1].title).toBe("Base Station Bluetooth Broken");

    expect(res.text).toBe("OK");

    done()
  });

  it("should return OK with valid checksum, base, and seqnum given and the a flag", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":2, \"s\":{\"a\":0, \"b\":1}};checksum")
      .set({"content-type": "text/plain", "base": "1"})

    user = await User.findOne({_id: mockUser._id});

    expect(pushNotification).toHaveBeenCalledTimes(1);
    expect(pushNotification.mock.calls[0][0]).toBe("admin_token");
    expect(pushNotification.mock.calls[0][1].title).toBe("Base Station Moved");

    expect(res.text).toBe("OK");

    done()
  });

  it("should return OK with f flag set and update test", async(done) => {

    let res = await request.post("/stations/")
      .send("{\"h\":3, \"s\":{\"a\":1, \"b\":1, \"f\":1}};checksum")
      .set({"content-type": "text/plain", "base": "1"})

    user = await User.findOne({_id: mockUser._id});
    expect(user.status).toBeTruthy();

    expect(pushNotification).toHaveBeenCalledTimes(0);

    test = await Test.findOne({_id: mockTest._id});
    expect(test.status).toBe(1)

    expect(res.text).toBe("OK");

    done()
  });
});


