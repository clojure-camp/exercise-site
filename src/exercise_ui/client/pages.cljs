(ns exercise-ui.client.pages
  (:require
    [reagent.core :as r]
    [bloom.commons.pages]
    [exercise-ui.client.ui.admin.progress-page :refer [progress-page-view]]
    [exercise-ui.client.ui.exercises-page :refer [exercises-page-view]]
    [exercise-ui.client.ui.pastebin-page :refer [pastebin-page-view]]
    [exercise-ui.client.ui.exercise-page :refer [exercise-page-view]]
    [exercise-ui.client.ui.index-page :refer [index-page-view]]
    [exercise-ui.client.ui.setup-page :refer [setup-page-view]]
    [exercise-ui.client.ui.shortcuts-page :refer [shortcuts-page-view]]
    [exercise-ui.client.ui.reference-example-page :refer [reference-example-page-view]]))

(def pages
  [{:page/id :index
    :page/view #'index-page-view
    :page/path "/"}

   {:page/id :exercises
    :page/view #'exercises-page-view
    :page/path "/exercises"}

   {:page/id :exercise
    :page/view (fn [[_ {:keys [exercise-id]}]]
                 [exercise-page-view exercise-id])
    :page/path "/exercises/:exercise-id"
    :page/parameters [:map
                      [:exercise-id string?]]}

   {:page/id :pastebin
    :page/view #'pastebin-page-view
    :page/path "/pastebin"}

   {:page/id :setup
    :page/view #'setup-page-view
    :page/path "/instructions/setup"}

   {:page/id :shortcuts
    :page/view #'shortcuts-page-view
    :page/path "/instructions/shortcuts"}

   {:page/id :reference-example
    :page/view #'reference-example-page-view
    :page/path "/reference-example"}

   {:page/id :admin/progress
    :page/view #'progress-page-view
    :page/path "/b21bc121-6525-4b99-beb7-b29943ac7973"}])

(def current-page-view bloom.commons.pages/current-page-view)

(def current-page-id
  (r/cursor bloom.commons.pages/current-page [:data :config :page/id]))
