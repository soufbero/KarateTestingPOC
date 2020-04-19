Feature: Testing a REST API with Karate

  Background:
    * def decodeRequest =
      """
      function(input) {
          var Base64 = Java.type('java.util.Base64');
          var decoded = Base64.getDecoder().decode(input);
          var String = Java.type('java.lang.String');
          return new String(decoded);
      }
      """


  Scenario: Testing AppA API 1
    Given url urlAppAapi1
    And request decodeRequest('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPEFwaU9uZVJlcXVlc3Q+CiAgICA8dXNlck5hbWU+c291ZmlhbmU8L3VzZXJOYW1lPgogICAgPHBhc3N3b3JkPnNvdWZwYXNzPC9wYXNzd29yZD4KPC9BcGlPbmVSZXF1ZXN0Pg==')
    And header Content-Type = 'application/xml'
    When method POST
    Then status 200





