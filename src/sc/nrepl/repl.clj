(ns sc.nrepl.repl
  (:require [sc.nrepl.impl :as i]
            [sc.impl.db :refer [db]]))

(defn in-ep
  "'In Execution Point' - Sets the current Execution Point Id to `ep-id`.
  Subsequent commands sent to the REPL via 'eval' or 'load-file'
  will be transformed so that each form `expr` is wrapped with (sc.api/letsc epid expr)"
  [ep-id]
  (when (or (nil? ep-id)
            (sc.impl/find-ep @db ep-id))
    (reset! i/current-ep-id ep-id))
  nil)

(defn exit
  "Exists the context of the current Execution Point, if any. Idempotent."
  []
  (in-ep nil))
