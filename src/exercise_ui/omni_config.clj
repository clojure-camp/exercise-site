(ns exercise-ui.omni-config
  (:require
    [exercise-ui.config :refer [config]]
    [exercise-ui.client.ui.styles]
    [exercise-ui.server.routes]))

(def omni-config
  {:omni/title "Clojure Exercises"
   :omni/http-port (:http-port config)
   :omni/auth {:cookie {:secret (:auth-cookie-secret config)
                        :name "clojure-exercise-ui"}
               :token {:secret (:auth-token-secret config)}}
   :omni/environment (:environment config)
   :omni/cljs {:main "exercise-ui.client.core"}
   :omni/css {:styles "exercise-ui.client.ui.styles/app"}
   :omni/api-routes #'exercise-ui.server.routes/routes})
