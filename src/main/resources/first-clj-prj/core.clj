(ns first-clj-prj/core)

;; when direct use : (require ['clojure.java.io :as 'io]), here is in ns macro,it's not evalate.
;;use add more function than require by using clojure.core/refer
;;Libspecs,A libspec is a lib name or a vector containing a lib name followed by options expressed as sequential keywords and arguments.
;;(use 'your.namespace :reload)

(defn main
  "I don't do a whole lot ... yet."
  [& args]
  ["a" "b" "c"]
  )

