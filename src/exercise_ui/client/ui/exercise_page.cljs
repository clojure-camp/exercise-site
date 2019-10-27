(ns exercise-ui.client.ui.exercise-page
  (:require
    [clojure.string :as string]
    [bloom.commons.fontawesome :as fa]
    [bloom.commons.pages :refer [path-for]]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe dispatch]]
    [exercise-ui.utils :refer [parse-backticks]]
    [exercise-ui.client.ui.code-view :refer [code-view]]
    [exercise-ui.client.ui.exercise-status :refer [exercise-status-view]]
    [exercise-ui.client.ui.teachable :refer [teachable-view]]))

(defn solution-view [exercise]
  (let [open? (r/atom false)]
    (fn []
      [:section.solution
       [:header {:on-click (fn []
                             (swap! open? not))}
        [:h2 "example solution"]
        (if @open?
          [fa/fa-chevron-down-solid]
          [fa/fa-chevron-right-solid])]
       (when @open?
         [code-view (exercise :solution) "code"])])))

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    (let [exercise-status @(subscribe [:exercise-status exercise-id])]
      [:div.page.exercise
       [:header
        [exercise-status-view (exercise :id)]
        [:h1 (exercise :title)]]

       [:div.status
        (case exercise-status
          nil
          [:div
           [:button
            {:on-click
             (fn [_]
               (dispatch [:set-exercise-status! exercise-id :started]))}
            "Start"]]
          :started
          [:div
           "In Progress"
           [:button
            {:on-click
             (fn [_]
               (dispatch [:set-exercise-status! exercise-id :completed]))}
            "Complete"]]
          :completed
          [:div "Complete"]
          :reviewed
          [:div "Reviewed"])]

       [:section.instructions
        (into [:<>]
              (for [node (exercise :instructions)]
                (if (or (not (string? node))
                        (and
                          (string/starts-with? node "(")
                          (string/ends-with? node ")")))
                  [code-view node "code" true]
                  (into [:p] (parse-backticks node)))))]

       (when (seq (exercise :tests))
         [:section.tests
          [:header [:h2 "example tests"]]
          [code-view (exercise :tests) "code"]])

       [:section.functions
        (into [:<>]
              (for [f (filter symbol? (exercise :teaches))]
                [teachable-view f "taught"]))
        (into [:<>]
              (for [f (filter symbol? (exercise :uses))]
                [teachable-view f "used"]))]

       (when (and (exercise :solution) (= exercise-status :completed))
         [solution-view exercise])

       (when (exercise :related)
         [:div.related
          [:h2 "Related"]
          (for [id (exercise :related)]
            ^{:key id}
            [:div.exercise
             [exercise-status-view id]
             [:a {:href (path-for :exercise {:exercise-id id})} id]])])])))
