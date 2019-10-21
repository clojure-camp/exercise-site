(ns exercise-ui.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx reg-fx dispatch]]
    [bloom.omni.fx.ajax :as ajax]))

(defn key-by [k col]
  (reduce (fn [memo i]
            (assoc memo (k i) i)) {} col))

(reg-fx :ajax ajax/fx)

(reg-event-fx :initialize!
  (fn [_ _]
    {:db {:exercises {}}
     :dispatch [:-fetch-exercises!]}))

(reg-event-fx :-fetch-exercises!
  (fn [_ _]
    {:ajax {:uri "/api/exercises"
            :method :get
            :on-success (fn [data]
                          (dispatch [:-store-exercises! data]))
            :on-error (fn [_])}}))

(reg-event-fx :-store-exercises!
  (fn [{db :db} [_ data]]
    {:db (assoc db :exercises (key-by :id data))}))

