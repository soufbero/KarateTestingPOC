Feature: Testing a REST API with Karate

  Scenario: Testing AppA API 1
    Given url urlAppAapi1
    And request <?xml version="1.0" encoding="UTF-8"?><ApiOneRequest><userName>soufiane</userName><password>soufpass</password></ApiOneRequest>
    When method POST
    Then status 200




