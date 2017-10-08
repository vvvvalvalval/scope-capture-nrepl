(ns sc.nrepl.middleware
  (:require [clojure.tools.nrepl.middleware :as nrm]
            [sc.nrepl.impl :as i]))

(defn wrap-letsc
  [handler]
  (fn [& args]
    (i/wrap-handle handler (vec args))))

(nrm/set-descriptor! #'wrap-letsc
  {:expects #{"load-file" "eval"}})
