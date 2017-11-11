Feature: Select group

  Scenario: Experiment group will be chosen for CHL
    When client country is "CHL"
    Then group will be "Experiment"

  Scenario: Default group will be chosen for RUS
    When client country is "RUS"
    Then group will be "Default"

