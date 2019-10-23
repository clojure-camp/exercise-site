(ns exercise-ui.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub :exercises
  (fn [db _]
    (vals (db :exercises))))

(reg-sub :exercise
  (fn [db [_ exercise-id]]
    (get-in db [:exercises exercise-id])))

(reg-sub :pastebin
  (fn [db _]
    (db :pastebin)))

(reg-sub :logged-in?
  (fn [db _]
    (some? (db :user))))

(reg-sub :user-name
  (fn [db _]
    (get-in db [:user :name])))

(reg-sub :exercise-status
  (fn [db [_ exercise-id]]
    (get-in db [:user :progress exercise-id])))
