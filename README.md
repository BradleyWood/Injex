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

## Annotations

Annotations are to be used to accomplish the following tasks

 - Chance class inheritance with @Extends or @Implements
 - Inject a method or field with @Inject
 - Replace or copy and existing method with @Replace or @Copy
 - Add method hooks with @HookBefore or @HookAfter
 - Shadow an existing field by declaring one with @Shadow
 - Replace instantiation operations of certain types with @ReplaceInstantiation
 - Inject code into a method at a specific line with @InlineHookAt

### @Inject

The @Inject annotation may be applied to both fields and methods. Its job
is to inject the entity into the target class.

### @Replace
The @Replace annotation is used to replace a method in the target class.
A method annotated with @Replace must have the same method descriptor as
the method it wishes to replace. The method may have a different name than
as defined in the target class.

#### Example

```java
@Replace("replaceMe")
public static void replaceMe() {
    System.out.println("You have been replaced!");
}
```


### @InlineHookAt

This annotation allows you to inject code into a target method
at a specific line.

#### Target Code

This is the decompiled source code of the target method.

```java
public static void printN(String msg, int n) {
    for (int i = 0; i < n; i++) {               // line 11
        System.out.print(msg);                  // line 12
        System.out.print(" ");                  // line 13
    }
    System.out.println();
}
```

#### Injex Example

This example will insert the block of code at line 13 in the target
method shown above. The arguments defined in the method below must
match the variables that are on the stack at line 13. 'n' and 'i'
are omitted because they are not used.

```java
@InlineHookAt(value = "printN", desc = "(Ljava/lang/String;I)V", line = 13)
public static void printN(String msg) {
    if (Math.random() < 0.5) {
        System.out.print(new StringBuilder(msg).reverse());
    }
}
```

#### Result

This is the decompiled view of the resulting method.

```java
public static void printN(String msg, int n) {
    for (i = 0; i < n; i++) {
        System.out.print(msg);
        if (Math.random() < 0.5D)
            System.out.print((new StringBuilder(msg)).reverse()); 
        System.out.print(" ");
    }
    System.out.println();
}
```
