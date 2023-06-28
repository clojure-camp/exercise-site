(ns exercise-ui.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub :loading?
  (fn [db _]
    (empty? (db :exercises))))

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

