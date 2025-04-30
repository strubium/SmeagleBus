package com.github.strubium.smeaglebus.eventbus;

/**
 * Represents an event that can be canceled.
 * <p>
 * Events that extend or use this class can be marked as canceled by listeners,
 * which will prevent further processing of the event by lower-priority listeners.
 */
public class CancelableEvent {
    /** Indicates whether the event has been canceled. */
    private boolean canceled = false;

    /**
     * Returns whether the event has been canceled.
     *
     * @return {@code true} if the event is canceled; {@code false} otherwise.
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Sets the canceled status of this event.
     *
     * @param canceled {@code true} to mark the event as canceled; {@code false} to allow it to continue.
     */
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
