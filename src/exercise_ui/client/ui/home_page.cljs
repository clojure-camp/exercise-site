(ns exercise-ui.client.ui.home-page
  (:require
    [re-frame.core :refer [subscribe]]
    [bloom.commons.pages :refer [path-for]]))

(defn home-page-view [params]
  [:div
   (for [exercise @(subscribe [:exercises])]
     ^{:key (exercise :id)}
     [:a {:href (path-for :exercise {:exercise-id (exercise :id)})}
      (exercise :id)])])
