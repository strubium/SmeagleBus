# SmeagleBus
SmeagleBus is a simple event bus implementation using Rxjava3. It allows you to easily register listeners, post events, and handles events in priority order. 
It supports cancelable events, so if an event is canceled, further processing  will stop.

## Installing

Add [Jitpack](https://www.jitpack.io/#strubium/JavaWindowManager) to your build.gradle file
```
    maven { url 'https://jitpack.io' }
```

Add SmeagleBus to your dependencies block

```
	        implementation 'com.github.strubium:SmeagleBus:1.0.0'
```

### Usage


#### Getting the Event Bus
You can get the SmeagleBus instance like this

```java
SmeagleBus bus = SmeagleBus.getInstance();
```

#### Registering Listeners
You can register a listener for an event type by using the listen() method. 
You can also specify a custom priority, the default is 5.


```java
bus.listen(MyEvent.class)
   .priority(10)  // Optional: Set a custom priority
   .subscribe(event -> {
       System.out.println("Handling event with priority 10");
   });
```

#### Posting Events
To post an event to the bus, simply use the post() method:
```java
bus.post(new MyEvent());
```

Events are just classes, and if you want them to be cancelable, have them extend CancelableEvent



