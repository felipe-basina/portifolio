{:server/jetty {:handler #ig/ref :cheffy/app
                :port    3000}
 :cheffy/app   {:jdbc-url #ig/ref :db/postgres}
 :db/postgres  {:jdbc-url "jdbc-url"}}