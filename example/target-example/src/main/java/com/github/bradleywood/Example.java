package com.github.bradleywood;

public class Example {

    static {
        System.out.println("Don't replace me");
    }

    public static void replaceMe() {
        System.out.println("You failed to replace me");
    }

    public static void dontReplaceMe() {
        System.out.println("Please don't replace me!");
    }

    public static String addStrings(String a, String b) {
        return a + b;
    }

    public static double copyMe() {
        return Math.random();
    }

    public static int hookAfterMe(int a, int b) {
        if (a < b) {
            return a * b;
        } else if (a != -b) {
            return a * b / (a - b);
        } else {
            return (int) Math.round(Math.pow(a, b));
        }
    }

    public Example() {
        System.out.println("Me neither!");
    }

    public static void main(String[] args) {
        System.out.println("Output=" + hookAfterMe(50, 25));
    }
}
