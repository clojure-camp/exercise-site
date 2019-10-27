(ns exercise-ui.client.ui.reference-example-page
  (:require
   [re-frame.core :refer [subscribe]]
   [exercise-ui.client.ui.code-view :refer [code-view]]))

(defn reference-example-page-view
  []
  [:div.page.reference-example
   [:h1 "Reference Example Code"]
   [code-view @(subscribe [:example]) "code"]])
