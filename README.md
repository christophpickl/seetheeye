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

* Component-Scan (not a good idea to do, still could be easily supported)
* Resolving repo by their super-type (was a hard decision, still i think it was the better one)
* anything other than constructor injection (by design!)


TODO
----

* provider should be singletons (by default? configurable?)
* provider should be able to get stuff injected
* install sub configs
** how should be proceeded with re-defining beans?!
* event bus (support observer from supertype)
* completely refactor internals and write proper unit tests
* enhanced validation (check everything on startup, not when getting a specific declaration)
* @Qualifier
* @PostConstruct
* eager singletons instantiation
* override mode (for testing and others)
* NOT YET: AOP/interceptor (investigate proxying!)

LUXURY:
* let user define own declaration name (just used for debugging/error reporting)


NOTES
=====

Just trying to persist some notes I've taken on paper...

GO WITH THE FLOW
----------------

* install
** install sub-configurations
** install concrete beans
*** scope (by default prototype)
*** optionally: as 1-n interfaces
** install instances
*** optionally: as 1-n interfaces
** install provider
*** scope (by default singleton)

* start (no -serious- validation during install to get cumulative error report)
**  build dependency tree (@Inject)
*** check cycles
*** check unresolvables
*** check duplicate definitions
** produce warnings
*** eg: no observer for produced event XY
** optional: eager singletons (would need a new scope or something like that)

* lookup
** return provider directly (not installed by user config); we could do that implicitly (magic)
** delegate to provider
** find bean by type/interface
*** return installed instance
*** optional: cached singleton instance
*** resolve dependencies (=ctor args)
**** wire event bus/get bean
*** invoke @PostConstruct methods
