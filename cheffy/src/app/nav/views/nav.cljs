(ns app.nav.views.nav
  (:require [app.nav.views.authenticated :refer [authenticated]]
            [app.nav.views.public :refer [public]]))

(defn nav
      "Redirects to the properly view accordingly to the authentication process"
      []
      (let [user true]
           (if user
             authenticated
             public)))