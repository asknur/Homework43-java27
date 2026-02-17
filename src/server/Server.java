package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(8080);

        String msg = "запускаем сервер по адресу\" + \" http://%s:%s/%n";
        System.out.printf(msg, address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("  удачно!");
        return server;
    }

    public void run() {
        try {
            HttpServer server = makeServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
