(ns exercise-ui.client.ui.setup-page
  (:require
    [clojure.string :as string]))

(def instructions
  "
Install Java
  First, check if you have Java installed.
    In cmd.exe or PowerShell, run: `java -version`. If it works and reports a version of the JDK > 1.8, then you're good to go.

  To install Java:
    The easiest option is via https://adoptopenjdk.net/
      (choose OpenJDK 8 (LTS) & Hotspot)

    Alternative options:
      via cholocatey, if you use it
        https://chocolatey.org/
        `choco install openjdk`
      or various other methods found here: https://stackoverflow.com/questions/52511778/how-to-install-openjdk-11-on-windows

  Check if it's installed:
    In cmd.exe or PowerShell, run: `java -version`. It should report it's version, ex: `openjdk version \"1.8.0_222\"`

Install Leiningen
  If you have chocolatey, just: `choco install lein`

  If not, follow the instructions at: https://leiningen.org/
    (you can save the lein.bat file wherever, perhaps: `C:/Program Files/Leiningen/lein.bat`)
    you will need to add the directory you put `lein.bat` in to your `PATH`
      instructions for Win 10: https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/
    you will need to close/reopen your cmd.exe/PowerShell for the path to update

  Check if it's installed:
    In cmd.exe or PowerShell, run: `lein -v`. It should report it's version, ex. `Leiningen 2.8.1 on Java 1.8.0_222 OpenJDK 64-Bit Server VM`

Install VScode
  Download and install from: https://code.visualstudio.com/

Install Calva extension in VScode
  In VScode, open the extensions search (via the extensiosn icon in the left sidebar, or via Ctrl+Shift+X)
  Search for \"Calva\"
  Click the install button

Set Up a New Project
  In cmd.exe or PowerShell, navigate to some directory in which you'll store your projects (maybe: `C:/Users/your-user/Clojure`)

  Then run: `lein new exercises` to create a new clojure project called \"exercises\"

  In VScode open the created `exercises` folder (File > Open Folder...)

  Open `src/exercises/core.clj` (via the file tree in the sidebar, or, via Ctrl+P and fuzzy search for it)

  'Jack In', ie. start and connect to a project REPL via Ctrl+Alt+C Ctrl+Alt+J , or by clicking on nREPL in the bottom left
    choose Leiningen when prompted
    this should open up a sidebar on the right with the REPL
      (which we won't be using much, mostly we'll be interacting with the running application from our code window)

  Somewhere in `core.clj` write `(+ 1 2)` then evaluate it by Ctrl+Alt+C V")

(defn setup-page-view []
  [:div {:style {:white-space "pre"}}
   instructions])


