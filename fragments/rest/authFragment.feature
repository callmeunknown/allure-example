#language:en

Feature: Methods

  @fragment
  Scenario: Login as client
    * create and send the request
      | method | path           | body                                                                                               |
      | POST   | /no-auth-login | {"parentUserId": "%{currentMerchantId}%","role": "ROLE_CLIENT","username":"%{currentClientName}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | clientAccessToken    | $.accessToken    |
      | clientRefreshToken   | $.refreshToken   |
      | clientWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as administrator
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/8d6aa7f0-8aa4-4b99-a45e-d546705aedbb | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | adminAccessToken    | $.accessToken    |
      | adminRefreshToken   | $.refreshToken   |
      | adminWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as trader
    * create and send the request
      | method | path                            | headers                             |
      | GET    | user-token/token/<traderUUID> | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | traderAccessToken    | $.accessToken    |
      | traderRefreshToken   | $.refreshToken   |
      | traderWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as support
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/e64faffe-eb86-4747-b18a-0be88010fe8d | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | supportAccessToken    | $.accessToken    |
      | supportRefreshToken   | $.refreshToken   |
      | supportWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as support 2nd
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/6e4bfd3e-43da-4cc5-855f-a39f0312cbc8 | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | supportSecondLineAccessToken    | $.accessToken    |
      | supportSecondLineRefreshToken   | $.refreshToken   |
      | supportSecondLineWebSocketToken | $.webSocketToken |


  @fragment
  Scenario: Login as supervisor
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/5ddb0f94-8559-46f7-a0a7-e1a979b003b7 | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | supervisorAccessToken    | $.accessToken    |
      | supervisorRefreshToken   | $.refreshToken   |
      | supervisorWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as branduser
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/745a94e9-8d52-4e56-b4f0-996abf7a63d5 | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | brandUserAccessToken    | $.accessToken    |
      | brandUserRefreshToken   | $.refreshToken   |
      | brandUserWebSocketToken | $.webSocketToken |

  @fragment
  Scenario: Login as branduser 2
    * create and send the request
      | method | path                                                  | headers                             |
      | GET    | user-token/token/546744b0-e100-4648-81e5-4e5ee510dcc5 | {"Content-type":"application/json"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | brandUser2AccessToken    | $.accessToken    |
      | brandUser2RefreshToken   | $.refreshToken   |
      | brandUser2WebSocketToken | $.webSocketToken |