(ns exercise-ui.config
  (:require
    [bloom.commons.config :as config]))

(def config
  (config/read
    "config.edn"
    [:map
     [:http-port integer?]
     [:environment keyword?]
     [:exercise-data-path string?]]))




