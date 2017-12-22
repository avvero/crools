Feature: Select group

  Scenario: Experiment group will be chosen for CHL
    When client country is "CHL"
    Then group will be "Chile"

  Scenario: Default group will be chosen for RUS
    When client country is "RUS"
    When client language is not "eng"
    Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
    When client country is "RUS"
    When client language is not "eng"
    And deposit more than 1000
    Then group will be "RichRussia"

  Scenario: Experiment group will be chosen for CHL
    When client country is "RUS"
    When client language is "eng"
    Then group will be "EnRussia"
