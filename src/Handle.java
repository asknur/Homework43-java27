import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handle {

    public static void initRoutes(HttpServer server) {
        server.createContext("/", Handle::handleRootRequest);
        server.createContext("/apps/", Handle::handleAppsRequest);
        server.createContext("/apps/profile", Handle::handleProfileRequest);
        server.createContext("/index.html", Handle::handleDataRequest);
        server.createContext("/css", Handle::handleDataRequest);
        server.createContext("/image", Handle::handleDataRequest);

    }

    private static void handleRequest(HttpExchange exchange, String msg) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                write(writer, "HTTP Метод", method);
                write(writer, "Запрос", uri.toString());
                write(writer, "Обработан через", ctxPath);

                writeHeaders(writer, "Заголовки запроса", exchange.getRequestHeaders());
                writeData(writer, exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleDataRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Path file = Paths.get("data", path.substring(1));

        if (!Files.exists(file) || Files.isDirectory(file)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String contentType = contentType(file.toString());
        exchange.getResponseHeaders().set("Content-Type", contentType);

        byte[] content = Files.readAllBytes(file);
        exchange.sendResponseHeaders(200, content.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html; charset=utf-8";
        if (fileName.endsWith(".css"))  return "text/css; charset=utf-8";
        if (fileName.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (fileName.endsWith(".png"))  return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            return "image/jpeg";
        return "application/octet-stream";
    }


    private static void handleRootRequest(HttpExchange exchange) {
        handleRequest(exchange, "Hello :) It's Root");
    }

    private static void handleAppsRequest(HttpExchange exchange){
        handleRequest(exchange, "Hello :) It's App");
    }

    private static void handleProfileRequest(HttpExchange exchange){
        handleRequest(exchange, "Hello :) It's Profile");
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);
        try {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static BufferedReader getReader(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    private static void writeData(Writer writer, HttpExchange exchange) {
        try (BufferedReader reader = getReader(exchange)) {
            if (!reader.ready()) {
                return;
            }
            write(writer, "Блок данных", "");
            reader.lines().forEach(v -> write(writer, "\t", v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
