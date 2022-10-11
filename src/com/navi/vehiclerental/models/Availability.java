package com.navi.vehiclerental.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class Availability {
    // If we say slot 1-3 is booked then bookedSlot[0] and bookedSlot[1] will be true.
    // So the size of bookedSlot array will be 23 (Since booking 24th hour slot doesn't make sense).
    private boolean[] bookedSlots;
}
