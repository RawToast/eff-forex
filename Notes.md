# Reqs

* The application should at least support 10000 requests per day
  * Free tier is limited to 1000 requests a day
  * But prices can be up to 5 minutes old, so caching results in the processor could work.
    * State...
    * Could create a caching client
    * Better to create some kind of repository and an in memory implementation
    
## Thoughts

* Lots of interesting new stuff here...
* First sticking point is I don't know a client a Monix Task
  * Used to fs2.Task and IO http libraries, it is possible to convert 
  between the types using futures or libraries.
  * Or use create a fs2.Task backed Interpreter instead
  * Sttp looks good -- supports many different Async types
  
