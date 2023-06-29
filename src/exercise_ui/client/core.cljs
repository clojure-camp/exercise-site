(ns
  ^:figwheel-hooks
  exercise-ui.client.core
  (:require
    [bloom.omni.reagent :as rdom]
    [re-frame.core :refer [dispatch-sync]]
    [exercise-ui.client.state.events]
    [exercise-ui.client.state.subs]
    [exercise-ui.client.ui.app :refer [app-view]]))

(enable-console-print!)

(defn render
  []
  (rdom/render [app-view]))

(defn ^:export init
  []
  (dispatch-sync [:initialize!])
  (render))

(defn ^:after-load reload
  []
  (render))
