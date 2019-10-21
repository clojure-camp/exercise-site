(ns exercise-ui.client.ui.app
  (:require
    [bloom.commons.pages :refer [path-for]]
    [re-frame.core :refer [subscribe]]
    [exercise-ui.client.pages :as pages]))

(defn app-view []
  [:div
   [:a {:href (path-for :home)} "Home"]
   [pages/current-page-view]])
