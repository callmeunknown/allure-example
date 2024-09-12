#language:en

Feature: Methods

  @fragment
  Scenario: Enable trader
    * create and send the request
      | method | path                  | headers                                                                                             |
      | POST   | /user-api/user/online | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Disable trader
    * create and send the request
      | method | path                   | headers                                                                                             |
      | POST   | /user-api/user/offline | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Check trader balance by trader
    * create and send the request
      | method | path                       | headers                                                                                             |
      | GET    | /trade-api/virtual-wallets | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Check brand balance by admin
    * create and send the request
      | method | path                                                                 | headers                                                                                            |
      | GET    | /trade-api/admin/brands?limit=25&offset=0&brandId=%{currentBrandId}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
