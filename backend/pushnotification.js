const admin = require("firebase-admin");
const serviceAccount = require("./qmonitor-306302-firebase-adminsdk-jmcix-0745e8a0f9.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

// Helper function to send a message to a device. The silent variable determines whether to send
// the data as a silent payload or as a notification
function sendPushNotification(deviceToken, payload) {
    if (deviceToken === "") {
        throw "Invalid Token";
    }

    var message = { "token": deviceToken }
    message.data = payload;
    console.log(message);

    admin.messaging().send(message)
    .then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
    })
    .catch((error) => {
        console.log('Error sending message:', error);
    })
}

module.exports = sendPushNotification;
