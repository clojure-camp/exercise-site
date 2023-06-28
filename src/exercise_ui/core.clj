(ns exercise-ui.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [exercise-ui.omni-config :refer [omni-config]]))

(defn start! []
  (omni/start! omni/system omni-config))

(defn stop! []
  (omni/stop!))

(defn restart! []
  (stop!)
  (start!))

(defn -main []
  (start!))

