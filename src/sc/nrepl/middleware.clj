(ns sc.nrepl.middleware
  (:require [sc.nrepl.impl :as i]
            [sc.nrepl.repl]))

;; Support both new & legacy nrepl versions
(if (find-ns 'nrepl.middleware)
  (require '[nrepl.middleware :as nrm]) ;; newer nrepl 0.5.3 and lein 2.8.3+
  (require '[clojure.tools.nrepl.middleware :as nrm])) ;; old tools.nrepl and earlier lein versions

(defn wrap-letsc
  [handler]
  (fn [& args]
    (i/wrap-handle handler (vec args))))

(nrm/set-descriptor! #'wrap-letsc
  {:expects #{"load-file" "eval"}})
