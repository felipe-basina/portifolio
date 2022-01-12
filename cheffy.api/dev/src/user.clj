(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn"
             slurp
             ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)                                   ; Reloads file changed
(def reset-all ig-repl/reset-all)                           ; Reloads all files

(defn app [] (-> state/system :cheffy/app))
(defn db [] (-> state/system :db/postgres))

(comment
  (go)
  (halt)
  (reset))