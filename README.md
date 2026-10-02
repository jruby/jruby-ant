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

`jruby-base`/`jruby-complete` and `ant` are `provided`: put them on Ant's classpath yourself.
The tasks also need:

* JRuby's standard library (Rake) and the `rake-ant` gem (`jruby -S gem install rake-ant`),
  loadable from JRuby's gem path or `RUBYLIB`.
* Ant installed (`ANT_HOME` or `ant` on `PATH`): the rake-ant gem uses it to find Ant's jars.

## Building

```
./mvnw verify                                     # unit tests against a stub RakeWrapper
./mvnw verify -Drake.ant.lib=/path/to/rake-ant/lib   # also runs the real Ant -> Rake test
```

Releases: `./mvnw -Prelease deploy` attaches sources and javadoc, signs with GPG and uploads to
the Central Portal (`central` server credentials in `settings.xml`).
