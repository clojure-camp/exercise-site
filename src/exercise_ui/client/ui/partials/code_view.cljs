(ns exercise-ui.client.ui.partials.code-view
  (:require
   [clojure.string :as string]
   [cljsjs.codemirror.addon.runmode.runmode-standalone]
   [cljsjs.codemirror.mode.clojure] ;; must come after runmode-standalone
   [reagent.core :as r]
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

(defn blinded-code-view
  "Renders :blind/code with hole tokens (_N) replaced by numbered badges.
   When hole->guess contains an entry for a hole, the chosen value is appended after the badge.

   handlers (optional) enables drag-and-drop:
     :on-hole-drop        (fn [hole-index]) called when a fragment is dropped on a hole
     :on-guess-drag-start (fn [guess-index]) called when a placed fragment starts being dragged
     :on-guess-drag-end   (fn []) called when a placed-fragment drag ends"
  [{:blind/keys [code shuffled-holes]} hole->guess {:keys [on-hole-drop on-guess-drag-start on-guess-drag-end]}]
  (r/with-let [el-ref (atom nil)]
    (let [guess-index->string (zipmap (map :hole/index shuffled-holes)
                                      (map :hole/string shuffled-holes))
          fill! (fn [el]
                  (when el
                    (set! (.-innerHTML el) "")
                    (js/CodeMirror.runMode
                     (string/trim code)
                     "clojure"
                     (fn [text style]
                       (if-let [[_ n] (re-matches #"_(\d+)" text)]
                         (let [hole-index (js/parseInt n)
                               hole-el (.createElement js/document "span")
                               badge (.createElement js/document "span")]
                           (set! (.-className hole-el) "cm-hole")
                           (set! (.-className badge) "cm-hole-badge")
                           (set! (.-textContent badge) n)
                           (.appendChild hole-el badge)
                           ;; drop target
                           (set! (.-ondragover hole-el)
                                 (fn [e]
                                   (.preventDefault e)
                                   (.add (.-classList hole-el) "drag-over")))
                           (set! (.-ondragleave hole-el)
                                 (fn [_]
                                   (.remove (.-classList hole-el) "drag-over")))
                           (set! (.-ondrop hole-el)
                                 (fn [e]
                                   (.preventDefault e)
                                   (.remove (.-classList hole-el) "drag-over")
                                   (when on-hole-drop
                                     (on-hole-drop hole-index))))
                           (when-let [guess-str (some-> (get hole->guess hole-index)
                                                        guess-index->string)]
                             (let [guess-index (get hole->guess hole-index)
                                   guess-el (.createElement js/document "span")]
                               (set! (.-className guess-el) "cm-hole-guess")
                               (js/CodeMirror.runMode guess-str "clojure" guess-el)
                               ;; placed fragments stay draggable
                               (set! (.-draggable guess-el) true)
                               (set! (.-ondragstart guess-el)
                                     (fn [e]
                                       (.setData (.-dataTransfer e) "text/plain" (str guess-index))
                                       (when on-guess-drag-start
                                         (on-guess-drag-start guess-index))))
                               (set! (.-ondragend guess-el)
                                     (fn [_]
                                       (when on-guess-drag-end
                                         (on-guess-drag-end))))
                               (.appendChild hole-el guess-el)))
                           (.appendChild el hole-el))
                         (let [span (.createElement js/document "span")]
                           (set! (.-className span) (if style (str "cm-" style) ""))
                           (set! (.-textContent span) text)
                           (.appendChild el span)))))))]
      (fill! @el-ref)
      [:<>
       [:style
        ".CodeMirror .cm-hole { border-radius: 4px; background: black; padding: 0.25em;}"
        ".CodeMirror .cm-hole.drag-over { outline: 2px dashed white; }"
        ".CodeMirror .cm-hole-badge { border-radius: 4px; background: white;  color: black; padding: 0 0.25em; pointer-events: none; }"
        ".CodeMirror .cm-hole-guess { margin: 0 0.25em; cursor: grab; }"
        ".CodeMirror .cm-hole-badge:only-child { margin-right: 3em; }"
        ".CodeMirror .cm-hole-badge:not(:only-child) { margin-right: 0.25em; }"]
       [:div {:class "CodeMirror cm-s-railscasts code"
              :ref (fn [el]
                     (reset! el-ref el)
                     (fill! el))}]])))

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
