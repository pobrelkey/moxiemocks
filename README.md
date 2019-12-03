moxiemocks
==========

[![Build Status](https://travis-ci.org/pobrelkey/moxiemocks.svg?branch=master)](https://travis-ci.org/pobrelkey/moxiemocks)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.moxiemocks/moxie/badge.png)](http://search.maven.org/#search|gav|1|g%3A%22org.moxiemocks%22%20AND%20a%3A%22moxie%22)

> :warning: **UPDATE 12/2019: Moxiemocks is no longer actively maintained.**  You'll probably want to use [Mockito](https://site.mockito.org/) instead - it's battle-tested, and at any rate their mocking syntax and feature set have improved since I first wrote Moxie.

Moxie is a library for creating mock objects in Java.  It aims to enable prescriptive mock tests with an easy-to-use syntax:

* **Concise** - set up any expectation in one Java statement. Short method names, no anonymous inner classes, no need to "replay" mock objects. 
* **Refactorable** - method name/signature changes using refactoring tools will be reflected in your tests. 
* **Easy** - integrates with JUnit 4 to automatically create and verify mocks on your test objects. (You can still do this manually.) 

Moxie is ovailable through Maven Central:

```xml
<dependency>
    <groupId>org.moxiemocks</groupId>
    <artifactId>moxie</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```

Want to learn more? See...

* [Javadoc](http://pobrelkey.github.io/moxiemocks/mvn/apidocs/) for every public method
* [Moxie by example](https://github.com/pobrelkey/mockdemo/wiki/Syntax-Comparison) - syntax comparison to other major mocking libraries, with a set of equivalent tests implemented in Moxie, JMock 1, JMock 2, EasyMock, Mockito
* [Maven-generated site](http://pobrelkey.github.io/moxiemocks/mvn/) for the latest release (test coverage stats etc.) 

You may also be interested in:

* An informal [performance comparison](https://github.com/pobrelkey/mockdemo/wiki/Performance-Comparison) of the major mocking libraries (executive summary: they all perform just fine)
* [un-jmock1.rb](https://code.google.com/p/moxiemocks/source/browse/scripts/un-jmock1.rb) - fugly, experimental Ruby script I wrote to do most of the donkey work of converting a client's JMock 1/JUnit 3 tests to Moxie/JUnit 4. 

