(ns exercise-ui.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [exercise-ui.omni-config :refer [omni-config]]
    [exercise-ui.server.transactions :as tx]))

(defn start! []
  (tx/init!)
  (omni/start! omni/system omni-config))

(defn stop! []
  (omni/stop!))

(defn restart! []
  (stop!)
  (start!))

(defn -main []
  (start!))

