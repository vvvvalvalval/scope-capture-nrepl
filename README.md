# scope-capture-nrepl

[![Clojars Project](https://img.shields.io/clojars/v/vvvvalvalval/scope-capture-nrepl.svg)](https://clojars.org/vvvvalvalval/scope-capture-nrepl)

A companion library to [scope-capture](https://github.com/vvvvalvalval/scope-capture), providing an nREPL middleware that lets you put your REPL in the context of an Execution Point (via `sc.api/letsc`). 

Project status: alpha quality. Tested empirically on Cursive. OTOH, this is typically only used in development,
 and for most purposes you can just falling back to using the raw API of scope-capture.

## Installation

This library exposes an [nREPL middleware](https://nrepl.org/nrepl/design/middleware.html) in the Var `sc.nrepl.middleware/wrap-letsc`.

It works with both the [nREPL](https://nrepl.org/nrepl/index.html) library and the older [tools.nrepl](https://github.com/clojure/tools.nrepl):
 you must provide these dependencies separately.

#### Via deps.edn

You need to declare dependencies both to this library and to nREPL itself.
 You'll typically want to put them in a custom profile, as [recommended](https://nrepl.org/nrepl/usage/server.html#_using_clojure_cli_tools) by the nREPL documentation:

```clojure
;; in your deps.edn file:
{
 ;; [...]
 :aliases
 {
  ;; [...]
  :nREPL
  {:extra-deps
   {nrepl/nrepl {:mvn/version "0.5.3"}
    vvvvalvalval/scope-capture-nrepl {:mvn/version "0.2.0"}}}}}
```

#### Via Leiningen 

Add the following to your project.clj file (potentially in a development profile):

```clojure
  :dependencies 
  [... ;; you probably have other dependencies 
   [vvvvalvalval/scope-capture-nrepl "0.2.0"]]
  :repl-options
  {:nrepl-middleware
   [... ;; you may have other nREPL middleware 
    sc.nrepl.middleware/wrap-letsc]}
```

## Usage

Assume you placed a `sc.api/spy` (or `sc.api/brk`) call in the following code:

```clojure 
(defn foo
  [x y]
  (let [z (* x y)]
    (sc.api/spy  
      (+ (* x x) (* 2 z) (* y y)))))
;SPY <-3> /home/me/myapp/src/myapp/myns.clj:4 
;  At Code Site -3, will save scope with locals [x y z]
```

You ran it and got an Execution Point with id `7`:

```clojure 
(foo 2 23)
;SPY [7 -3] /home/me/myapp/src/myapp/myns.clj:4 
;  At Execution Point 7 of Code Site -3, saved scope with locals [x y z]
;SPY [7 -3] /home/me/myapp/src/myapp/myns.clj:4 
;(+ (* x x) (* 2 z) (* y y))
;=>
;625
```

You can now 'place yourself' in the context of that Execution Point by calling `sc.nrepl.repl/in-ep`

```clojure
(sc.nrepl.repl/in-ep 7)
```

Once you've done that, you'll see that the locals bindings of the Execution Point are always in scope, although not via Global Vars:

```clojure
x 
=> 2 

y 
=> 23

z
=> 26

(+ x z)
=> 28
```

This is achieved by wrapping each code expression to evaluate (via the 'eval' and 'load-file' nREPL ops) with `(sc.api/letsc <<ep-id>> <<expr>>)`.

So the semantics are exactly those of `sc.api/letsc`, you just don't get the tedium of writing them manually.

Once you're done with that Execution Point, you put your REPL back in a normal state by using `sc.nrepl.repl/exit`:

```clojure
(sc.nrepl.repl/exit)
```

## License

Copyright © 2017 Valentin Waeselynck and contributors.

Distributed under the MIT License.
