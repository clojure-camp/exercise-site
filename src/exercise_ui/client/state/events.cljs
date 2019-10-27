(ns exercise-ui.client.state.events
  (:require
    [ajax.core]
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
          :user nil
          :example ""
          :pastebin ""}
     :dispatch-n [[:-check-session!]
                  [:-fetch-exercises!]
                  [:-fetch-example!]]}))

(reg-event-fx :-check-session!
  (fn [_ _]
    {:ajax {:uri "/api/session"
            :method :get
            :on-success (fn [data]
                          (dispatch [:-store-user! (data :user)]))
            :on-error (fn [_])}}))

(reg-event-fx :-fetch-example!
  (fn [_ _]
    {:ajax {:uri "https://raw.githubusercontent.com/cognitory/clojure-exercises/master/examples/example_app_state.clj"
            :method :get
            :response-format (ajax.core/text-response-format)
            :on-success (fn [data]
                          (dispatch [:-store-example! data]))
            :on-error (fn [_])}}))

(reg-event-fx :-store-example!
  (fn [{db :db} [_ example]]
    {:db (assoc db :example example)}))

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

(reg-event-fx :log-in!
  (fn [{db :db} [_ id]]
    {:ajax {:uri "/api/session"
            :method :put
            :params {:user-id id}
            :on-success (fn [data]
                          (dispatch [:-store-user! (data :user)]))
            :on-error (fn [_])}}))

(reg-event-fx :log-out!
  (fn [{db :db} _]
    {:db (assoc db :user nil)
     :ajax {:uri "/api/session"
            :method :delete
            :on-success (fn [_])
            :on-error (fn [_])}}))

(reg-event-fx :-store-user!
  (fn [{db :db} [_ user]]
    {:db (assoc db :user user)}))

(reg-event-fx :set-exercise-status!
  (fn [{db :db} [_ exercise-id status]]
    {:db (assoc-in db [:user :progress exercise-id] status)
     :ajax {:uri "/api/progress"
            :method :put
            :params {:exercise-id exercise-id
                     :status status}}}))

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

(reg-event-fx
  :admin/load-progress!
  (fn [_ _]
    {:ajax {:uri "/api/admin/progress"
            :method :get
            :on-success (fn [data]
                          (dispatch [:admin/-store-progress! data]))
            :on-error (fn [_])}}))

(reg-event-fx
  :admin/-store-progress!
  (fn [{db :db} [_ data]]
    {:db (assoc-in db [:admin :progress] data)}))
