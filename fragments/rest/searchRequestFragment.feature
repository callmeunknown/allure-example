#language:en

Feature: Methods

  @fragment
  Scenario: Generate SRID
    * create and send the request
      | method | path             | body            |
      | POST   | /search-requests | createSRID.json |
    * expected status code "200"
    * extract data from the response body into the context variable
      | generatedSRID | $.id |
    * create and send the request
      | method | path                               |
      | GET    | /search-requests/%{generatedSRID}% |
    * expected status code "200"

  @fragment
  Scenario: Generate SRID extended
    * create and send the request
      | method | path             | body                    |
      | POST   | /search-requests | createSRIDextended.json |
    * expected status code "200"
    * extract data from the response body into the context variable
      | generatedSRID | $.id |
    * create and send the request
      | method | path                               |
      | GET    | /search-requests/%{generatedSRID}% |
    * expected status code "200"