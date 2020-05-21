package com.github.bradleywood;

import java.io.IOException;
import java.net.Socket;

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

    public static Socket getSocket(String address, int port) throws IOException {
        return new Socket(address, port);
    }

    public static void hookBeforeAndAfter() {
        System.out.println("Hello World!");
    }

    public Example() {
        System.out.println("Me neither!");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Output=" + copyMe());
        System.out.println("HookAfterMe: " + hookAfterMe((int)(copyMe() * 1000), (int)(copyMe() * 1000)));

        final Socket socket = getSocket("localhost", 1234);
        System.out.println(socket.isConnected());
    }
}
