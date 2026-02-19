import com.sun.net.httpserver.HttpServer;

import java.io.*;
import static server.Server.makeServer;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            Handle.initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}