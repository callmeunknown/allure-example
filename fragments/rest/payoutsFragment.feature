#language:en

Feature: Methods

  @fragment
  Scenario: Generate payout
    * create and send the request
      | method | path                              | body              | headers                                                                                            |
      | POST   | /trade-api/payouts/debug/unsigned | createPayout.json | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | payoutId | $.id |

  @fragment
  Scenario: Generate payout extended
    * create and send the request
      | method | path                              | body                      | headers                                                                                            |
      | POST   | /trade-api/payouts/debug/unsigned | createPayoutExtended.json | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | payoutId | $.id |

  @fragment
  Scenario: Accept the payout by trader
    * create and send the request
      | method | path                                          | headers                                                                                             |
      | POST   | /trade-api/payouts/trader/%{payoutId}%/accept | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Process the payout by trader
    * create and send the request
      | method | path                                       | body                                                                                        | headers                                                                                             |
      | POST   | /trade-api/payouts/trader/%{payoutId}%/pay | {"attachments":["e01cf7a1-a8b5-41fd-a2f3-89e99f30cdf1"], "requisites":["4242000042420000"]} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Process the payout by admin
    * create and send the request
      | method | path                                      | headers                                                                                            |
      | POST   | /trade-api/admin/payouts/%{payoutId}%/pay | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "204"

  @fragment
  Scenario: Turn the payout into dispute by admin
    * create and send the request
      | method | path                                          | body                                                 | headers                                                                                            |
      | POST   | /trade-api/admin/payouts/%{payoutId}%/dispute | {"reason":"SUPPORT_FIAT_NOT_RECEIVED", "comment":""} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Cancel the payout by trader
    * create and send the request
      | method | path                                          | body                                      | headers                                                                                             |
      | POST   | /trade-api/payouts/trader/%{payoutId}%/cancel | {"reason":"TRADER_OTHER", "comment":null} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Cancel processed payout by trader
    * create and send the request
      | method | path                                          | body                                         | headers                                                                                             |
      | POST   | /trade-api/payouts/trader/%{payoutId}%/cancel | {"reason":"TRADER_REJECTED", "comment":null} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Cancel the payout by admin
    * create and send the request
      | method | path                                         | body                                                | headers                                                                                            |
      | POST   | /trade-api/admin/payouts/%{payoutId}%/cancel | {"reason":"ADMIN_WRONG_REQUISITES", "comment":null} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Resolve dispute to client by admin
    * create and send the request
      | method | path                                                | body           | headers                                                                                            |
      | POST   | trade-api/admin/payouts/%{payoutId}%/resolve/client | {"comment":""} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Resolve dispute to trader by admin
    * create and send the request
      | method | path                                                | body           | headers                                                                                            |
      | POST   | trade-api/admin/payouts/%{payoutId}%/resolve/trader | {"comment":""} | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Get payout info by admin
    * create and send the request
      | method | path                                                      | headers                                                                                            |
      | GET    | trade-api/admin/payouts?limit=25&offset=0&id=%{payoutId}% | {"Content-type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | payoutBuyAmount              | $.records[0].buyAmount              |
      | payoutCommissionAmount       | $.records[0].commission             |
      | payoutCryptoAmount           | $.records[0].cryptoAmount           |
      | payoutCryptoCurrencyId       | $.records[0].cryptoCurrency.id      |
      | payoutExchangeRate           | $.records[0].exchangeRate           |
      | payoutExternalPayoutId       | $.records[0].externalPayoutId       |
      | payoutExtraPayMethod         | $.records[0].extraPayMethod         |
      | payoutFiatAmount             | $.records[0].fiatAmount             |
      | payoutIsAutomatedAttachments | $.records[0].isAutomatedAttachments |
      | payoutMerchantAmount         | $.records[0].merchantAmount         |
      | payoutPayMethodId            | $.records[0].payMethod.id           |
      | payoutReason                 | $.records[0].reason                 |
      | payoutReasonComment          | $.records[0].reasonComment          |
      | payoutReferenceExchangeRate  | $.records[0].referenceExchangeRate  |
      | payoutRequisites             | $.records[0].requisites             |
      | payoutSearchRequestStatus    | $.records[0].searchRequestStatus    |
      | payoutStatus                 | $.records[0].status                 |
      | payoutTradeStatus            | $.records[0].tradeStatus            |
      | payoutTraderAmount           | $.records[0].traderAmount           |
      | payoutTraderRequisites       | $.records[0].traderRequisites       |
      | payoutTraderSpread           | $.records[0].traderSpread           |