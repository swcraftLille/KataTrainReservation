Feature: Train Reservation
    As a railway operator
    I want to reserve seats on trains
    So that customers can book their tickets online with optimal seat allocation

Background:
    Given the booking reference service is available
    And the train data service is available

Scenario: Reserve seats successfully when train has enough capacity
    Given a train "express_2000" with the following configuration:
      | Coach | Total Seats | Reserved Seats |
      | A     | 10          | 0              |
      | B     | 10          | 0              |
    When I request to reserve 3 seats on train "express_2000"
    Then the reservation should be successful
    And all 3 seats should be in the same coach
    And the reservation should have a booking reference
    And the train should be reserved with those seats
