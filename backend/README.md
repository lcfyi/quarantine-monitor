# Backend

The NodeJS application has deen deployed to a Google Cloud App Engine instance. It can be found at https://qmonitor-306302.wl.r.appspot.com/.

The database connected to the app is stored on a MongoDB Atlas cluster.

TODO: Set up Firebase and code to send notifications and requests to clients.
TODO: Test API with Jest
TODO: Develop main loop to submit / handle verification tests

## API
| Method | Description |
| ------ | ----------- |
| **POST /users** | Registers a user. Accepts keys `coordinates` (array), `username` (string), and `password` (string) in the body and returns the user id on success (201) and "User already exists with that username" (400) on failure.|
| **POST /users/login** | Accepts keys `deviceToken` (string), `username` (string), and `password` (string) in the body and returns the user id on success (201) and "Incorrect Password" (400) on failure.|
| **GET /users/:userid** | Accepts param `userid` returns the user object containing `username`, `deviceToken`, `stationid`, `lastCoords`, `locationMap` on success and 404 on failure.|
| **GET /users/:userid/plotmap** | Accepts param `userid` returns the user object containing `locationMap` on success and 404 on failure.|
| **GET /users/:userid/station** | Accepts param `userid` returns the station object associated with the current user containing `users`, and `baseCoords` on success and 404 on failure.|
| **DELETE /users/:userid/** | Deletes user with id `userid` and returns "Deleted User" on success and 404 on failure. |
| **PUT /users/:userid/** | Updates user with id `userid`. Takes in keys `coordinates` (array) OR `stationid` and returns 200 on success and 404 on failure.|
| **POST /stations** | Registers a station. Accepts key `stationid` in the body and returns "Station created" on success (201) and "Station already exists" (400) on failure.|
| **DELETE /stations/:stationid/** | Deletes station with id `stationid` and returns "Deleted Station" on success and 404 on failure. |
| **GET /stations/:stationid/** | Accepts param `stationid` and returns the station object containing keys `users`, and `baseCoords` on success and 404 on failure.|
| **PUT /stations/:stationid/location** | Updates the location of the base station with id `stationid`. Takes in key `coordinates` (array) and returns 200 on success and 404 on failure.|
| **GET /stations/:stationid/users** | Returns list of users connected to station with id `stationid` on success and 404 on failure.|
| **POST /stations/:stationid/users** | Adds a user to the list of users connected to station with id `stationid`. Takes in key `userid` in the body and returns "Successfully added member" (201) on success and 400 on failure.|
| **DELETE /stations/:stationid/users** | Removes a user from the list of users connected to station with id `stationid`. Takes in key `userid` in the body and returns "Successfully removed member" (200) on success and 400 on failure.|

Adding more which I haven't documented yet later...