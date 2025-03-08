(ns exercise-ui.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub :loading?
  (fn [db _]
    (empty? (db :db/exercises))))

(reg-sub :exercises
  (fn [db _]
    (vals (db :db/exercises))))

(reg-sub :ordered-exercises
  (fn [db _]
    (->> (db :db/ordered-exercise-ids)
         (map (fn [id]
                (get-in db [:db/exercises id]))))))

(reg-sub :unordered-exercises
  (fn [db _]
    (let [ordered-ids (set (db :db/ordered-exercise-ids))]
      (->> (db :db/exercises)
           vals
           (remove (fn [exercise]
                     (contains? ordered-ids (exercise :exercise/id))))))))

(reg-sub :exercise
  (fn [db [_ exercise-id]]
    (get-in db [:db/exercises exercise-id])))

