(ns exercise-ui.client.ui.app
  (:require
    [re-frame.core :refer [subscribe]]))

(defn app-view []
  [:div
   (for [exercise @(subscribe [:exercises])]
     [:div
      (exercise :id)])])
