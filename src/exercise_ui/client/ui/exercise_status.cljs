(ns exercise-ui.client.ui.exercise-status
  (:require
    [re-frame.core :refer [subscribe]]))

(defn exercise-status-view [exercise-id]
  (let [status @(subscribe [:exercise-status exercise-id])]
    [:div.status {:class ((fnil name "none") status)}
     (case status
       nil ""
       :started "Started"
       :completed "Completed"
       :reviewed "Reviewed")]))
