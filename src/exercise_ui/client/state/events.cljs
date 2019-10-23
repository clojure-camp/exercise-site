(ns exercise-ui.client.state.events
  (:require
    [re-frame.core :refer [reg-event-fx reg-fx dispatch]]
    [bloom.omni.fx.ajax :as ajax]
    [bloom.commons.pages]
    [exercise-ui.client.pages :refer [pages]]))

(defn key-by [k col]
  (reduce (fn [memo i]
            (assoc memo (k i) i)) {} col))

(reg-fx :ajax ajax/fx)

(reg-event-fx :initialize!
  (fn [_ _]
    (bloom.commons.pages/initialize! pages)
    {:db {:exercises {}
          :pastebin ""}
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

(reg-event-fx :set-pastebin!
  (fn [{db :db} [_ value]]
    {:db (assoc db :pastebin value)
     :ajax {:uri "/api/pastebin"
            :method :put
            :params {:value value}
            :on-success (fn [_])
            :on-error (fn [_])}}))

(reg-event-fx :fetch-pastebin!
  (fn [{db :db} _]
    {:ajax {:uri "/api/pastebin"
            :method :get
            :on-success (fn [value]
                          (dispatch [:-store-pastebin! (value :value)]))
            :on-error (fn [_])}}))

(reg-event-fx :-store-pastebin!
  (fn [{db :db} [_ value]]
    {:db (assoc db :pastebin value)}))

