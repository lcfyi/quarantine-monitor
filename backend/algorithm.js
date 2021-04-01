const sendPushNotification = require("./pushnotification");
const User = require("./models/user");
const Station = require("./models/station");
const Test = require("./models/test");

const TEST_STATUS = {"SENT": 0, "INCOMPLETE": 3};

// Helper function to generate a random normal distribution in between min and max
// https://stackoverflow.com/questions/25582882/javascript-math-random-normal-distribution-gaussian-bell-curve
function randn(min, max) {
    let u = 0, v = 0;

    while(u === 0) u = Math.random() //Converting [0,1) to (0,1)
    while(v === 0) v = Math.random()
    let num = Math.sqrt( -2.0 * Math.log( u ) ) * Math.cos( 2.0 * Math.PI * v )
    
    num = num / 10.0 + 0.5 // Translate to 0 -> 1
    if (num > 1 || num < 0) 
      num = randn(min, max) // resample between 0 and 1 if out of range
    
    else{
      num *= max - min // Stretch to fill range
      num += min // offset to min
    }
    return Math.floor(num)
}

/*
 *	Given an array containing a start and end time in 
 *  10 min segments, and time zone this function generates 
 *  two random points that are normally distributed in 
 *  each half. It rounds to the lowest interval of 10 minutes.
 */
function randomizedTimes(availability){
    firstHalf = randn(availability[0], Math.floor((availability[1] - availability[0]) / 2))
    secondHalf = randn(Math.floor((availability[1] - availability[0]) / 2) + 1, availability[1])
    return [firstHalf, secondHalf];
}

/*
 *	Main looping algorithm to submit new tests and handle previous ones.
 *  Occasionally handle
 */
async function handleTests(test, userid) {
    try {
        // Iterate through all the remaining tests and flag them as incomplete (3)
		var tests = await Test.find({"status": TEST_STATUS.SENT});

        for (var test of tests) {
            test.status = TEST_STATUS.INCOMPLETE;
            await test.save();
            
            const admin = await User.findById(test.adminid)
            // Alert admin of the test of failure
            const body = "User " + test.userid + " connected to station " + test.stationid + " failed to complete a verification test. (Unix Time: " + test.time + ")";
            sendPushNotification(admin.deviceToken, {"title": "QMonitor - Test Failure", "body": body}, false);
        }
            
        // Find all non-admin users who are currently flagged as following quarantine, and still supposed to be quarantined
        const now = new Date();
        var users = await User.find({"admin": false, "status": true, "endTime": { $gt: now.getTime()}});
        if (users == null) {
            return;
        }
        for (var user of users) {
            // TODO: remove test code
            if (user._id.toString() != userid) {
                continue;
            }
            const currentSlot = now.getUTCHours() * 6 + Math.floor(now.getUTCMinutes() / 10);
            console.log(currentSlot);
            
            if (user.availability[1] >= currentSlot && !test) {

                // Once the max time of the range has been hit for each day, regenerate the random times
                user.scheduledTests = user.randomizedTimes();
                await user.save();
            } else if ((user.scheduledTests.includes(currentSlot) || test)) {

                // If the current iteration is the test time, we create it
                const station = await Station.find({"stationid": user.stationid});
                const test = new Test({
                    "userid": user._id.toString(),
                    "stationid": user.stationid,
                    "time": now.getTime(),
                    "status": TEST_STATUS.SENT,
                    "adminid": station.adminid
                });
                await test.save();

                // Send a push notification to user to trigger test
                // TODO: set high priority?
                sendPushNotification(user.deviceToken, 
                    {"title": "QMonitor - Verify Identity", 
                     "body": "Please complete the facial verification test within 10 minutes"}, false);
            }

            // Collect user's location every hour (or 6th iteration)
            // TODO: in 1 minute loop, any signing tokens which are unmatched I collect the location every iteration for that user in
            //       addition wtih the regular location tracking here
            if (currentSlot % 6 == 0) {
                sendPushNotification(user.deviceToken, {"key": "Requesting location"}, true);
            }
        }

	} catch (err) {
        console.log(err);
		return;
	}
}

module.exports = {randomizedTimes, handleTests};
