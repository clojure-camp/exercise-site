(ns exercise-ui.client.ui.exercise-page
  (:require
    [re-frame.core :refer [subscribe]]))

(defn exercise-page-view [exercise-id]
  (let [exercise @(subscribe [:exercise exercise-id])]
    [:div.page
     (exercise :id)]))
