const app = require("../index");
const Test = require("../models/testModel");
const supertest = require('supertest');
const request = supertest(app);
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer, mockTest;

describe('testing querying verification tests', () => {

  beforeAll(async () => {
    mongoServer = new MongoMemoryServer();
    const mongoUri = await mongoServer.getUri();
    await mongoose.connect(mongoUri, {useUnifiedTopology: true, useNewUrlParser: true}, (err) => {
        if (err) console.error(err);
    });

    const now = new Date();
    mockTest = new Test({
      userid: "test_user",
      stationid: "1",
	    time: now.getTime(),
      status: 0 
    });
    
    await mockTest.save();
  })


  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  it("should successfully query tests with empty query", async(done) => {

    let res = await request.get("/tests");

    expect(res.body.length).toBe(1);
    expect(res.body[0].userid).toBe(mockTest.userid);
    expect(res.body[0].stationid).toBe(mockTest.stationid);
    expect(res.body[0].status).toBe(mockTest.status);
    expect(res.body[0].time).toBe(mockTest.time);

    done()
  });

  it("should successfully query tests with userid, status", async(done) => {
    let res = await request.get("/tests?userid=test_user&status=0&range=5184");

    expect(res.body.length).toBe(1);
    expect(res.body[0].userid).toBe(mockTest.userid);
    expect(res.body[0].stationid).toBe(mockTest.stationid);
    expect(res.body[0].status).toBe(mockTest.status);
    expect(res.body[0].time).toBe(mockTest.time);

    done();
  });

  it("should 404 user with invalid/missing required field", async (done) => {
    // Relies on mock user being saved in the first suite
    let res = await request.get("/tests?userid=test_user&status=abcd")
      .expect(404);

    done();
  });
});


