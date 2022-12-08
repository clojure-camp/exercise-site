(ns exercise-ui.client.ui.admin.progress-page
  (:require
   [clojure.string :as string]
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

(defn nice-date
  [date]
  (let [now (js/Date.)]
    (if (= (.toDateString date) (.toDateString now))
      (->> (.toTimeString date)
          (re-matches #"^(\d{1,2}:\d{2}).*$")
          second)
      (.toLocaleString date))))

(defn nice-interval
  [start finish]
  (let [diff (- (.getTime finish) (.getTime start))
        time (cond
               (< diff (* 60 1000)) (str (Math/round (/ diff 1000)) " second")
               (< diff (* 60 60 1000)) (str (Math/round (/ diff (* 1000 60))) " minute")
               :else (str (/ (Math/round (/ (* 10 diff) (* 1000 60 60))) 10) " hour"))]
    (if (string/starts-with? time "1 ")
      time
      (str time "s"))))

(defn status-view
  [progress]
  (when progress
    (let [{:keys [status started-at completed-at reviewed-at]
           :or {started-at (js/Date.) completed-at (js/Date.) reviewed-at (js/Date.)}} progress]
      (case status
        :started
        [:span "started at " (nice-date (or started-at (js/Date.)))]
        :completed
        [:span "completed in " (nice-interval started-at completed-at)]
        :reviewed
        [:span "reviewed at " (nice-date reviewed-at)]))))

(defn progress-page-view
  []
  (dispatch [:admin/load-progress!])
  (fn []
    [:div.page.admin-progress
     [:h1 "Progress"]
     [:button {:on-click (fn [_] (dispatch [:admin/load-progress!]))} "Refresh"]
     (let [exercises (concat
                       @(subscribe [:ordered-exercises])
                       (admin-sort-exercises @(subscribe [:unordered-exercises])))
           users (sort-by :user-id @(subscribe [:admin/progress]))]
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
               [:a {:href (path-for [:exercise {:exercise-id id}])}
                (exercise :title)]]
              (for [user users]
                ^{:key (user :user-id)}
                [:td.status
                 [status-view (get-in user [:progress id])]
                 (when (= :completed (get-in user [:progress id :status]))
                   [:button.review
                    {:title "Mark reviewed"
                     :on-click (fn [_]
                                 (dispatch [:admin/mark-reviewed!
                                            (user :user-id)
                                            id]))}
                    "📋"] )])]))]])]))
