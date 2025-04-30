package com.github.strubium.smeaglebus.eventbus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * A simple event bus that allows creating event listeners,
 * posting events, and handling events in priority order.
 * <p>
 * If an event is canceled, the bus will stop processing it further.
 *
 * @author strubium
 */
public class SmeagleBus {
    /** Singleton instance of the SmeagleBus. */
    private static final SmeagleBus INSTANCE = new SmeagleBus();
    private static final int DEFAULT_PRIORITY = 5;
    private final Map<Class<?>, List<Listener<?>>> listeners = new ConcurrentHashMap<>();

    /**
     * Get the instance of SmeagleBus. You shouldn't need to make another.
     *
     * @return the instance of SmeagleBus
     */
    public static SmeagleBus getInstance() {
        return INSTANCE;
    }

    /**
     * Starts building a listener for a specific event type.
     *
     * @param eventType the event type to listen for
     * @param <T> the type of event
     * @return a {@link ListenerBuilder} for building and subscribing the listener
     */
    public <T> ListenerBuilder<T> listen(Class<T> eventType) {
        return new ListenerBuilder<>(this, eventType);
    }

    private <T> void listen(Class<T> eventType, EventListener<T> listener, int priority) {
        // Common registration logic
        listeners
                .computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(new Listener<>(listener, priority));

        // Sort so higher priority comes first
        listeners.get(eventType)
                .sort(Comparator.comparingInt(l -> -l.priority));
    }


    /**
     * Posts an event to all listeners for that event type.
     * <p>
     * Listeners are called in priority order. If an event is cancelable and is marked as canceled,
     * the event processing stops for subsequent listeners.
     *
     * @param event the event to post
     */
    public void post(Object event) {
        List<Listener<?>> list = listeners.get(event.getClass());
        if (list == null) return;

        for (Listener<?> wrapper : list) {
            @SuppressWarnings("unchecked")
            Listener<Object> casted = (Listener<Object>) wrapper;
            casted.listener.onEvent(event);

            if (event instanceof CancelableEvent) {
                CancelableEvent cancelable = (CancelableEvent) event;
                if (cancelable.isCanceled()) {
                    break;
                }
            }
        }
    }

    /**
     * Interface that defines the listener for events.
     *
     * @param <T> the type of event
     */
    public interface EventListener<T> {
        /**
         * Handles an event.
         *
         * @param event the event to handle
         */
        void onEvent(T event);
    }

    /**
     * Internal class that wraps a listener with its priority.
     *
     * @param <T> the type of event this listener handles
     */
    private static class Listener<T> {
        final EventListener<T> listener;
        final int priority;

        Listener(EventListener<T> listener, int priority) {
            this.listener = listener;
            this.priority = priority;
        }
    }

    /**
     * Builder class for subscribing event listeners.
     * <p>
     * Use this to subscribe to a listener with a specific priority.
     *
     * @param <T> the type of event to listen for
     */
    public static class ListenerBuilder<T> {
        private final SmeagleBus bus;
        private final Class<T> eventType;
        private int priority = DEFAULT_PRIORITY;


        /**
         * Constructs a ListenerBuilder for the given event type.
         *
         * @param bus the event bus to register with
         * @param eventType the event type to listen for
         */
        public ListenerBuilder(SmeagleBus bus, Class<T> eventType) {
            this.bus = bus;
            this.eventType = eventType;
        }


        /**
         * Sets the priority for the listener.
         *
         * @param priority the priority to set
         * @return this builder instance for chaining
         */
        public ListenerBuilder<T> priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Subscribes the listener to the event bus.
         *
         * @param listener the listener to subscribe
         */
        public void subscribe(EventListener<T> listener) {
            bus.listen(eventType, listener, priority);
        }
    }
}



    /*
    bus.listen(FooEvent.class)      // start building
   .priority(10)                // override the default
   .subscribe(event -> {        // subscribe your listener
     System.out.println("Also high-priority via builder");
   });
    */

