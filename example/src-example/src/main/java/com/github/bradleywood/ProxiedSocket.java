package com.github.bradleywood;

import com.github.bradleywood.injex.annotations.ReplaceInstantiation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * Forces socket use to be proxied
 */
@ReplaceInstantiation(Socket.class)
public class ProxiedSocket extends Socket {

    public ProxiedSocket() {
        super(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050)));
    }

    public ProxiedSocket(final String host, final int port) throws IOException {
        this();
        connect(new InetSocketAddress(host, port));
    }

}
