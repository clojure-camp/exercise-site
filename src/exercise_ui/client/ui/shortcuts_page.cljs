(ns exercise-ui.client.ui.shortcuts-page
  (:require
    [bloom.commons.fontawesome :as fa]
    [clojure.string :as string]))

(def reference
  {"VSCode" {:link "https://code.visualstudio.com/shortcuts/keyboard-shortcuts-windows.pdf"
             :shortcuts
             {"Ctrl+P" "fuzzy file search"
              "Ctrl+Shift+P" "command lookup"
              "Ctrl+B" "toggle sidebar"
              "Ctrl+\\" "create vertical split"
              "Ctrl+1/2/3" "jump to split 1/2/3"
              "Alt+Click" "multiple cursors"
              "Ctrl+D" "given a selection, create another selection around the next  atching occurrence"
              "Ctrl+K Ctrl+S" "keyboard shortcut list and editor"
              "Ctrl+Space" "intellisense suggesions"}}

   "Calva" {:link "https://calva.readthedocs.io/en/latest/commands-top10.html"
            :shortcuts
            {"Ctrl+Alt+C Ctrl+Alt+J" "start REPL (choose Leiningen)"
             "Ctrl+Alt+C Enter" "execute current file"
             "Ctrl+Alt+C V" "evalute current form / highlight  inline (Esc clears)"
             "Ctrl+Alt+C Ctrl+Alt+V" "evaluate current form / highligt  in REPL"
             "Ctrl+Alt+C C" "evalaute current form/highlight (as comment)"
             "Ctrl+Alt+C Space" "evalauate current top-level form"
             "Ctrl+Alt+C Ctrl+Alt+N" "switch REPL to current file's namespace"}}

   "Paredit" {:link "https://calva.readthedocs.io/en/latest/paredit.html"
              :shortcuts
              {"Ctrl+W" "expand selection"
               "Ctrl+Right" "slurp forward / right paren outward / ) -> "
               "Ctrl+Left" "barf forward / right paren inward /  <- ) "
               "Ctrl+Shift+Left" "slurp backward / left paren outward / <- ( "
               "Ctrl+Shift+Right" "barf backward / left paren inward / ( ->"
               "Ctrl+Alt+(" "wrap with ("
               "Ctrl+Alt+[" "wrap with ["
               "Ctrl+Alt+{" "wrap with {"
               "Ctrl+Alt+S" "splice / 'unwrap current' ((+ 1 2)) -> (+ 1 2)" }}})

(defn shortcuts-page-view []
  [:div.page.shortcuts
   (for [[program {:keys [shortcuts link]}] reference]
     ^{:key program}
     [:section
      [:header
       [:h1 program]
       [:a {:href link} [fa/fa-external-link-alt-solid]]]
      [:table.shorcuts
       [:tbody
        (for [[keys explanation] shortcuts]
          [:tr
           [:td {:style {:white-space "nowrap"}}
            (->> (string/split keys #" ")
                     (map (fn [chord]
                            [:kbd.chord
                             (->> (string/split chord #"\+")
                                  (map (fn [s]
                                         [:kbd.key s]))
                                  (interpose [:span.plus "+"]))])))]
           [:td explanation]])]]])])
