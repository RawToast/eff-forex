# Forex

Proxy for getting Currency Exchange Rates

## Starting the server

Add your api key to `src/main/resources/reference.conf`

`sbt run` - starts a server listening on port 8888.

# Reqs

* The application should at least support 10000 requests per day
  * Free tier is limited to 1000 requests a day
  * But prices can be up to 5 minutes old, so caching could work.
    
## Thoughts

* Lots of interesting new stuff here...

* Considered using http4s client, would've had to replace Monix and upgrade a lot of libraries to use Cats 1.x
* Found that sttp provides a client that with both Monix and Circe support
  
* Looked into Akka Http route caching
  * Did not seem to have enough control
  
* Whilst we can control a single pair using caching this does not help when there are
  numerous pairs.
  * Even with 5 minute caching the limit is would easily be reached
  
* 1Forge allows us to query for all pairs for the same cost as requesting a single pair
  
* Handling exceptions via ADTs is nice, just needed more variants for alter native error codes/messages
    
# Post task notes

* If I had more time, I would've attempted making a pure interpreter using the State effect / monad
  * And tests... with tech I know I would tend to go for a TTD approach. I feel that could've been
    a hindrance due to the time limit and my unfamiliarity with the stack.
  
* The caching could be improved in many ways
  * Cache times could be reduced by calling the limits endpoint and calculating the best rate
  * State could be held externally
  
* Could have moved the cache length into config

* Could have injected the sttp backend and used a mock backend for testing

* This was a really interesting task and I had to pick up a lot of new things (Eff, Monix, Grafter, Akka Http, etc)