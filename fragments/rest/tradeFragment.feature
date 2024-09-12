#language:en

Feature: Methods

  @fragment
  Scenario: Prepare the trader for the trade
    * insert fragment "Enable automation"
    * insert fragment "Enable offer"
    * insert fragment "Enable requisites"
    * insert fragment "Enable trader"

  @fragment
  Scenario: Disable requisites and trader
    * insert fragment "Disable requisites"


  @fragment
  Scenario: Enable offer
    * create and send the request
      | method | path                                        | headers                                                                                             |
      | POST   | /trade-api/offers/%{currentOfferId}%/enable | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  Scenario: Disable offer
    * create and send the request
      | method | path                                         | headers                                                                                             |
      | POST   | /trade-api/offers/%{currentOfferId}%/disable | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Enable requisites
    * create and send the request
      | method | path                                                 | headers                                                                                             |
      | POST   | /trade-api/requisites/%{currentRequisitesId}%/enable | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Disable requisites
    * create and send the request
      | method | path                                                  | headers                                                                                             |
      | POST   | /trade-api/requisites/%{currentRequisitesId}%/disable | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Start deal
    * wait 3 seс
    * create and send the request
      | method | path                                    | body           | headers                                                                                             |
      | POST   | /trade-api/widget/search-requests/start | startDeal.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{clientAccessToken}%"} |
    * expected status code "201"
    * extract data from the response body into the context variable
      | createdDealId | $.id |

  @fragment
  Scenario: Get tradeId by SRID
    * create and send the request
      | method | path                               | headers                                                                                             |
      | GET    | /search-requests/%{generatedSRID}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{clientAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | createdDealId | $.tradeId |

  @fragment
  Scenario: Confirm payment by client
    * create and send the request
      | method | path                                     | headers                                                                                             |
      | POST   | /trade-api/trades/%{createdDealId}%/next | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{clientAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | createdDealId | $.id |

  @fragment
  Scenario: Cancel deal by client
    * create and send the request
      | method | path                                       | headers                                                                                             |
      | POST   | /trade-api/trades/%{createdDealId}%/cancel | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{clientAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Create canceled deal by client
    * insert fragment "Generate SRID"
    * insert fragment "Start deal"
    * insert fragment "Cancel deal by client"

  @fragment
  Scenario: Dispute deal by trader
    * wait 3 seс
    * create and send the request
      | method | path                                              | headers                                                                                             |
      | POST   | /trade-api/trades/%{createdDealId}%/apply/DISPUTE | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete deal by trader
    * wait 3 seс
    * create and send the request
      | method | path                                           | body                                                   | headers                                                                                             |
      | POST   | /trade-api/trades/%{createdDealId}%/apply/DONE | {"payload": {"acceptAmount": "%{currentSellAmount}%"}} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete deal by admin
    * wait 3 seс
    * create and send the request
      | method | path                                                   | body                                                   | headers                                                                                            |
      | POST   | /trade-api/admin/trades/%{createdDealId}%/resolve-done | {"payload": {"acceptAmount": "%{currentSellAmount}%"}} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Cancel deal by admin
    * wait 3 seс
    * create and send the request
      | method | path                                                       | headers                                                                                            |
      | POST   | /trade-api/admin/trades/%{createdDealId}%/resolve-rejected | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Get deal info by admin
    * create the request
      | method | path                    | headers                                                                                            |
      | GET    | /trade-api/admin/trades | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * add query parameters
      | limit  | 25                |
      | offset | 0                 |
      | id     | %{createdDealId}% |
    * send the request
    * expected status code "200"
    * extract data from the response body into the context variable
      | dealSellAmount               | $.records[*].tradeSellAmount               |
      | dealSellAmountWithCommission | $.records[*].tradeSellAmountWithCommission |
      | dealCommissionAmount         | $.records[*].commissionAmount              |
      | dealExchangeRate             | $.records[*].exchangeRate                  |
      | dealExchangeRateDeltaPercent | $.records[*].exchangeRateDeltaPercent      |
      | dealExchangeRateMarket       | $.records[*].exchangeRateMarket            |
      | dealExtraCommission          | $.records[*].extraCommission               |
      | dealMarketAmount             | $.records[*].marketAmount                  |
      | dealMarketExchangeRate       | $.records[*].marketExchangeRate            |
      | dealMerchantAmount           | $.records[*].merchantAmount                |
      | dealReferenceExchangeRate    | $.records[*].referenceExchangeRate         |
      | dealReferenceAmount          | $.records[*].referenceAmount               |
      | dealAmountWithCommission     | $.records[*].tradeAmountWithCommission     |
      | dealTraderAmount             | $.records[*].traderAmount                  |
      | dealTraderCommission         | $.records[*].traderCommission              |
      | dealTraderSpread             | $.records[*].traderSpread                  |

  @fragment
  Scenario: Make deal copy by admin
    * create and send the request
      | method | path                                                | body                                                                                                                              | headers                                                                                            |
      | POST   | /trade-api/admin/trades/%{createdDealId}%/make-copy | {"amount":%{currentSellAmount}%,"closeReason":"DUE_TO_CLIENT_OR_TRADER","receiptIds":[],"requisitesId":"%{currentRequisitesId}%"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | copyDealId                       | $.trade.id                            |
      | copyDealSellAmount               | $.trade.tradeSellAmount               |
      | copyDealSellAmountWithCommission | $.trade.tradeSellAmountWithCommission |
      | copyDealCommissionAmount         | $.trade.commissionAmount              |
      | copyDealExchangeRate             | $.trade.exchangeRate                  |
      | copyDealExchangeRateDeltaPercent | $.trade.exchangeRateDeltaPercent      |
      | copyDealExchangeRateMarket       | $.trade.exchangeRateMarket            |
      | copyDealExtraCommission          | $.trade.extraCommission               |
      | copyDealMarketAmount             | $.trade.marketAmount                  |
      | copyDealMarketExchangeRate       | $.trade.marketExchangeRate            |
      | copyDealMerchantAmount           | $.trade.merchantAmount                |
      | copyDealReferenceExchangeRate    | $.trade.referenceExchangeRate         |
      | copyDealReferenceAmount          | $.trade.referenceAmount               |
      | copyDealAmountWithCommission     | $.trade.tradeAmountWithCommission     |
      | copyDealTraderAmount             | $.trade.traderAmount                  |
      | copyDealTraderCommission         | $.trade.traderCommission              |
      | copyDealTraderSpread             | $.trade.traderSpread                  |