
import com.github.strubium.smeaglebus.eventbus.CancelableEvent;
import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SmeagleBusTest {

    @Test
    void testCancelableEventStopsLowerPriorityListeners() {
        SmeagleBus bus = SmeagleBus.getInstance();

        AtomicReference<String> output = new AtomicReference<>("");
        AtomicBoolean lowPriorityCalled = new AtomicBoolean(false);

        // High priority listener (runs first)
        bus.listen(FooEvent.class)
                .priority(10)
                .subscribe(event -> output.set("High: " + event.getMessage()));

        // Mid priority cancels
        bus.listen(FooEvent.class)
                .priority(6)
                .subscribe(event -> {
                    output.set(output.get() + " | Mid: Canceling");
                    event.setCanceled(true);
                });

        // Low priority shouldn't run
        bus.listen(FooEvent.class)
                .priority(2)
                .subscribe(event -> lowPriorityCalled.set(true));

        // Post the event
        bus.post(new FooEvent("Test Message"));

        assertEquals("High: Test Message | Mid: Canceling", output.get());
        assertFalse(lowPriorityCalled.get(), "Low-priority listener should not have been called due to cancellation.");
    }

    // Test event class with cancelable support
    static class FooEvent extends CancelableEvent {
        private final String message;

        FooEvent(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }
}
