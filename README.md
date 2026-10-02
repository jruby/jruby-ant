# jruby-ant

Ant tasks that run [Rake](https://github.com/ruby/rake) from an Ant build, the opposite
direction of the [rake-ant](https://github.com/jruby/rake-ant) gem (which calls Ant from a
Rakefile). These tasks used to live in JRuby itself as `org.jruby.ant`.

```xml
<taskdef name="rake" classname="org.jruby.ant.Rake"/>
<taskdef name="rake-import" classname="org.jruby.ant.RakeImport"/>

<rake file="Rakefile" task="default"/>
<rake-import file="Rakefile"/>
```

## Maven

```xml
<dependency>
  <groupId>org.jruby</groupId>
  <artifactId>jruby-ant</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Requirements

`jruby-base`/`jruby-complete` and `ant` are `provided`: put them on Ant's classpath yourself,
along with JRuby's standard library (for Rake).

The Ruby half, the [rake-ant](https://github.com/jruby/rake-ant) gem, is bundled into the
jar (its `lib` files sit at the jar root), so there is no separate gem to install. The version
is `rake-ant.version` in `pom.xml`.

rake-ant finds Ant's jars through `ANT_HOME` or by running `ant -diagnostics`, so Ant must be
installed (`ANT_HOME` set or `ant` on `PATH`).

## Building

```
./mvnw verify
```

The build downloads the rake-ant gem from rubygems.org through the
[mavengem](https://github.com/jruby/mavengem-wagon) wagon (`.mvn/extensions.xml`), installs it
with `gem-maven-plugin` and copies its `lib` into `target/classes`. Tests are unit tests against
a stub `RakeWrapper` plus an Ant -> Rake integration test (skipped without Ant installed).

## Releasing

Releases use the Maven release plugin. The plugin runs `deploy` with the `release` profile
(sources and javadoc jars, GPG signing, upload to the Central Portal), so GPG and the `central`
server credentials must be set up in `settings.xml`:

```
./mvnw release:prepare
./mvnw release:perform
```
