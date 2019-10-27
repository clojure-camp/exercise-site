(ns exercise-ui.client.ui.code-view
  (:require
   [clojure.string :as string]
   [cljsjs.codemirror.mode.clojure]
   [cljsjs.codemirror.addon.runmode.runmode-standalone]
   [zprint.core :refer [zprint zprint-str]]))

(defn format-code [code]
  (if (nil? code)
    "nil"
    (zprint-str code 80 {:style :community
                         :binding {:force-nl? true}
                         :parse-string? (string? code)
                         :parse-string-all? (string? code)
                         :parse {:interpose "\n\n"}
                         :map {:comma? false
                               :force-nl? true}
                         :fn-map {"if" :arg1-force-nl
                                  "when" :arg1-force-nl
                                  "fn" :binding}})))

(defn code-view
  ([code class-name] (code-view code class-name false))
  ([code class-name fragment?]
   [:div {:class (string/join " "  ["CodeMirror" "cm-s-railscasts" class-name])
          :ref (fn [el]
                 (when (not (nil? el))
                   (js/CodeMirror.runMode
                     (if (and (not fragment?) (vector? code))
                       (string/join "\n\n" (map format-code code))
                       (format-code code))
                     "clojure"
                     el)))}]))
