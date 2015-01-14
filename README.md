## CloGRID

To start developing you need lein > 2.0. After that starting server and developing is straightforward

```bash
lein ring server-headless
```

and the server will reload automatically whenever you change the file. Happy hacking!

## Beware
It's just a proof of concept implementation of grid endpoint in clojure. Nothing more and nothing less.
To show all upsides and downsides we should show:

* how to use our java libraries that we already written (eg. customer-api-adapter-client)
* try to embrace the clojure way (it's better to use simple types instead of creating classes and stuff)
* how to send metrics to graphite
* how to write simple clients of our api in clojure (eg. schedule-client)
* how to write and run tests
* how much different (worse) is work with clojure in IDEA

