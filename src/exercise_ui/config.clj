(ns exercise-ui.config
  (:require
    [exercise-ui.client.ui.styles]
    [exercise-ui.server.routes]))

(def config
  {:omni/title "Clojure Exercises"
   :omni/cljs {:main "exercise-ui.client.core"}
   :omni/css {:styles "exercise-ui.client.ui.styles/app"}
   :omni/auth {:cookie {:name "clojure-exercise-ui"}}
   :omni/api-routes #'exercise-ui.server.routes/routes})
