# Toozer Example

Simple toozer that displays frames of different colors.

## Implementation

This application has only one activity, MainActivity. Upon its creation, we obtain a Toozifier instance.

```
private val toozifier = ToozifierFactory.getInstance()
```

We register this app as a toozer in MainActivity's `onCreate` lifecycle callback method.

```
toozifier.register(this, getString(R.string.app_name), registrationListener)
```

We can start sending frames if the registration succeeds.