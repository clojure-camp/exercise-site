(ns exercise-ui.client.ui.home-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [subscribe]]
    [bloom.commons.pages :refer [path-for]]))

(defn home-page-view [params]
  [:div.page.home
   [:table
    [:tbody
     (for [exercise @(subscribe [:exercises])]
       ^{:key (exercise :id)}
       [:tr
        [:td
         [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
          (exercise :id)]]
        [:td
         (string/join " " (sort (map str (exercise :uses))))]])]]])
