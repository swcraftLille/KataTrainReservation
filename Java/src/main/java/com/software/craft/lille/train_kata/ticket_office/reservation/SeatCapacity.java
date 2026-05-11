package com.software.craft.lille.train_kata.ticket_office.reservation;

import static java.lang.Double.compare;

public record SeatCapacity(int requestedSeats, long bookedSeats, long totalSeats) {
  private static final double MAXIMUM_RESERVATION_THRESHOLD = 0.7d;

  public double availabilityThresholdAfterReservation() {
    return (double) (requestedSeats + bookedSeats) / totalSeats;
  }

  public boolean hasAvailableRequestedSeatsNumber() {
    return (totalSeats - bookedSeats) >= requestedSeats;
  }

  public boolean reachReservationThreshold() {
    return availabilityThresholdAfterReservation() >= MAXIMUM_RESERVATION_THRESHOLD;
  }

  public int higherCapacityAfterReservation(final SeatCapacity otherSeatCapacity) {
    double thisCapacityThresholdAfterReservation = availabilityThresholdAfterReservation();
    double otherCapacityThresholdAfterReservation =
        otherSeatCapacity.availabilityThresholdAfterReservation();
    boolean firstCapacityReachThreshold =
        thisCapacityThresholdAfterReservation > MAXIMUM_RESERVATION_THRESHOLD;
    boolean nextCapacityReachThreshold =
        otherCapacityThresholdAfterReservation > MAXIMUM_RESERVATION_THRESHOLD;
    if (firstCapacityReachThreshold != nextCapacityReachThreshold) {
      return firstCapacityReachThreshold ? -1 : 1;
    }
    int capacityComparison =
        compare(thisCapacityThresholdAfterReservation, otherCapacityThresholdAfterReservation);
    if (capacityComparison == 0) {
      return Long.compare(totalSeats(), otherSeatCapacity.totalSeats());
    }
    return capacityComparison;
  }
}
