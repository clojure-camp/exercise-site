(ns exercise-ui.client.ui.exercise-status
  (:require
    [bloom.commons.fontawesome :as fa]
    [re-frame.core :refer [subscribe]]))

(defn exercise-status-view [exercise-id]
  (let [status @(subscribe [:exercise-status exercise-id])
        status-text ((fnil name "none") status)]
    [:div.status {:class status-text
                  :alt status-text}
     (case status
       nil [fa/fa-circle-regular]
       :started [fa/fa-adjust-solid]
       :completed [fa/fa-circle-solid]
       :reviewed [fa/fa-check-circle-solid])]))
