(ns atol.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [atol.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[atol started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[atol has shut down successfully]=-"))
   :middleware wrap-dev})
