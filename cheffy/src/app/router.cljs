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

(defn start!
      []
      (let [dispatch #(js/console.log (:handler %))
            match #(bidi/match-route routes %)]
           (pushy/start! (pushy/pushy dispatch match))))

(defn path-for
      [route]
      (bidi/path-for routes route))