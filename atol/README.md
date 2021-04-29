# ATOL
This project aims to provide a web solution for saving phones contacts number. There is nothing special when it's about features, instead it is about the technologies used that makes this project special :-)

# Incentive
We, as developers, when willing learning a new programming language, we used to try out some code developing **general purposes applications**, and one of the simplest way of doing that is to write a CRUD web application so with this, allowing us to use resources as HTTP requests/responses, handling errors, parsing forms, database integrations, and the lines keeps going further...

During my **Clojure** studies and journey I found myself searching from a lot of different websites pursuiting some examples on how to create a full CRUD web application starting from the basics of integrating HTML as web pages going through to the not so problematic-database-operations (CREATE, READ, UPDATE, DELETE). But instead of seeing any code with all of this items I just have found fragments on how to do that, not the entire application with all its integrations and particularities.

As my studies and understanding of the **Clojure language** getting improved I decided to write my own application trying not just to test my ability with it, but to experiment a couple of frameworks which I considered a good choice to start working with it.

The outcomes are the current application! There are a lot of improvements to take, good practices to follow, silly mistakes to address and **much** more issues to review that makes it a bad choice to learning the language from it. However this simple application demonstrate some steps and how to start from when it's about on writing a web application using the Clojure progamming language. I am expecting to improve it as I am getting more involved with the **functional paradigm** and less addicted with **OO concepts**.

# Stack
This web application relies on the following frameworks:
- **Luminus** as web framework, with all its dependencies: **selmer, ring and buddy**
- **HugSQL** as *kind of* ORM solution
- **Postgres** as **data store**

# Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

# Running

To start a web server for the application + with dev dependencies, run:

    . ./run.sh

# REPL

Connect to REPL

    lein repl :connect 7000

# Swagger

Swagger UI is available on following path: (but its serving fake resources)

    /swagger-ui/index.html

# Heroku
This solution is deployed on Heroku at the following address:
    
    https://atolweb.herokuapp.com/