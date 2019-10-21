(ns exercise-ui.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx]]))

(reg-event-fx :initialize!
  (fn [_ _]
    {}))
