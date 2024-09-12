#language:en

Feature: Methods

  @fragment
  Scenario: Create requisites by trader
    * create and send the request
      | method | path                  | body                                                                                                                                                                                                                                                                                                                                     | headers                                                                                             |
      | POST   | /trade-api/requisites | {"payMethodId": "%{currentPayMethodId}%","currencyId": "%{currentSellCurrencyId}%","countryId": "77b89f21-bc1a-46c9-847e-76ea5581f406","description": "%{currentDescription}%","requisites": "%{currentRequisites}%","limits": %{currentLimits}%,"status": "ENABLED","automationConfigurationId": null,"virtualPayMethodRequisites": []} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | currentRequisitesId | $.id |

  @fragment
  Scenario: Create requisites extended by trader
    * create and send the request
      | method | path                  | body                                                                                                                                                                                                                                                                                                                                                                                               | headers                                                                                             |
      | POST   | /trade-api/requisites | {"payMethodId": "%{currentPayMethodId}%","currencyId": "%{currentSellCurrencyId}%","countryId": "77b89f21-bc1a-46c9-847e-76ea5581f406","description": "%{currentDescription}%","requisites": "%{currentRequisites}%","limits": %{currentLimits}%,"status": "%{status}%","automationConfigurationId": %{automationConfigurationId}%,"virtualPayMethodRequisites": [%{virtualPayMethodRequisites}%]} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | currentRequisitesId | $.id |

  @fragment
  Scenario: Create offer by trader
    * create and send the request
      | method | path              | body                                                                                                                                                                                                                                                                                                                                 | headers                                                                                             |
      | POST   | /trade-api/offers | {"buyCurrencyId": "%{currentSellCurrencyId}%","countryId": "77b89f21-bc1a-46c9-847e-76ea5581f406","innerUUID": "%{currentPayMethodId}%#%{currentSellCurrencyId}%","payMethodId": "%{currentPayMethodId}%","sellCurrencyId": "67b7a413-4deb-4153-93b4-7e30c7adc150","status": "ENABLED","requisitesIds": ["%{currentRequisitesId}%"]} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | currentOfferId | $.id |

  @fragment
  Scenario: Delete requisites by trader
    * create and send the request
      | method | path                                          | headers                                                                                             |
      | DELETE | /trade-api/requisites/%{currentRequisitesId}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Delete offer by trader
    * create and send the request
      | method | path                                 | headers                                                                                             |
      | DELETE | /trade-api/offers/%{currentOfferId}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "204"

  @fragment
  Scenario: Change requisites category by admin
    * create and send the request
      | method | path                                                                | body               | headers                                                                                            |
      | POST   | /trade-api/admin/requisites/%{currentRequisitesId}%/change-category | {"category": null} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "204"
