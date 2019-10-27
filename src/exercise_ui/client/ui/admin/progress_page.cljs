(ns exercise-ui.client.ui.admin.progress-page
  (:require
   [re-frame.core :refer [dispatch subscribe]]))

(defn progress-page-view
  []
  (dispatch [:admin/load-progress!])
  (fn []
    [:div.page.admin-progress
     [:h1 "Progress"]
     [:button {:on-click (fn [_] (dispatch [:admin/load-progress!]))} "Refresh"]
     (let [exercises @(subscribe [:exercises])
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
           (for [exercise exercises]
             ^{:key (exercise :id)}
             [:tr
              [:td.exercise (exercise :id)]
              (for [user users]
                ^{:key (user :user-id)}
                [:td.status
                 (get-in user [:progress (exercise :id)])])]))]])]))
