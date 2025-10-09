(ns exercise-ui.client.ui.pages.exercise
  (:require
    [clojure.string :as string]
    [bloom.commons.fontawesome :as fa]
    [bloom.commons.pages :as pages]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe dispatch]]
    [exercise-ui.client.ui.styles :as styles]
    [exercise-ui.utils :refer [parse-backticks]]
    [exercise-ui.client.ui.partials.code-view :refer [code-view format-code]]
    [exercise-ui.client.ui.partials.teachable :refer [teachable-view]]
    [exercise-ui.client.i18n :as i18n]))

(defn solution-view [exercise]
  (r/with-let [open? (r/atom false)]
    [:section.solution
     [:header {:on-click (fn []
                           (swap! open? not))}
      [:h2 (i18n/value {:en-US "example solution"
                        :pt-BR "solução exemplo" })]
      (if @open?
        [fa/fa-chevron-down-solid]
        [fa/fa-chevron-right-solid])]
     (when @open?
       [code-view {:class "code"}
        (:exercise/solution exercise)])]))

(defn test-case-view [exercise]
  (r/with-let [active-mode (r/atom :mode/table)]
    [:section.test-cases
     [:header {:tw "gap-2"}
      [:h2 {:tw "grow"} (i18n/value {:en-US "sample tests"
                                     :pt-BR "testes de exemplo"})]
      (doall
        (for [[mode icon] [[:mode/table [fa/fa-table-solid]]
                           [:mode/rcf "RCF"]
                           [:mode/clj "CLJ"]]]
          ^{:key mode}
          [:button
           {:on-click (fn [_] (reset! active-mode mode))
            :tw [(when (= @active-mode mode)
                   "bg-white")]}
           icon]))]

     (case @active-mode
       :mode/table
       [:table {:cellPadding 0
                :tw "w-full bg-#2b2b2b border-collapse"}
        [:tbody
         (into [:<>]
               (for [{:keys [input output]} (:exercise/test-cases exercise)]
                 [:tr
                  [:td
                   [code-view {:class "code"} (pr-str input)]]
                  [:td
                   [:code {:style {:font-family styles/code-font
                                   :color "white"
                                   :font-size "0.8em"}} ":="]]
                  [:td
                   [code-view {:class "code"} (pr-str output)]]]))]]
       :mode/clj
       [code-view {:class "code"}
        (str
         "(ns exercises." (:exercise/id exercise) "\n"
         "  (:require\n"
         "    [clojure.test :refer [is testing]]))\n\n"
         (when (seq (:exercise/function-template exercise))
           (str (string/join "\n\n" (:exercise/function-template exercise)) "\n\n"))
         (string/join "\n\n"
                      (for [{:keys [input output]} (:exercise/test-cases exercise)]
                        (list 'is (list '= output input))))

         "(clojure.test/run-tests)")]

       :mode/rcf
       [:div
        [code-view {:class "code"
                    :pre-formatted? true}
         (str
           "(ns exercises." (:exercise/id exercise) "\n"
           "  (:require\n"
           "    [hyperfiddle.rcf :as rcf]))\n\n"
           "(rcf/enable!)\n\n"
           (when (seq (:exercise/function-template exercise))
             (str (string/join "\n\n" (:exercise/function-template exercise)) "\n\n"))
           "(rcf/tests\n"
           (string/join "\n\n"
                        (for [{:keys [input output]} (:exercise/test-cases exercise)]
                          (str "  " (format-code input) " := \n"
                               "  "
                               (if (string? output)
                                 (pr-str output)
                                 (string/replace (format-code output)
                                                 "\n" "\n  ")))))
           ")")]])]))

(defn exercise-page-view [exercise-id]
  (when-let [exercise @(subscribe [:exercise exercise-id])]
    [:div.page.exercise
     [:header
      [:h1 (i18n/value (:exercise/title exercise))]]

     [:<>
      [:section.instructions
       (into [:<>]
             (for [node (i18n/value (:exercise/instructions exercise))]
               (cond
                 ;; "a string" => [:p "a string"]
                 (string? node)
                 (into [:p] (parse-backticks node))

                 ;; [:code ...] => [code-view ...]
                 (and
                   (seq node)
                   (= :code (first node)))
                 [code-view {:class "code"
                             :fragment? true}
                  (string/join "\n" (rest node))]

                 ;; [:file ...] => [code-view ...]
                 (and
                   (seq node)
                   (= :file (first node)))
                 [code-view {:class "code"
                             :lang "text/plain"
                             :pre-formatted? true}
                  (string/join "\n" (rest node))]

                 ;; (... ) => [code-view ...]
                 (and
                   (string/starts-with? (str node) "(")
                   (string/ends-with? (str node) ")"))
                 [code-view {:class "code"
                             :fragment? true} node]

                 :else
                 node)))]

       (when (:exercise/function-template exercise)
         [:section.starter-code
          [:header
           [:h2 (i18n/value {:en-US "starter code"
                             :pt-BR "código inicial"})]]
          [:div.body
           [code-view {:class "code"}
            (:exercise/function-template exercise)]]])

      (let [fns (->> (concat (map (fn [x] [x :teaches]) (:exercise/teaches exercise))
                             (map (fn [x] [x :uses]) (:exercise/uses exercise)))
                     (filter (fn [[f _]] (symbol? f))))]
        (when (seq fns)
          [:section.functions
           [:header
            [:h2 (i18n/value {:en-US "related functions"
                              :pt-BR "funções relacionadas"})]]
           [:div.body
            (into [:<>]
                  (->> fns
                       (map (fn [[f category]] [teachable-view f (name category)]))
                       (interpose " ")))]]))

      (when (seq (:exercise/test-cases exercise))
        [test-case-view exercise])

      [solution-view exercise]

      (when (seq (:exercise/related exercise))
        [:div.related
         [:h2 (i18n/value {:en-US "See also:"
                           :pt-BR "Veja também:"})]
         [:div.exercises
          (for [id (:exercise/related exercise)]
            ^{:key id}
            [:div.exercise
             [:a {:href (pages/path-for [:exercise {:exercise-id id}])} id]])]])]]))
