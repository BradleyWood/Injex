package com.github.bradleywood;

import com.github.bradleywood.injex.annotations.*;

@InjexTarget(name = "com.github.bradleywood.Example")
public class Example {

    @Replace("replaceMe")
    public static void replaceMe() {
        System.out.println("You have been replaced!");
    }

    @HookBefore("dontReplaceMe")
    public static void hookBefore() {
        System.out.println("Okay, I wont replace you");
    }

    @HookBefore("addStrings")
    public static String addStringsHook(String a, String b) {
        System.out.println("Add strings hooked! a=\"" + a + "\", b=\"" + b + "\"");
        return a + b;
    }

    @Copy("copyMe")
    public static double copy$copyMe() {
        throw new RuntimeException();
    }

    @Replace("copyMe")
    public static double replacement() {
        return copy$copyMe() + 10;
    }

    @HookAfter("hookAfterMe")
    public static int mathHook(int a, int b) {
        System.out.println("Math function hooked");
        return Integer.MAX_VALUE;
    }

    public void dontInjectThis() {
        System.out.println("Hello, world");
    }

    @Inject
    public void injectThis() {
        System.out.println("We have been injected: " + mathHook(50, 25));
    }

}
