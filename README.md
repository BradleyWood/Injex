# Injex

Injex is a framework to simplify JVM bytecode injection and manipulation. By annotating
your code you may inject, hook, copy and replace existing code with ease.


## Project Layout

- [example](/example) - An example project

- [injex-maven-plugin](/injex-maven-plugin) - A plugin for maven integration

- [injex](/injex/src/main/java/com/github/bradleywood/injex) - The core library

## Build

```
git clone https://github.com/BradleyWood/Injex.git
```

```
mvn install
```

## Execution Instructions

Maven integration is coming soon. For now, you will have to use the command line
tool for code injection.

```
java -jar injex.jar [source-jar] [target-jar] [output-jar]
```

