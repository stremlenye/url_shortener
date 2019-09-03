# Coding Challenge

URL shortener HTTP service.

## Problem Specification

Design and implement a URL shortener HTTP service that fulfills the following criteria:
* Provide a HTTP API to:
  * Shorten a URL
  * Redirecting to the long URL by the shortened URL
* Shortened URL requirements:
  * Id of the shortened URL needs to be unique (across past and concurrent requests and multiple backend service instances)
  * Id of the shortened URL should be as short as possible, as max. 8 characters long
  * Long/shortened URL mapping needs to be persisted to not lose the mapping after backend service restart

## Assessment Criteria

We expect that your code is well-factored, without needless
duplication, follow good practices and be automatically verified.

What we will look at:
* How clean is your design and implementation, how easy it is to
understand and maintain your code
* How you verified your software, if by automated tests or some
other way
