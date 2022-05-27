# Misaliased

Identify inconsistenly named NS aliases across a Clojure project. Then
fix them youself.

## About

In short, this is really easy and cool and you should just try it out
on your projects.

This will take a minute or so to run over 1,000 sizable .clj files.

An example output for a given set of namespace aliases:

``` shell


% ./misaliased.clj ~/work/myproj/src/clj/some/nested/area/**.clj(.)
...
ERROR: clojure.string
myproj.communication.some-docs 	->	s
myproj.roles 	->	str
myproj.route 	->	str
myproj.token 	->	str
myproj.search 	->	string
...
```

That shows that across those `myproj` namespaces, someone decided to
use:
```
(ns myproj.communication.some-docs
  (:require [clojure.string :as s] ...))
```

and someone else:

```
(ns myproj.roles
  (:require [clojure.string :as str] ...))
  ```

and yet someone else:

```
(ns myproj.search
  (:require [clojure.string :as string] ...))
```

That's a common problem where no one can decide as to what they want
to alias `clojure.string`. But it runs deeper. On a 1,000-file project
I'm looking at now, there are over 200 inconsistent alias sections!
And I've needed this on several other projects. Most often, there will
be a handful of consistent ones, with a couple that don't follow suit.
Those are the ones you wanna fix to make your refactoring and grepping
much better.

## Installation

It's [Babashka](https://github.com/babashka/babashka) with a
[Parcera](https://github.com/carocad/parcera)
[Pod](https://github.com/babashka/pod-babashka-parcera). You'll need
to put that Pod executable on your `PATH`.

## Further Recommendations

Get your house in order and fix all the aliases that this identifies
for you. Then talk to your team about standardizing.

You can even put a comment on each NS that indicates its canonical
form.

You can even start using those canonical forms to do something like
"magic requires" to maintain future consistency.

If your code base is really really big, you may want to run over
shallower directories. Here's a simple way to do that, selecting just
clj files below a dir tree:

## Contributing

This was hacked together in an afternoon with a single large code
base example. If it doesn't work for you, uncomment some of the print
statements and check the output for any lines that look abnormal. Fix
your NS and then rerun.
