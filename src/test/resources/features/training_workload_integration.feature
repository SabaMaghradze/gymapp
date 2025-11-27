Feature: Training Service to Workload Service integration

  Scenario: Add valid training updates workload in DB
    Given Workload Mongo DB is empty
    When I POST valid training to Training Service
    Then Workload Mongo DB should contain the trainer with correct duration