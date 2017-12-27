Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England"

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"

  Scenario: Client country is not defined
     When client country is not defined
     Then group will be "Default"

  Scenario: Default
     When client country no in
     |ENG|
     |CHL|
     |RUS|
     Then group will be "Default"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     And deposit is null
     Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     And deposit < 1000
     Then group will be "Russia"

  Scenario: Default group will be chosen for RUS
     When client country is "RUS"
     When client language is not "eng"
     And deposit >= 1000
     Then group will be "RichRussia"

  Scenario: Experiment group will be chosen for CHL
     When client country is "RUS"
     When client language is "eng"
     Then group will be "EnRussia"
