(ns atol.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[atol started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[atol has shut down successfully]=-"))
   :middleware identity})
