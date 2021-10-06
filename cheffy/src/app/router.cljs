(ns app.router
  (:require [bidi.bidi :as bidi]                            ;; Provides router functions
            [pushy.core :as pushy]                          ;; Manages html states
            [re-frame.core :as rf]))                        ;; Dispatches events when something happens

(def routes ["/" {""              :recipes
                  "become-a-chef" :become-a-chef
                  "saved/"        :saved
                  "recipes/"      {""           :recipes
                                   [:recipe-id] :recipe}
                  "inbox/"        {""          :inboxes
                                   [:inbox-id] :inbox}
                  "profile"       :profile
                  "sign-up"       :sign-up
                  "log-in"        :log-in}])

(def history
  (let [dispatch #(rf/dispatch [:route-changed %])
        match #(bidi/match-route routes %)]
       (pushy/pushy dispatch match)))

(defn start!
      "Adds an event listener to all click events, which dispatches on all matched routes"
      []
      (pushy/start! history))

;; Before allowing multiple parameters
(comment
  defn path-for
  [route]
  (bidi/path-for routes route))

;; Use the '&' to indicate it can receive multiple parameters
;; Solution 1
(comment
  defn path-for
  [& route]
  (apply bidi/path-for routes route))

;; Solution 2 using partial
(def path-for (partial bidi/path-for routes))

(defn set-token!
      [token]
      (pushy/set-token! history token))