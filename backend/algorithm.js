const sendPushNotification = require("./pushnotification");
const User = require("./models/user");
const Station = require("./models/station");
const Test = require("./models/testModel");

const TEST_STATUS = {"SENT": 0, "INCOMPLETE": 3};
const NOTIF_TYPE = {"REQUEST_LOCATION": "0", "VERIFY_IDENTITY": "1", "ALERT_ADMIN": "2"};

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
 *  The following algorithm will run 10 minutes
 *  NOTE: For the sake of the project demo this has been disabled in place of test endpoints
 */
async function handleTests(test, userid) {
    try {
        // Iterate through all the remaining tests and flag them as incomplete (3)
		var tests = await Test.find({"status": TEST_STATUS.SENT});

        for (var test of tests) {
            test.status = TEST_STATUS.INCOMPLETE;
            await test.save();
            
            console.log(test.adminid);
            const admin = await User.findById(test.adminid)
            // Alert admin of the test of failure
            const body = "User " + test.userid.substring(0,8) + " connected to station " + test.stationid + " failed to complete a verification test. (Unix Time: " + test.time + ")";
            sendPushNotification(admin.deviceToken, {"key": NOTIF_TYPE.ALERT_ADMIN, "title": "Test Failure", "body": body});
        }
            
        // Find all non-admin users who are still supposed to be quarantined
        const now = new Date();
        var users = await User.find({"admin": false, "endTime": { $gt: now.getTime()}});
        if (users == null) {
            return;
        }
        for (var user of users) {
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
                const station = await Station.findById(user.stationid);
                console.log(station);
                const test = new Test({
                    "userid": user._id.toString(),
                    "stationid": user.stationid,
                    "time": now.getTime(),
                    "status": TEST_STATUS.SENT,
                    "adminid": station.admin
                });
                await test.save();

                // Send a push notification to user to trigger test
                sendPushNotification(user.deviceToken, 
                    {"key": NOTIF_TYPE.VERIFY_IDENTITY, 
                     "title": "Verify Identity", 
                     "body": "Please complete the facial verification test within 10 minutes"});
            }

            // Collect user's location every hour (or 6th iteration) or if they are flagged as having broken quarantine
            if (currentSlot % 6 == 0 || user.status == false) {
                sendPushNotification(user.deviceToken, {"key": NOTIF_TYPE.REQUEST_LOCATION});
            }
        }

	} catch (err) {
        console.log(err);
		return;
	}
}

module.exports = {randomizedTimes, handleTests};
