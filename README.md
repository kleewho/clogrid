## CloGRID

To start developing you need lein > 2.0. After that starting server and developing is straightforward

```bash
SCHEDULE_URL=http://example.appdev.io/kraken/v2/schedule/data lein ring server-headless
```

and the server will reload automatically whenever you change the file. Happy hacking!

### Beware
It's just a proof of concept implementation of grid endpoint in clojure. Nothing more and nothing less.
To show all upsides and downsides we should show:

* how to use our java libraries that we already written (eg. customer-api-adapter-client)
* try to embrace the clojure way (it's better to use simple types instead of creating classes and stuff)
* how to send metrics to graphite
* how to write simple clients of our api in clojure (eg. schedule-client)
* how to write and run tests
* how much different (worse) is work with clojure in IDEA
* show exception/error handling

## Clojure - few things for a start

### Defining things

* `(def name value)` - defines a constant
* `(defn name [params] (body))` - defines  a function

### Data structures literals:

* `'(v)` - list
* `[v]` - vector
* `{:k v}` - map
* `#{v}` - set

### Function/macro calls:
`(function-name arg1 arg2 arg3)` - function calls looks like a list

### Binding forms (e.g. local "variables")
Binding forms are in `[]`. Most commonly they can be found in function definitions
as a list of function params or in special form `let`.


```clojure
(defn name [a] (print (str "Hello" a)))
```

a is bound to value passed as a first arg


```clojure
(defn name [a]
(let [msg (str "Hello" a)]
(print msg)))
```

msg is bound to result of computing `(str "Hello" a)` and within let scope msg name will be known

## Ring - web applications library

First of all couple of not so long lectures:

* [Ring concepts](https://github.com/ring-clojure/ring/wiki/Concepts)
* [Introduction](http://drtom.ch/posts/2012-12-10/An_Introduction_to_Webprogramming_in_Clojure_-_Ring_and_Middleware/) - very interesting diagrams

In our case routing is in `handler.clj`, but the actual logic of this is in `grid.clj` and the middleware is in `params.clj`.
