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


## Solution overview

The problem of generating short urls breaks down to the problem of acquiring the keys both unique and short enough to satisfy the
requirements. There are few approaches which solves the problem each with it's own pros and cons.

The most classical one is to
use hash function to compute the hash of the original url itself and trim the resulting hash to fit into the trunk length limit.
To ensure uniqueness of the generated key across instances each one have to be provided with unique salt to use with hash function.
While having the nodes generate keys independently surely provides the latency benefit there is a downside of said approach coming
from hash collisions and increasing probability of key uniqueness violation by taking only the required portion of characters from
original hash.  

To solve the problems of the previous method the one could favour the way which includes creating the single source of unique keys
shared among instances. This approach gives the benefit of a better oversight over the key generation algorithm since it has single source
of truth but increases the latency as far as application instances have to communicate with said service to acquire the keys. Another
downside – failure of key generation service would lead to the whole system outage.

The approach taken in this solution makes use of randomly generated numeric values being encoded into url safe strings using the
constant alphabet. The resulting keys are prefixed with the configured node specific character to ensure uniqueness across nodes which on
its own limits the amount of nodes to `alphabet.size`. Being clearly a downside of the approach it still leaves the room for 78 nodes
using the provided alphabet. The numeric values to be used for the keys are generated through `SecureRandom` utility which gives good
values distribution for demonstration purpose.

## Tech stack overview

The choice of libraries used for the implementation stays on the `typlevel/cats` spectrum to support the functional and typesafe approach
which was taken. The HTTP layer is backed by `finch` mostly for convenience as far as that's the HTTP library I am most familiar with
nowadays. Same reasoning applies to the persistence layer as far as the scope of the assignment haven't put strict requirements onto
the data storage.

## Validation

The project is validated by both unit tests as well as integration ones.

The unit tests cover only the key generation related modules to ensure the algorithm works as
intended.

To run the tests use:
```shell
> sbt test
```

The integration tests validate the API functionality and the persistence layer integrity.
Due to its nature integration tests have to depend onto `PostgreSQL` server to be accessible
what is provided by the docker-compose configuration included into project.
To run the integration tests you need `Docker` and`docker-compose` installed and `Docker` running.

To run the tests use that sequence of commands in your sbt console:
```shell
> localDependenciesUp # spins up the container with postgresql server running
> flywayMigrate # runs the database migrations bootstraping the data structure needed
> it:test 
``` 

## Notes

The solution would probably look stretched a bit beyond suggested four hours boundaries but
please keep in mind that certain parts of code are coming from the previous projects of mine
since those being generic and universally useful.   
