(ns exercise-ui.client.ui.admin.progress-page
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [bloom.commons.pages :refer [path-for]]
   [exercise-ui.client.ui.exercises-page :refer [sort-exercises]]))

(defn admin-sort-exercises
  [exercises]
  (->> exercises
      sort-exercises
      (sort-by (fn [ex]
                 (case (:category ex)
                   :learning-functions 0
                   :starter 1
                   :synthesis 2)))))

(defn progress-page-view
  []
  (dispatch [:admin/load-progress!])
  (fn []
    [:div.page.admin-progress
     [:h1 "Progress"]
     [:button {:on-click (fn [_] (dispatch [:admin/load-progress!]))} "Refresh"]
     (let [exercises (admin-sort-exercises @(subscribe [:exercises]))
           users @(subscribe [:admin/progress])]
       [:table.user-progress
        [:thead
         [:tr
          [:th "Exercise"]
          (doall (for [user users]
                   ^{:key (user :user-id)}
                   [:th (user :user-id)]))]]
        [:tbody
         (doall
           (for [{:keys [id] :as exercise} exercises]
             ^{:key id}
             [:tr
              [:td.exercise
               [:a {:href (path-for :exercise {:exercise-id id})}
                (exercise :title)]]
              (for [user users]
                ^{:key (user :user-id)}
                [:td.status
                 (get-in user [:progress id])])]))]])]))
