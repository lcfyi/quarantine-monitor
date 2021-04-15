# Frontend Testing Documentation

Below is a list of manual tests to be done ideally upon every commit to the frontend, in order to make sure existing workflow is not altered or changed.

| Test Case | Steps | Expected Behavior |
| ------ | ------ | ------ |
| Full Signup Workflow for New Account | 1. Signup with a new username and password, input availability and base station id <br> 2. Complete bluetooth connection steps <br> 3. Register facial profile on device on facial verification page | SignUp page -> Bluetooth connection steps -> Facial verification -> Home Page |
| Close App and Open | 1. Close and dismiss the app. <br> 2. Open the app. | The app should keep you logged in, additionally should prompt you to bluetooth connect to the De1 again. |
| Signup Using Existing username | 1. Uninstall the app to get rid of the cookie. <br> 2. Open it again. <br> 3. Attempt to signup using the same credentials as before. | A toast message will appear informing the username exists already. |
| Signup Using invalid Waking hours | 1. Attempt to signup, fill out everything correctly except waking hours. <br> 2. Input "5:00" for start time and "3:00" for end time. | A toast message will appear informing the user that the end time cannot occur before the start time. |
| Login | 1. Attempt to login using the previous credentials. <br> 2. Complete bluetooth connection steps | App will take you to the home page. |
| Login using invalid credentials | 1. Attempt to login using random credentials | A toast message will appear notifying the user that the username or password was invalid. |
| Complete Facial Verification Test | 1. Open up the app <br> 2. Click on the facial verification page | App should recognize your face and take you back to the home page. |
| Attempt facial verification on the wrong face | 1. Open up the app <br> 2. Click on the facial verification page <br> 3. Point camera towards a face that is not yours | App should not recognize this face, and a blue square should appear detecting the face but not verifying it. |
