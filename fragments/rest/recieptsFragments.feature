#language:en

Feature: Логи уведомлений по АЗ

  @fragment
  Scenario: Обработать уведомления по сумме
    * create the request
      | method | path                | headers                                                                                             |
      | GET    | /trade-api/receipts | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * add query parameters
      | status | NOT_RESOLVED |
    * send the request
    * expected status code "200"
    * extract data from the response body into the context variable
      | receiptId | $.records[?(@.amount == '%{currentSellAmount}%')].id |
    * create and send the request
      | method | path                                      | body                                  | headers                                                                                             |
      | POST   | /trade-api/receipts/%{receiptId}%/resolve | {"comment": "null","status": "OTHER"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"