#language:en

Feature: Methods

  @fragment
  Scenario: Create new automation configuration (Android)
    * create and send the request
      | method | path                                                                                 | body                                                                                                                                                                                                                                                                                                                                                                                 | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/681a6a02-989a-4b68-b29a-52bd6af6c16b | {"id": "TestConfiguration-%{count}%","app": "Raiffeisen","text": "+ %{currentSellAmount}%.00 ₽ от +79063497047, Даниил Александрович К. через СБП. Теперь на счете 373.88 ₽","header": "Пришел перевод на счет *1234","notification": "+ 96.00 ₽ от +79063497047, Даниил Александрович К. через СБП. Теперь на счете 373.88 ₽","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
    * wait 3 seс
    * create and send the request
      | method | path                                                   | headers                                                                                             |
      | GET    | /trade-api/automation-configurations?limit=25&offset=0 | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
    * extract data from the response body into the context variable
      | newConfigurationId | $.records[?(@.name == 'Android: TestConfiguration-%{count}%')].id |

  @fragment
  Scenario: Delete new automation configuration
    * create and send the request
      | method | path                                                        | headers                                                                                             |
      | DELETE | /trade-api/automation-configurations/%{newConfigurationId}% | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "204"

  @fragment
  Scenario: Enable automation
    * create and send the request
      | method | path                                                                | body                         |
      | POST   | /trader-automation-api/external/mobile/%{traderAutomationId}%/check | {"deviceId": "%{deviceId}%"} |
    * expected status code "200"

  @fragment
  Scenario: Imitate setting AZN course
    * create and send the request
      | method | path                                                                   | body                                                                                                             |
      | POST   | /exchange-rate-api/external/rates/768d4627-1e09-4673-8e73-63bbd724d67f | {"deviceId": null,"rate": %{currentAznRubCourse}%,"baseCurrency": "AZN","quoteCurrency": "RUB","meta": "string"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Tinkoff
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                              | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Пополнение, счет RUB. %{currentSellAmount}% RUB. Александр П.  Доступно 50000 RUB.","number": "Tinkoff"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Raiffeisen
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                             | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Raiffeisen","text": "+ %{currentSellAmount}%.00 ₽ от +79063497047, Даниил Александрович К. через СБП. Теперь на счете 373.88 ₽","header": "Пришел перевод на счет *%{bankAccount}%","notification": "+ 96.00 ₽ от +79063497047, Даниил Александрович К. через СБП. Теперь на счете 373.88 ₽","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Raiffeisen SMS
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                           | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Karta *%{bankAccount}%. Zachisleno %{currentSellAmount}% RUB. Balans 7700.00 RUB. 29.11.2022","number": "Raiffeisen"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal RaiffeisenUAH
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                     | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "14.08.23 14:00 Visa Reward Virtual*%{bankAccount}% zarakhuvannya koshtiv %{currentSellAmount}%.00 UAH RAIFFEISEN ONLINE UAH. Dostupna suma 8826.39 UAH","number": "Raiffeisen"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal CenterCreditBank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                 | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "BCC.KZ","text": "","header": "","notification": "Поступил перевод %{currentSellAmount}%.0 KZT на счёт KZ*%{bankAccount}%. Доступно 48083.02 KZT","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal CenterCreditBank-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                               | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "BCC.KZ","text": "","header": "","notification": "Поступил перевод %{currentSellAmount}% KZT на счёт KZ*%{bankAccount}%. Доступно 48083.02 KZT","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sense SuperApp
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                               | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Sense SuperApp","text": "Kartka %{bankAccount}% popovnennya +%{currentSellAmount}%.00UAH 22.05.23 15:46 vid kartky %{bankAccount}% cherez Perekaz z kartky na kartku, UKR Korysne tut: https://sense.top/info","header": "","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sense SuperApp-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                            | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Sense SuperApp","text": "Kartka %{bankAccount}% popovnennya +%{currentSellAmount}%UAH 22.05.23 15:46 vid kartky %{bankAccount}% cherez Perekaz z kartky na kartku, UKR Korysne tut: https://sense.top/info","header": "","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Rosbank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                                                                                            | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "РОСБАНК","text": "Счет *%{bankAccount}%. Перевод СБП из Тинькофф Банк от Кристина Анатольевна Т. успешный. Зачислено %{currentSellAmount}% р. Баланс 1064.13 р.","header": "ROSBANK Online","notification": "Счет *%{bankAccount}%. Перевод СБП из Тинькофф Банк от Кристина Анатольевна Т. успешный. Зачислено %{currentSellAmount}% р. Баланс 1064.13 р.","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sberbank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                    | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%", "app": "{not_app_name}", "text": "{not_text_big}", "header": "{not_title}", "notification": "{notification}", "sms_text": "Перевод из ЮниКредит Банк +%{currentSellAmount}%.00р от Алексей Ш. СЧЁТ%{bankAccount}% — Баланс: 15861.50р "Перевод в другой банк"", "number": "900"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sberbank PUSH
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                                                         | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "СберБанк","text": "Visa Classic%{bankAccount}% 20.09.23 зачислен перевод %{currentSellAmount}%р из Райффайзен Банк от Даниил Александрович К.","header": "Зачислен перевод","notification": "Visa Classic%{bankAccount}% 20.09.23 зачислен перевод %{currentSellAmount}%р из Райффайзен Банк от Даниил Александрович К.","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sberbank-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                 | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%", "app": "{not_app_name}", "text": "{not_text_big}", "header": "{not_title}", "notification": "{notification}", "sms_text": "Перевод из ЮниКредит Банк +%{currentSellAmount}%р от Алексей Ш. СЧЁТ%{bankAccount}% — Баланс: 15861.50р "Перевод в другой банк"", "number": "900"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sberbank-SBP
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                        | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%", "app": "{not_app_name}", "text": "{not_text_big}", "header": "{not_title}", "notification": "{notification}", "sms_text": "MIR-%{bankAccount}% 16:26 зачисление %{currentSellAmount}%р Альфа Банк Баланс: 251.01р", "number": "900"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sberbank-Sberbank SMS
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                            | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%", "app": "{not_app_name}", "text": "{not_text_big}", "header": "{not_title}", "notification": "{notification}", "sms_text": "MIR%{bankAccount}% 21:05 Перевод %{currentSellAmount}%р от Илья Юрьевич С.", "number": "900"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Ukrgazbank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                          | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Kartka #%{bankAccount}%/nPopovnennia na sumu: %{currentSellAmount}% UAH/n14.06.2023 11:57:07/nDostupno: %{currentSellAmount}% UAH/nKreditny limit: 0.00 UAH","number": "Ukrgasbank"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Ukrgazbank Telegram
    * create and send the request
      | method | path                                                                    | body                                                                                                                                                                             | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | {"message": "Popovnennia %{currentSellAmount}%.00 UAH %{bankAccount}% 11:26:43 Dostupno: 820.30 UAH Uvaga, zmina umov informuvanna https://bit.ly/2ZNm3lZ","from": "6601764744"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Ukrgazbank Telegram-Part
    * create and send the request
      | method | path                                                                    | body                                                                                                                                                                          | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | {"message": "Popovnennia %{currentSellAmount}% UAH %{bankAccount}% 11:26:43 Dostupno: 820.30 UAH Uvaga, zmina umov informuvanna https://bit.ly/2ZNm3lZ","from": "6601764744"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal KredoBank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                  | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "KredoBank","text": "","header": "","notification": "18.08.2023 21:55 ZARAKHUVANIA %{currentSellAmount}%.00UAH CARD**%{bankAccount}% ZALISHOK %{currentSellAmount}%.00 UAH OVER %{currentSellAmount}%.00 UAH DOSTUPNO %{currentSellAmount}%.00UAH","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal PUMB
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                         | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "ПУМБ Online","text": "%{currentSellAmount}%.00UAHA2C PUMB ONLINE MOB KYIV UA 22-05-2023 15:28 Картка: *%{bankAccount}% Доступно: 20234.33UAH","header": "Надходження","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal PUMB-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                      | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "ПУМБ Online","text": "%{currentSellAmount}%UAHA2C PUMB ONLINE MOB KYIV UA 22-05-2023 15:28 Картка: *%{bankAccount}% Доступно: 20234.33UAH","header": "Надходження","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal DemirBank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                           | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "19/07/2023 Vy poluchili perevod %{currentSellAmount}%.00 KGS.Tel+996321987654","number": "DemirBank"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal DemirBank-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                        | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "19/07/2023 Vy poluchili perevod %{currentSellAmount}% KGS.Tel+996321987654","number": "DemirBank"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal FedBank
    * create and send the request
      | method | path                                                                     | body                                                                                                                                                                                                                                                                                          | headers                                                                                             |
      | POST   | /trader-automation-proxy/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Dear Customer, Rs.%{currentSellAmount}%.00 credited to your A/c XX2314 on 15FEB2024 17:18:39. BAL-Rs.110.31-Federal Bank","number": "AX-FEDBNK","device": "device1"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal FedBank-Part
    * create and send the request
      | method | path                                                                     | body                                                                                                                                                                                                                                                                                       | headers                                                                                             |
      | POST   | /trader-automation-proxy/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Dear Customer, Rs.%{currentSellAmount}% credited to your A/c XX2314 on 15FEB2024 17:18:39. BAL-Rs.110.31-Federal Bank","number": "AX-FEDBNK","device": "device1"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal YesBank
    * create and send the request
      | method | path                                                                     | body                                                                                                                                                                                                                                                                                                              | headers                                                                                             |
      | POST   | /trader-automation-proxy/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Dear Merchant, you have received INR %{currentSellAmount}% from AMAN SONI towards UPI QR on 2024-01-04 at 20:21:08. /n/n Regards,/n YES BANK","number": "VK-YESBNK","device": "device1"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal HUMO
    * create and send the request
      | method | path                                                                    | body                 | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | humoWebhookBody.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal HUMO-Part
    * create and send the request
      | method | path                                                                    | body                     | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | humoWebhookBodyPart.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal UZCARD
    * create and send the request
      | method | path                                                                    | body                   | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | uzcardWebhookBody.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal UZCARD-Part
    * create and send the request
      | method | path                                                                    | body                       | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | uzcardWebhookBodyPart.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal M10
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                    | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "m10","text": "На ваш m10 поступило %{fiatSumAZN}% AZN","header": "Пополнение баланса m10","notification": "На ваш m10 поступило %{fiatSumAZN}% AZN","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Kredit Dnipro
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                                                                     | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "FreeBank","text": "Операція на +%{currentSellAmount}%.00₴Комісія: 0.00₴ABNK*DONYK NAZARII,Visa Direct18.09.23 16:16Картка %{bankAccount}%Баланс: 30.00₴","header": "FreeBank","notification": "Операція на +%{currentSellAmount}%.00₴Комісія: 0.00₴ABNK*DONYK NAZARII,Visa Direct18.09.23 16:16Картка %{bankAccount}%Баланс: 30.00₴","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Kredit Dnipro-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                                                               | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "FreeBank","text": "Операція на +%{currentSellAmount}%₴Комісія: 0.00₴ABNK*DONYK NAZARII,Visa Direct18.09.23 16:16Картка %{bankAccount}%Баланс: 30.00₴","header": "FreeBank","notification": "Операція на +%{currentSellAmount}%₴Комісія: 0.00₴ABNK*DONYK NAZARII,Visa Direct18.09.23 16:16Картка %{bankAccount}%Баланс: 30.00₴","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sportbank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                       | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Sportbank","text": "Вiд Юлия К. Баланс 1000,00₴","header": "%{currentSellAmount}%₴","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Pivdenny
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                               | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Pivdenny","text": "Карта: %{bankAccount}%Остаток: 333.00 UAH","header": "%{currentSellAmount}%.00 UAH  MONODirect","notification": "Карта: %{bankAccount}%Остаток: 333.00 UAH","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sinara
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                             | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "p2p","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Karta %{bankAccount}% zachisleno %{currentSellAmount}%,00 RUB Tinkoff Card2Card 22/04/2024. Dostupno 358,00 RUR", "number": "SKB-SINARA"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sinara-Part
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                          | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "p2p","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Karta %{bankAccount}% zachisleno %{currentSellAmount}% RUB Tinkoff Card2Card 22/04/2024. Dostupno 358,00 RUR", "number": "SKB-SINARA"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Sinara SBP
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                 | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "p2p","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "Schet %{bankAccount}% zachisleno %{currentSellAmount}% RUR 17.04.2024 perevod (SBP)", "number": "SKB-SINARA"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Uralsib
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                       | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Уралсиб","text": "POSTUPLENIE SREDSTV NA SCHET: %{currentSellAmount}% RUR. 24.04.24 13:04. Ostatok 24684.05 RUR.","header": "Уралсиб","notification": "POSTUPLENIE SREDSTV NA SCHET: %{currentSellAmount}% RUR. 24.04.24 13:04. Ostatok 24684.05 RUR.","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Union Bank
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "UPI payment of Rs. %{currentSellAmount}% received from patilmaheshshelke@okhdfcbank on 10-JAN-2024 20:53:13: with transaction ID 401082874669 - Union Bank of India","number": "ADUNIONB"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal PrivatBank
    * create and send the request
      | method | path                                                                    | body                        | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | privatebankWebhookBody.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal PrivatBank-Part
    * create and send the request
      | method | path                                                                    | body                            | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/telegram/%{traderAutomationId}% | privatebankWebhookBodyPart.json | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal UBRiR SMS
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                  | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{not_text_big}","header": "{not_title}","notification": "{notification}","sms_text": "Зачисление СБП: %{currentSellAmount}% р от Даниил Алексеевич Л","number": "UBRR"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Ozon Push
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                         | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Ozon Банк","text": "Пополнение на %{currentSellAmount}% ₽. Полина Вадимовна Ф.. Баланс 7 861.86 ₽","header": "Ozon Банк","notification": "Пополнение на %{currentSellAmount}% ₽. Полина Вадимовна Ф.. Баланс 7 861.86 ₽","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal RNKB SMS
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                                  | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{not_text_big}","header": "{not_title}","notification": "{notification}","sms_text": "Schet *%{bankAccount}% Zachislen perevod SBP summa %{currentSellAmount}% RUR ot Ilya Igorevich M tel +79052229339 is Sberbank of Ruussia 23-09-21 23:55:10 balans: 5122.50 RUR. Operatsiya zavershena uspeshno","number": "RNCB"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Rosselhozbank Push
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                                         | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "RSHB","text": "16:29 перевод %{currentSellAmount}% р на карту *%{bankAccount}% от Татьяна Владимировна Ф. Баланс 1,541.90","header": "RSHB","notification": "16:29 перевод %{currentSellAmount}% р на карту *%{bankAccount}% от Татьяна Владимировна Ф. Баланс 1,541.90","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal BankLvov
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                                                                                    | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "P2Pay","text": "{text}","header": "{header}","notification": "{notification}","sms_text": "KOD DOSTUPU 4244,KARTA*%{bankAccount}% 2023-06-23 15:03:25 BLOKUVANNYA %{currentSellAmount}% UAH *ZALISHOK 131.69 UAH,OVER 0.00 UAH Z YAKYKH VIKORISTANO 0.00 UAH CHENTUK YULIIA VISA D","number": "BankLviv"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"

  @fragment
  Scenario: Complete auto-deal Vlasniy Rahunok
    * create and send the request
      | method | path                                                                   | body                                                                                                                                                                                                                                                                          | headers                                                                                             |
      | POST   | /trader-automation-api/external/webhook/android/%{traderAutomationId}% | {"id": "%{deviceId}%","app": "Банк Власний Рахунок","text": "Переказ коштів з ПР Довгань В. П. на ПР Плєшаков М. С.Залишок: 81.58 ₴","header": "Переказ з картки на картку: %{currentSellAmount}%₴ ","notification": "","sms_text": "{sms_message}","number": "{sms_number}"} | {"Content-Type":"application/json", "Accept":"*/*", "Authorization":"Bearer %{traderAccessToken}%"} |
    * expected status code "200"
