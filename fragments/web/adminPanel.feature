#language:en

Feature: Login in admin panel

  @fragment
  Scenario: Login in admin panel
    * insert fragment "Login as administrator"
    * open URL by page name "Админка"
    * add token "admin"

