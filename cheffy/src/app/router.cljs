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

(defn path-for
      [route]
      (bidi/path-for routes route))

(defn set-token!
      [token]
      (pushy/set-token! history token))