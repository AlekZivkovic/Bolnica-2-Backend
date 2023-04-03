Feature: Laboratory Service
  Laboratory service is used for managing referrals and laboratory work orders - Creating,
  deleting, updating, searching by parameters

  Scenario: Doctor specialist creates a new referral
    When doctor provides valid information for creating a referral
    Then created referral is returned

  Scenario: Doctor specialist fetches referral's info
    When given referral does not exist for given id
    Then NotFoundException is thrown with status code 404 for given id

  Scenario: Doctor specialist fetches referral's info
    When given referral exists for given id
    Then referral is returned for given id