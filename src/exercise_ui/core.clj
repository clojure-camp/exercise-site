(ns exercise-ui.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [exercise-ui.config :refer [config]]
    [exercise-ui.server.transactions :as tx]))

(defn start! []
  (tx/init!)
  (omni/start! omni/system config))

(defn stop! []
  (omni/stop!))

(defn restart! []
  (stop!)
  (start!))

(defn -main []
  (start!))
