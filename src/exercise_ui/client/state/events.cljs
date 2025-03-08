(ns exercise-ui.client.state.events
  (:require
    [ajax.core]
    [re-frame.core :refer [reg-event-fx reg-fx dispatch]]
    [bloom.commons.ajax :as ajax]
    [bloom.commons.pages]
    [exercise-ui.client.pages :refer [pages]]))

(defn key-by [k col]
  (reduce (fn [memo i]
            (assoc memo (k i) i)) {} col))

(reg-fx :ajax ajax/request)

(reg-event-fx :initialize!
  (fn [_ _]
    (bloom.commons.pages/initialize! pages)
    {:db {:db/exercises {}
          :db/ordered-exercise-ids []}
     :dispatch-n [[:-fetch-exercises!]]}))

(reg-event-fx :-fetch-exercises!
  (fn [_ _]
    {:ajax {:uri "/api/exercises"
            :method :get
            :on-success (fn [{:keys [exercises order]}]
                          (dispatch [:-store-exercises! exercises order]))
            :on-error (fn [_])}}))

(reg-event-fx :-store-exercises!
  (fn [{db :db} [_ exercises order]]
    {:db (-> db
             (assoc :db/exercises (key-by :exercise/id exercises))
             (assoc :db/ordered-exercise-ids order))}))
