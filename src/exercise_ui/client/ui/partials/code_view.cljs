(ns exercise-ui.client.ui.partials.code-view
  (:require
   [clojure.string :as string]
   [cljsjs.codemirror.addon.runmode.runmode-standalone]
   [cljsjs.codemirror.mode.clojure] ;; must come after runmode-standalone
   [zprint.core :refer [zprint zprint-str]]
   [exercise-ui.client.ui.styles :as styles]))

(defn format-code [code]
  (if (nil? code)
    "nil"
    (zprint-str code 40 {:style [:community :hiccup]
                         :binding {:force-nl? true}
                         :parse-string? (string? code)
                         :parse-string-all? (string? code)
                         :parse {:interpose "\n\n"}
                         :set {:sort? true}
                         :map {:comma? false
                               :lift-ns? false
                               :force-nl? true}
                         :fn-map {"if" :arg1-force-nl
                                  "when" :arg1-force-nl
                                  "fn" :binding
                                  "rcf/tests" :flow-body}})))

(defn code-view
  [{:keys [class fragment? pre-formatted? lang] :as opts} code]
  [:div {:class (string/join " "  ["CodeMirror" "cm-s-railscasts" class])
         :ref (fn [el]
                (when (not (nil? el))
                  (js/CodeMirror.runMode
                   (cond
                     pre-formatted?
                     code
                     (and (not fragment?) (vector? code))
                     (string/join "\n\n" (map format-code code))
                     :else
                     (format-code code))
                   (or lang "clojure")
                   el)))}])
