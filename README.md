# mt2

micro twitter version 2.

I wrote `micro twitter` in CommonLisp using Hunchentoot/Huncheksocket library.
This was a remake of it using Clojure/Sente.
I must learn a lot more.

## **NB**

Sometimes browser seems to cache main.js.
In that case, exec `Clear Browsing Data...`,
this is Opera's menu, to flush old main.js out.

## Developing

### Setup

When you first clone this repository, run:

```sh
lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.http-kit/starting-server {:port 3040}
:initiated
```

This creates a web server at <http://localhost:3040>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server. Changes to CSS or ClojureScript
files will be hot-loaded into the browser.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

If you want to access a ClojureScript REPL, make sure that the site is loaded
in a browser and run:

```clojure
dev=> (cljs-repl)
Waiting for browser connection... Connected.
To quit, type: :cljs/quit
nil
cljs.user=>
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

## Legal

Copyright © 2020 hkimura


