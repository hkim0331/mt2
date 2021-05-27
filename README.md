# mt2

micro twitter version 2.

I wrote `micro twitter` in CommonLisp using Hunchentoot/Huncheksocket library.
`mt2` is a remake of it by using Clojure/Sente.
I must learn a lot more.

## **NB**

Browser caches `main.js`.
In that case, must exec `Clear Browsing Data...` to flush old main.js out.
The menu is Opera's.

## Environment

see mt2.handler.mt2/login-post. provide appropriate env-vars
before starting app.

## start
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
Take 10 or 15 seconds before start mt2.
FIXME: Until server starts, ClojureScript show some warning messages.
Have not fixed yet.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server. Changes to CSS or ClojureScript
files will be hot-loaded into the browser. Do `Clear Browser Data`.

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
