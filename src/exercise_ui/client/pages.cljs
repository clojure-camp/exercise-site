(ns exercise-ui.client.pages
  (:require
    [reagent.core :as r]
    [bloom.commons.pages]
    [exercise-ui.client.ui.admin.progress-page :refer [progress-page-view]]
    [exercise-ui.client.ui.exercises-page :refer [exercises-page-view]]
    [exercise-ui.client.ui.pastebin-page :refer [pastebin-page-view]]
    [exercise-ui.client.ui.exercise-page :refer [exercise-page-view]]
    [exercise-ui.client.ui.setup-page :refer [setup-page-view]]
    [exercise-ui.client.ui.shortcuts-page :refer [shortcuts-page-view]]
    [exercise-ui.client.ui.reference-example-page :refer [reference-example-page-view]]))

(def pages
  [{:id :exercises
    :view (fn [data] [exercises-page-view])
    :path "/"}

   {:id :exercise
    :view (fn [data]
            [exercise-page-view (:exercise-id data)])
    :path "/exercises/:exercise-id"
    :coerce {:exercise-id str}}

   {:id :pastebin
    :view (fn [data]
            [pastebin-page-view])
    :path "/pastebin"}

   {:id :setup
    :view (fn [data]
            [setup-page-view])
    :path "/instructions/setup"}

   {:id :shortcuts
    :view (fn [data]
            [shortcuts-page-view])
    :path "/instructions/shortcuts"}

   {:id :reference-example
    :view (fn [data]
            [reference-example-page-view])
    :path "/reference-example"}

   {:id :admin/progress
    :view (fn [data]
            [progress-page-view])
    :path "/b21bc121-6525-4b99-beb7-b29943ac7973"}])

(def current-page-view bloom.commons.pages/current-page-view)

(def current-page-id
  (r/cursor bloom.commons.pages/state [:page-id]))
