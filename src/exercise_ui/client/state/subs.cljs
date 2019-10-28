(ns exercise-ui.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub :loading?
  (fn [db _]
    (not (and
           (or (db :user)
               (db :user-checked?))
           (seq (db :exercises))))))

(reg-sub :exercises
  (fn [db _]
    (vals (db :exercises))))

(reg-sub :ordered-exercises
  (fn [db _]
    (->> (db :ordered-exercise-ids)
         (map (fn [id]
                (get-in db [:exercises id]))))))

(reg-sub :unordered-exercises
  (fn [db _]
    (let [ordered-ids (set (db :ordered-exercise-ids))]
      (->> (db :exercises)
           vals
           (remove (fn [exercise]
                     (contains? ordered-ids (exercise :id))))))))

(reg-sub :exercise
  (fn [db [_ exercise-id]]
    (get-in db [:exercises exercise-id])))

(reg-sub :pastebin
  (fn [db _]
    (db :pastebin)))

(reg-sub :logged-in?
  (fn [db _]
    (some? (db :user))))

(reg-sub :user-id
  (fn [db _]
    (get-in db [:user :user-id])))

(reg-sub :exercise-status
  (fn [db [_ exercise-id]]
    (get-in db [:user :progress exercise-id :status])))

(reg-sub :example
  (fn [db _]
    (db :example)))

(reg-sub
  :admin/progress
  (fn [db _]
    (get-in db [:admin :progress])))
