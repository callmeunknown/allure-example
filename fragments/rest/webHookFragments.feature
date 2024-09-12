#language:en

Feature: Methods

  @fragment
  Scenario: Generate web hook address
    * create and send the request
      | method | full_url                    | headers                                            |
      | POST   | https://webhook.site/token/ | {"api-key":"15c1e8ca-bf1a-44ab-86fe-ace9c2bbad9a"} |
    * expected status code "201"
    * extract data from the response body into the context variable
      | uuidWebHook | $.uuid |


  @fragment
  Scenario: handle web hook url
    * create and send the request
      | method | full_url                                                      | headers                                            |
      | GET    | https://webhook.site/token/%{uuidWebHook}%/request/latest/raw | {"api-key":"15c1e8ca-bf1a-44ab-86fe-ace9c2bbad9a"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | dataFromWebHook | $.data |

  @fragment
  Scenario: Webhook sent after deal
    * create and send the request
      | method | path                                                                  | headers                                                                                            |
      | GET    | /webhook-notification-api/admin/notifications/trade/%{createdDealId}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{adminAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | webhookBody               | $..[?(@.userId == '%{currentMerchantId}%')].taskResults[0].requestBody                        |
      | webhookNotificationStatus | $..[?(@.userId == '%{currentMerchantId}%' && @.status == 'DONE')].notificationStatus          |
      | webhookResponseCode       | $..[?(@.userId == '%{currentMerchantId}%' && @.status == 'DONE')].taskResults[*].responseCode |