(ns exercise-ui.client.ui.app
  (:require
    [re-frame.core :refer [subscribe]]
    [exercise-ui.client.pages :as pages]))

(defn app-view []
  [:div
   [pages/current-page-view]])
