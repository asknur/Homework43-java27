package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.tools.javac.Main;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host,8080);

        String msg = "запускаем сервер по адресу" + " http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("  удачно!");
        return server;
    }
}
