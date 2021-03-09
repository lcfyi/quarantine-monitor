const sendPushNotification = require("./pushnotification");
const User = require("./models/user");
const Station = require("./models/station");
const Test = require("./models/test");

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
 *  TODO: handle time zones by accepting input
 */
async function handleTests() {
    try {
        
        // Iterate through all the remaining tests and flag them as incomplete (3)
		var tests = await Test.find({"status": 0});
        if (tests == null) {
            return;
        }

        for (var test of tests) {
            // TODO: use static variable
            test.status = 3;
            await test.save();
            
            const admin = await User.findById(test.adminid)
            // Alert admin of the test of failure
            //sendPushNotification(admin.deviceToken)
        }
            
        // Find all non-admin users who are currently flagged as following quarantine
        var users = await User.find({"admin": false, "status": true});
        if (users == null) {
            return;
        }
        for (var user of users) {
            
            const now = new Date();
            const currentSlot = now.getUTCHours * 6 + Math.floor(now.getUTCMinutes() / 10);
            
            if (user.availability[1] >= currentSlot) {

                // Once the max time of the range has been hit for each day, regenerate the random times
                user.scheduledTests = user.randomizedTimes();
                await user.save();
            } else if (user.scheduledTests.include(currentSlot)) {

                // If the current iteration is the test time, we create it
                const station = await Station.find({"stationid": res.user.stationid});
                const test = new Test({
                    "userid": str(user._id),
                    "stationid": user.stationid,
                    "time": now.getTime.toString(),
                    "status": 0,
                    "adminid": station.adminid
                });
                await test.save();

                // Send a push notification to user to trigger test
                // TODO: set high priority
                sendPushNotification(user.deviceToken, 
                    {"title": "QMonitor - Verify Identity", 
                     "body": "Please complete the facial verification test within 10 minutes"}, false);
            }
        }

	} catch (err) {
        console.log(err);
		return;
	}
}



module.exports = {randomizedTimes, handleTests};
