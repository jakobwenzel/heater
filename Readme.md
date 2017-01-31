Heater Controller
=================

This program makes my dumb old heater _smart_.

System architecture
-------------------

A servo motor is fastened to my heater using some metal scraps. Its axis is hot glued to the control knob. It is
controlled by a Raspberry Pi running the Python script in the `client` directory. The Client connects to
AWS IOT to receive control commands.

The Java server needs to be running somewhere on the web. Users can control the heater with a nice web interface.

Setting up the client
---------------------

* After setting up a device in AWS IOT, get your certificate as described [here](http://docs.aws.amazon.com/iot/latest/developerguide/create-device-certificate.html).
* Inside the `client` directory, create a file called `settings.py` with content like this:
```python
useWebsocket = False
host = "my-endpoint.iot.eu-central-1.amazonaws.com"
rootCAPath = "root-CA.crt"
certificatePath = "raspberrypi.cert.pem"
privateKeyPath = "raspberrypi.private.key"

servoPin = 18
```
* Start the client:
```
sudo python iot.py
```
  Admin rights are needed for GPIO access.

Setting up the server
---------------------

* Use any of the ways documented in
[Default AWS Credentials](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default)
or [ClasspathPropertiesFileCredentialsProvider](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/ClasspathPropertiesFileCredentialsProvider.html)
to setup your AWS Credentials for the server
* in `src/main/resources/`, create a file called `settings.json` with content like the following:

```json
{
  "region": "eu-central-1",
  "endpoint":"my-endpoint.iot.eu-central-1.amazonaws.com",
  "hostname": "0.0.0.0",
  "port": 8080,
  "users":[
    {"username":"user1","password":"password1"},
    {"username":"user2","password":"password2"}
  ]
}
```
  * Passwords are currently not hashed. Use with caution! (Pull Requests are welcome :) )
  * There are no fine grained permissions. Once a user is logged in, they may do everything.
* Do one of the following:
  * Either run the Server with `./gradlew run`
  * Or create a folder with all jars plus a start script with `./gradlew installDist` (output is located in `build/install`)

* To remember users authorized by cookie, the server will create a file called `authorized.json` in its working directory, so make sure it's writable.

Contributing
------------

Open an issue, send a pull request or send me a mail. Anything goes!
