(ns exercise-ui.client.ui.setup-page
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [exercise-ui.utils :refer [parse-backticks]]))

(def oses [:mac :ubuntu :windows])
(def editors [:vscode :emacs :intellij :vim :atom])

; more: https://clojurebridgelondon.github.io/workshop/development-tools/install-guides/vscode-calva.html

(defn plugin-view [plugins]
  [:div.plugins {:style {:margin-left "2em"}}
   [:h3 "Editor Plugins"]
   (for [plugin plugins]
     ^{:key (plugin :name)}
     [:div.plugin {:style {:margin-bottom "1em"}}
      [:div
       [:span.name {:style {:font-weight "bold"}}
        (if (plugin :url)
          [:a {:href (plugin :url)}
           (plugin :name)]
          (plugin :name))]
      (when (plugin :group)
        [:span
         " (" (plugin :group) ")"])
      " "
      [:span {:style {:font-style "italic"}}
       (case (plugin :status)
         :recommended "★ recommended"
         "")]
      ]

      [:div (plugin :note)]])])

(def steps
  [{:heading "Install Java (JDK)"
    :all "Clojure runs on the JVM (the Java Virtual Machine). You need to have a JDK installed to develop and run Clojure programs.\n\nFirst, check if you have Java installed.\n\tIn a terminal, run: `java -version`. If the command runs and reports a JDK version > 8 (or 1.8), then you're fine."
    :steps [{:windows "To install Java:\n\tvia AdoptOpenJDK (recommended):\n\t\thttps://adoptopenjdk.net/ (choose OpenJDK 8 (LTS) & Hotspot)\n\tvia chocolatey:\n\t\thttps://chocolatey.org/\n\t\t`choco install openjdk`\n\tvia other methods:\n\t\thttps://stackoverflow.com/questions/52511778/how-to-install-openjdk-11-on-windows\n\n\tAfter installing, check if Java is installed correctly as noted above."
             :mac "To install Java:\n\tvia homebrew:\n\t\t`brew update`\n\t\t`brew cask install java`\n\tvia AdoptOpenJDK:\n\t\thttps://adoptopenjdk.net/releases.html (choose OpenJDK 11 (LTS) & Hotspot)\n\n\tAfter installing, check if Java is installed correctly as noted above."
             :ubuntu
             "To install Java:\n\tvia apt:\n\t\t`sudo apt-get install default-jre`\n\n\tAfter installing, check if Java is installed correctly as noted above."}]}
   {:heading "Install Leiningen"
    :all "Leiningen is a commandline tool for working with Clojure projects (similar to Javascript's npm).\n\nFirst, check if you have Leiningen installed.\n\tIn a terminal, run: `lein -v`. If the command runs and reports a Leiningen version > 2.8, then you're fine."
    :steps [{:windows
             "To install Leiningen:\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\t\t(you can save the lein.bat file wherever, perhaps: `C:/Program Files/Leiningen/lein.bat`)\n\t\tyou will need to add the directory you put `lein.bat` in to your `PATH`\n\t\t\tinstructions for Win 10: https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/\n\t\t\tyou will need to close/reopen your cmd.exe/PowerShell for the path to update\n\tvia chocolatey\n\t\t`choco install lein`\n\n\tAfter installing, check if Leiningen installed correctly as noted above."
             :mac
             "To install Leiningen:\n\tvia homebrew:\n\t\t`brew install leiningen`\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\n\tAfter installing, check if Leiningen is installed correctly as noted above."
             :ubuntu
             "To install Leiningen:\n\tvia apt:\n\t\t`sudo apt-get install leiningen-clojure`\n\tvia leiningen.org:\n\t\tFollow instructions at: https://leiningen.org/\n\t\t\tyou can save the lein.bat file wherever, perhaps: `/usr/bin/lein`\n\t\t\t\tthe directory needs to be in your path, you can check if it's there by running `echo $PATH`\n\t\t\t\tif it's not in your PATH, ask for help\n\t\t\t\tyou will need to close/reopen your shell/terminal for the path to update\n\n\tAfter installing, check if Leiningen is installed correctly as noted above."}]}
    {:heading "Setup Your Editor"
     :steps [{
              :vscode "
              Download it at: https://code.visualstudio.com/

              To install extensions...
                open the extensions search (via the extensions icon in the left sidebar, or via Ctrl+Shift+X)"
              :emacs "To consider:
                       CIDER
                       Spacemacs
                       Emacs Live
                       inf-clojure: https://clojurescript.org/tools/emacs-inf"
              :atom "To download Atom: https://atom.io/"}
             {;; plugins
              :vscode [plugin-view [{:name "Calva"
                                     :group "REPL integration, etc."
                                     :url "https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva"
                                     :status :recommended}]]
              :atom [plugin-view [{:name "ProtoREPL"
                                   :url "https://atom.io/packages/proto-repl"
                                   :group "REPL integration"
                                   :status :recommended}
                                  {:name "ink"
                                   :url "https://atom.io/packages/ink"
                                   :note "prerequisite for ProtoREPL"
                                   :group "REPL integration"
                                   :status :recommended}
                                  {:name "proto-repl-charts"
                                   :url "https://atom.io/packages/proto-repl-charts"
                                   :status :optional}
                                  {:name "proto-repl-sayid"
                                   :url "https://atom.io/packages/proto-repl-sayid"
                                   :note "support for the Clojure debugger Sayid"
                                   :status :optional}
                                  {:name "parinfer"
                                   :url "https://atom.io/packages/parinfer"
                                   :note "brackets based on indentation, don't use with paredit"
                                   :group "structural editing"
                                   :status :recommended}
                                  {:name "lisp-paredit"
                                   :url "https://atom.io/packages/lisp-paredit"
                                   :note "paredit-style editing, don't use with parinfer"
                                   :group "structural editing"
                                   :status :recommended}
                                  {:name "rainbow-delimiters"
                                   :url "https://atom.io/packages/rainbow-delimiters"}
                                  {:name "atom-beautify"
                                   :url "https://atom.io/packages/atom-beautify"
                                   :group "formatting"}
                                  {:name "linter-joker"
                                   :url "https://atom.io/packages/linter-joker"
                                   :group "linter"}
                                  {:name "Other Recommendations"
                                   :url "https://gist.github.com/jasongilman/d1f70507bed021b48625"}]]
              :emacs [plugin-view [{:name "clj-refactor"
                                    :url "https://github.com/clojure-emacs/clj-refactor.el"}
                                   {:name "paredit"
                                    :url "http://mumble.net/~campbell/emacs/paredit.html"}
                                   {:name "smartparens"
                                    :url "https://github.com/Fuco1/smartparens"}
                                   {:name "rainbow-delimiters"
                                    :url "https://github.com/Fanael/rainbow-delimiters"}]]
              :intellij [plugin-view [{:name "Cursive"
                                       :url "https://cursive-ide.com/"
                                       :status :recommended}]]
              :vim [plugin-view [{:name "vim-clojure-static"
                                  :url "https://github.com/guns/vim-clojure-static"
                                  :group "syntax support"
                                  :note "syntax higlighting (static), indentation, and autocompletion"
                                  :status :recommended}
                                 {:name "vim-clojure-highlight"
                                  :url "https://github.com/guns/vim-clojure-highlight"
                                  :group "syntax support"
                                  :note "syntax highlighting (dynamic) based on runtime information"
                                  :status :optional}
                                 {:name "vim-clojure"
                                  :group "syntax support"
                                  :status :unknown}
                                 {:name "vim-fireplace"
                                  :url "https://github.com/tpope/vim-fireplace"
                                  :group "repl integration"
                                  :note "repl integration, dynamic autocompletion"
                                  :status :recommended}
                                 {:name "vim-sexp"
                                  :url "https://github.com/guns/vim-sexp"
                                  :group "structural editing"
                                  :note "paredit-style editing"
                                  :status :recommended}
                                 {:url "https://github.com/tpope/vim-sexp-mappings-for-regular-people"
                                  :group "structural editing"
                                  :note "mnemonic keyboard shortcuts for vim-sexp"
                                  :status :recommended}
                                 {:url "https://github.com/bhurlow/vim-parinfer"
                                  :group "structural editing"
                                  :note "brackets based on indentation, don't use with vim-sexp"
                                  :status :recommended}]]}]}
     {:heading "Setup a New Clojure Project"
      :all "In a terminal, navigate to some directory in which you'll store your projects.\n\nThen run: `lein new exercises` to create a new clojure project called \"exercises\""}
     {:heading "Run Your Project"
      :vscode
      "In VScode open the created `exercises` folder (File > Open Folder...)

      Open `src/exercises/core.clj` (via the file tree in the sidebar, or, via Ctrl+P and fuzzy search for it)

      'Jack In', ie. start and connect to a project REPL via Ctrl+Alt+C Ctrl+Alt+J , or by clicking on nREPL in the bottom left
      choose Leiningen when prompted
      this should open up a sidebar on the right with the REPL
      (which we won't be using much, mostly we'll be interacting with the running application from our code window)

      Somewhere in `core.clj` write `(+ 1 2)` then evaluate it by Ctrl+Alt+C V (that is, while hold Control and Alt press C, release, then press V) (Ctrl+Option+C E on a Mac)"
      :fallback "No instructions for your editor yet (sorry!). Ask for help.
                Maybe this will help: https://clojurebridgelondon.github.io/workshop/development-tools/editor-guides/"}
     ])

(defn steps-view [steps selected-os selected-editor]
  [:div.steps
   (into [:<>]
         (for [step steps]
           [:div.step
            [:h2 (step :heading)]
            (when-let [instructions (or (step :all)
                                        (step selected-os)
                                        (step selected-editor)
                                        (step :fallback))]
              (if (string? instructions)
                (into [:<>]
                      (parse-backticks instructions))
                instructions))
            (when (step :steps)
              [steps-view (step :steps) selected-os selected-editor])]))])

(defonce selected-os (r/atom :mac))
(defonce selected-editor (r/atom :vscode))

(defn setup-page-view []
  [:div.setup.page {:style {:white-space "pre"}}
   [:h1 "Getting Started"]
   [:p "Select your OS and preferred Editor (if you don't have any preference, we recommend VSCode):"]
   [:div.oses
    "OS:"
    (doall
      (for [os oses]
        ^{:key os}
        [:button {:class (when (= os @selected-os) "active")
                  :style (when (= os @selected-os)
                           {:font-weight "bold"})
                  :on-click (fn []
                              (reset! selected-os os))}
         (name os)]))]
   [:div.editors
    "Editor:"
    (doall
      (for [editor editors]
        ^{:key editor}
        [:button {:class (when (= editor @selected-editor) "active")
                  :style (when (= editor @selected-editor)
                           {:font-weight "bold"})
                  :on-click (fn []
                              (reset! selected-editor editor))}
         (name editor)]))]
   [steps-view steps @selected-os @selected-editor]])
