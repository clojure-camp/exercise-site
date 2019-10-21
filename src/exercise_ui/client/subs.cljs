(ns exercise-ui.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub :exercises
  (fn [db _]
    (vals (db :exercises))))
