README
======

See The Eye is a Java implementation of CDI (specified in JSR 299), also known as _Contexts and Dependency Injection_, just for fun ;)

FEATURES
--------

* Java Config
    * Installing concrete declaration classes (as interfaces)
    * Installing instances (as interfaces)
    * Installing providers
    * Installing by scopes (singleton, prototype) or use @Singleton
* Injection
    * Support of @Inject annotation
    * If only one constructor existing, using that one
* Minor
    * Supporting non-public types/methods

NON-FEATURES
------------

* Component-Scan
* Resolving repo by their super-type (was a hard decision)


TODO
----

* provider should be singletons (by default? configurable?)
* provider should be able to get stuff injected
* install sub configs
** how should be proceeded with re-defining beans?!
* event bus
* completely refactor internals and write proper unit tests
* enhanced validation (check everything on startup, not when getting a specific declaration)
* @Qualifier
* NOT YET: AOP/interceptor

LUXURY:
* let user define own declaration name (just used for debugging/error reporting)