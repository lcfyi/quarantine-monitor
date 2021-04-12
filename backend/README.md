# Backend

The NodeJS application has deen deployed to a Google Cloud App Engine instance. It can be found at https://qmonitor-306302.wl.r.appspot.com/.

The database connected to the app is stored on a MongoDB Atlas cluster.


## API
| Method | Description |
| ------ | ----------- |
| **POST /users** | Registers a user. Accepts keys `coordinates` (array), `username` (string), and `password` (string), and `availability` (array) and `stationid` (String) in the body and returns the user id plus some other fields on success (201) and "User already exists with that username" (400) on failure.|
| **POST /users/login** | Accepts keys `deviceToken` (string), `username` (string), and `password` (string) in the body and returns the user id AND admin on success (201) and "Incorrect Password" (400) on failure.|
| **GET /users/:userid** | Accepts param `userid` returns the user object containing `username`, `deviceToken`, `stationid`, `lastCoords`, `locationMap` on success and 404 on failure.|
| **GET /users/:userid/plotmap** | Accepts param `userid` returns the user object containing `locationMap` on success and 404 on failure.|
| **GET /users/:userid/station** | Accepts param `userid` returns the station object associated with the current user containing `users`, and `baseCoords` on success and 404 on failure.|
| **DELETE /users/:userid/** | Deletes user with id `userid` and returns "Deleted User" on success and 404 on failure. |
| **DELETE /users/:userid/devicetoken** | Signs out the user by deleting their deviceToken for user with id `userid` and returns "Successfully signed out" on success and 400 on failure. |
| **PUT /users/:userid/** | Updates user with id `userid`. Takes in keys `coordinates` (array) OR `stationid` OR `availability` (array) OR `endTime` (number) OR `token` (String) and returns 200 on success and 404 on failure.|
| **POST /stations/create** | Registers a station. Accepts key `stationid` in the body and returns "Station created" on success (201) and "Station already exists" (400) on failure. Only used by admins for development purposes.|
| **GET /stations/** | Returns 200 "OK". Used in the handshaking process with the De1. |
| **POST /stations/** | Accepts input from the De1-SoC and performs actions accordingly |
| **DELETE /stations/:stationid/** | Deletes station with id `stationid` and returns "Deleted Station" on success and 404 on failure. |
| **GET /stations/:stationid/** | Accepts param `stationid` and returns the station object containing keys `users`, and `baseCoords` on success and 404 on failure.|
| **PUT /stations/:stationid/location** | Updates the location of the base station with id `stationid`. Takes in key `coordinates` (array) and returns 200 on success and 404 on failure.|
| **GET /tests** | Returns list of all tests in the database with 404 on failure. The status indicates Sent (0), Passed (1), Failed (2), Incomplete(3). Accepts optional query parameters of `userid` and `status` and `range`. |
