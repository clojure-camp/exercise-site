(ns exercise-ui.client.ui.exercises-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [subscribe]]
    [bloom.commons.pages :refer [path-for]]
    [exercise-ui.client.ui.exercise-status :refer [exercise-status-view]]
    [exercise-ui.client.ui.teachable :refer [teachable-view]]))

(def difficulty->n {:low 1 :mid 2 :high 3})

(defn sort-exercises
  [exercises]
  (->> exercises
      (sort-by :title)
      (sort-by (comp difficulty->n :difficulty))))

(defn exercises-view
  [exercises]
  [:table.exercises
   [:thead
    [:tr
     [:th "Status"]
     [:th]
     [:th "Exercise"]
     [:th "Teaches"]]]
   [:tbody
    (doall
      (for [exercise (sort-exercises exercises)]
        ^{:key (exercise :id)}
        [:tr.exercise
         [:td.status
          [exercise-status-view (exercise :id)]]
         [:td.difficulty
          (repeat (difficulty->n (exercise :difficulty)) "★")]
         [:td
          [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
           (exercise :title)]]
         (into
           [:td]
           (interpose " " (map teachable-view (exercise :teaches))))]))]])

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
