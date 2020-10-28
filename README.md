# Toozer Examples
Toozer app that demonstrates example usages of Toozifier library.

### 1. Obtain Toozifier instance
The first step you need to do to use the Toozifier library is to obtain an instance of Toozifier object. You can do that by calling
```
val toozifier: Toozifier = ToozifierFactory.getInstance()
```

### 2. Implement RegistrationListener interface
Next you need to implement RegistrationListener which receives registration events from Toozifier:
```
private val registrationListener = object : RegistrationListener {

   override fun onDeregisterFailure(errorCause: ErrorCause) {
      Timber.e("Deregister failure: ${errorCause.description}")
   }

   override fun onDeregisterSuccess() {
      Timber.i("Deregister success")
   }

   override fun onRegisterFailure(errorCause: ErrorCause) {
      Timber.e("Register failure: ${errorCause.description}")
   }

   override fun onRegisterSuccess() {
      Timber.i("Register success")
      button_send_frame.isEnabled = true
   }
}
```

### 3. Register your app as a Toozer
To use Toozifier features you need to register your app as a Toozer:
```
toozifier.register(this, getString(R.string.app_name), registrationListener)
```
*NOTE: You need to pass as arguments: context, toozer name, and a registration listener that receives registration events*

### 4. Update card
If registration succeeds, you can use Toozifier features like: updating cards, recording the audio, or receiving sensor data.
To update card call:
```
toozifier.updateCard(promptView, frameView, Constants.FRAME_TIME_TO_LIVE_FOREVER)
```
*NOTE: You need to pass as arguments: prompt view, frame view, and time to live parameter*

### 5. Deregister Toozer when lifecycle of your component ends to avoid memory leaks
To deregister toozer, call:
```
toozifier.deregister()
```

## Card Example

The card example is a simple example of how to update the focus view in the glasses.

## Heartbeat-Example

The heartbeat-example comprises the "toozbeat-wear-app" and the examples app under the package "Heartbeat".
To try it out, do the following:

1. Install Tooz-Os on your phone and pair the glasses with the phone
2. Install the "Wear os App" on your phone and pair the watch with your phone
3. Install the Toozbeat-App on the watch and give it permission to read sensor data
4. Install the Example-App on the device and start the "Heartbeat"-Example, put on glasses and wait a moment.

## Sensor example

The sensor example shows the usage of the IMU. In the example the sensor data is evaluated to track head motion.

### Important note regarding reading sensor data

The IMU with its three integrated sensors accelerometer, gyroscope and magnetometer enables the measurement of relative movements of the hardware. Thus, the measured changes of direction and acceleration can be used as a trigger to display new information. A determination of the absolute position is not possible, since this requires a continuous recalibration of the sensors.