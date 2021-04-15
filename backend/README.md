# Backend

The backend server is built as a NodeJS application that is responsible to act as a handler for sending and dealing with verification tests, collecting user locations, maintaing the database, and communicationg with users/admins through push notifications using Firebase.

The NodeJS application has deen deployed to a Google Cloud App Engine instance. It can be found at https://qmonitor-306302.wl.r.appspot.com/.
NOTE: the server has been disabled while nobody is using the app, however I can reenable quickly via a message.

The database connected to the app is stored on a MongoDB Atlas cluster. The backend API and algorithm has been comprehensively tested using Jest as shown in the screen shot below.

![Unit Tests Code Coverage](testcoverage.png)


## API
| Method | Description |
| ------ | ----------- |
| **POST /users** | Registers a user. Accepts keys `coordinates` (array), `username` (string), and `password` (string), and `availability` (array) and `stationid` (String) in the body and returns the user id plus some other fields on success (201) and "User already exists with that username" (400) on failure.|
| **POST /users/login** | Accepts keys `deviceToken` (string), `username` (string), and `password` (string) in the body and returns the user id AND admin on success (201) and "Incorrect Password" (400) on failure.|
| **GET /users/:userid** | Accepts param `userid` returns the user object containing `username`, `deviceToken`, `stationid`, `lastCoords`, `locationMap` on success and 404 on failure.|
| **GET /users/:userid/plotmap** | Accepts param `userid` returns the user object containing `locationMap` on success and 404 on failure.|
| **DELETE /users/:userid/** | Deletes user with id `userid` and returns "Deleted User" on success and 404 on failure. |
| **DELETE /users/:userid/devicetoken** | Signs out the user by deleting their deviceToken for user with id `userid` and returns "Successfully signed out" on success and 400 on failure. |
| **PUT /users/:userid/** | Updates user with id `userid`. Takes in keys `coordinates` (array) OR `stationid` OR `availability` (array) OR `endTime` (number) OR `token` (String) and returns 200 on success and 404 on failure.|
| **GET /users/:userid/active** | Accepts param `userid` returns the list of active users containing `id_`, `lastCoords`, `startTime`, and `endTime` on success and 404 on failure.|
| **GET /stations/** | Returns 200 "OK". Used in the handshaking process with the De1. |
| **POST /stations/** | Accepts input from the De1-SoC and performs actions accordingly. For more information, refer to the documentation in hardware/firmware.|
| **GET /tests** | Returns list of all tests in the database with 404 on failure. The status indicates Sent (0), Passed (1), and Incomplete(3). Accepts optional query parameters of `userid` and `status`. |
| **POST /stations/create** | Registers a station. Accepts key `stationid` in the body and returns "Station created" on success (201) and "Station already exists" (400) on failure. Only used by admins for development and demo purposes.|
| **GET /users/:userid/requestlocation** | Accepts param `userid` and sends a silent notification to request that user's location. Only used by admins for development and demo purposes.|
| **GET /users/:userid/sendtest** | Accepts param `userid` and sends a push notification to request that user to complete a facial verification test. Only used by admins for development and demo purposes.|
| **GET /users/:userid/sendtest2** | Accepts param `userid` and flags all incomplete tests and signals the admin. Only used by admins for development and demo purposes.|



