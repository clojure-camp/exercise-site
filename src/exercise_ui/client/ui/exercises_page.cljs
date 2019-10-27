(ns exercise-ui.client.ui.exercises-page
  (:require
    [clojure.string :as string]
    [clojure.set :as set]
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [subscribe]]
    [exercise-ui.client.ui.exercise-status :refer [exercise-status-view]]
    [exercise-ui.client.ui.teachable :refer [teachable-view]]))

(def difficulty->n {:low 1 :mid 2 :high 3})

(defn intersects?
  [a b]
  (some? (seq (set/intersection a b))))

(defn depends?
  "Does exercise `a` depend on exercise `b` being learnt first? If
  both exercises use things the other teaches, don't count that as a
  dependency."
  [a b]
  (and (intersects? (:uses a) (:teaches b))
       (not (intersects? (:teaches a) (:uses b)))))

(defn group-dependencies
  [exercises]
  (map
    (fn [exercise]
      (assoc exercise
             :dependencies (set (map :id (filter (partial depends? exercise) exercises)))))
    exercises))

(defn first-index-where
  [f v]
  (loop [i 0]
    (cond
      (= i (count v)) nil
      (f (get v i)) i
      :else (recur (inc i)))))

(defn sort-by-deps
  [exercises]
  (loop [exercises (vec exercises)
         i 0]
    (cond
      (= i (count exercises))
      exercises

      (empty? (:dependencies (get exercises i)))
      (recur exercises (inc i))

      :else
      (let [exercise (get exercises i)
            prev (subvec exercises 0 i)]
        (if-let [idx (first-index-where (fn [ex] (contains? (:dependencies ex) (:id exercise))) prev)]
          (recur (into (conj (subvec exercises 0 idx) exercise)
                       (into (subvec exercises idx i)
                             (subvec exercises (inc i))))
                 (inc idx))
          (recur exercises (inc i)))))))

(defn sort-exercises
  [exercises]
  (->> exercises
      (sort-by :title)
      (sort-by (comp difficulty->n :difficulty))
      group-dependencies
      (sort-by (comp count :dependencies))
      sort-by-deps))

(defn exercises-view
  [exercises]
  [:table.exercises
   [:thead
    [:tr
     [:th]
     [:th "Exercise"]
     [:th "Teaches"]
     [:th]]]
   [:tbody
    (doall
      (for [exercise (sort-exercises exercises)]
        ^{:key (exercise :id)}
        [:tr.exercise
         [:td.status
          [exercise-status-view (exercise :id)]]
         [:td
          [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
           (exercise :title)]]
         [:td
          (into [:<>]
                (interpose " " (map teachable-view (exercise :teaches))))]
         [:td.difficulty
          (repeat (difficulty->n (exercise :difficulty)) "★")]]))]])

(defn exercises-page-view [params]
  (let [grouped-exercises (group-by :category @(subscribe [:exercises]))]
    [:div.page.exercises
     [:section
      [:h1 "Learning Functions"]
      [exercises-view (:learning-functions grouped-exercises)]]
     [:section
      [:h1 "Starter Exercises"]
      [exercises-view (:starter grouped-exercises)]]
     [:section
      [:h1 "Synthesis"]
      [exercises-view (:synthesis grouped-exercises)]]]))
