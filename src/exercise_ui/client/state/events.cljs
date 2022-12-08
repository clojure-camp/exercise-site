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

(reg-fx :ajax ajax/fx)

(reg-event-fx :initialize!
  (fn [_ _]
    (bloom.commons.pages/initialize! pages)
    {:db {:user-checked? false
          :exercises {}
          :ordered-exercise-ids []
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
            :on-error (fn [_]
                        (dispatch [:-set-user-checked!]))}}))

(reg-event-fx :-fetch-example!
  (fn [_ _]
    {:ajax {:uri "/api/example"
            :method :get
            :on-success (fn [data]
                          (dispatch [:-store-example! (:example data)]))
            :on-error (fn [_])}}))

(reg-event-fx :-store-example!
  (fn [{db :db} [_ example]]
    {:db (assoc db :example example)}))

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
             (assoc :exercises (key-by :id exercises))
             (assoc :ordered-exercise-ids order))}))

(reg-event-fx :log-out!
  (fn [{db :db} _]
    {:db (assoc db :user nil :user-checked? false)
     :ajax {:uri "/api/session"
            :method :delete
            :on-success (fn [_])
            :on-error (fn [_])}}))

(reg-event-fx :-store-user!
  (fn [{db :db} [_ user]]
    {:db (assoc db :user user)}))

(reg-event-fx :-set-user-checked!
  (fn [{db :db} [_ user]]
    {:db (assoc db :user-checked? true)}))

(reg-event-fx :set-exercise-status!
  (fn [{db :db} [_ exercise-id status]]
    {:db (assoc-in db [:user :progress exercise-id :status] status)
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

(reg-event-fx
  :admin/mark-reviewed!
  (fn [_ [_ user-id exercise-id]]
    {:ajax {:uri "/api/admin/progress"
            :method :post
            :params {:user-id user-id
                     :exercise-id exercise-id}
            :on-success (fn [new-data]
                           (dispatch [:admin/-store-progress! new-data]))
            :on-error (fn [_])}}))

(reg-event-fx
  :request-email!
  (fn [_ [_ email code on-success on-failure]]
    {:ajax {:uri "/api/request-email"
            :method :post
            :params {:email email
                     :code code}
            :on-success (fn [_] (on-success))
            :on-error (fn [] (on-failure))}}))
