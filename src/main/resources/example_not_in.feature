Feature: Select group

  Scenario: England
     When client country is "ENG"
     Then group will be "England"

  Scenario: Chile
     When client country is "CHL"
     Then group will be "Chile"

  Scenario: Default
     When client country no in
     |ENG|
     |CHL|
     Then group will be "Default"