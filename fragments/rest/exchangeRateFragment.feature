#language:en

Feature: Exchange rate check

  @fragment
  Scenario: Exchange rate check
    * create the request
      | method | path                     | headers                                                                                             |
      | GET    | /exchange-rate-api/rates | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * add query parameters
      | exchange | %{currentExchange}%             |
      | sell     | %{currentExchangeSellCurrency}% |
      | buy      | USDT                            |
    * send the request
    * expected status code "200"
    * extract data from the response body into the context variable
      | exchangeRate | $.exchangeRate |