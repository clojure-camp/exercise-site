(ns exercise-ui.client.ui.exercise-page
  (:require
    [clojure.string :as string]
    [bloom.commons.pages :refer [path-for]]
    [cljsjs.codemirror]
    [cljsjs.codemirror.mode.clojure]
    [re-frame.core :refer [subscribe dispatch]]
    [zprint.core :refer [zprint zprint-str]]))

(defn format-code [code]
  (if (nil? code)
    "nil"
    (zprint-str code 50 {:style :community
                         :binding {:force-nl? true}
                         :parse-string? (string? code)
                         :map {:comma? false
                               :force-nl? true}
                         :fn-map {"if" :arg1-force-nl
                                  "when" :arg1-force-nl
                                  "fn" :binding}})))

(defn code-view [code class-name]
  [:div {:class (string/join " "  ["CodeMirror" "cm-s-railscasts" class-name])
         :ref (fn [el]
                (when (not (nil? el))
                  (js/CodeMirror.runMode
                    (if (vector? code)
                      (string/join "\n\n" (map format-code code))
                      (format-code code))
                    "clojure"
                    el)))}])

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    (let [exercise-status @(subscribe [:exercise-status exercise-id])]
      [:div.page
       [:h1 (exercise :title)]

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

       (into [:div.instructions]
             (for [node (exercise :instructions)]
               (if (and
                     (string/starts-with? node "(")
                     (string/ends-with? node ")"))
                 [code-view node "code"]
                 [:p node])))

       (when (seq (exercise :tests))
         [:div.tests
          [:h2 "Tests"]
          [code-view (exercise :tests) "code"]])

       (when (exercise :solution)
         [:details
          [:summary "Solution"]
          [:div.solution
           [code-view (exercise :solution) "code"]]])

       (when (exercise :related)
         [:div.related
          [:h2 "Related"]
          (for [id (exercise :related)]
            ^{:key id}
            [:a {:href (path-for :exercise {:exercise-id id})} id])])])))
