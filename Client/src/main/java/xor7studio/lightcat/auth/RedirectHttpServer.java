package xor7studio.lightcat.auth;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class RedirectHttpServer {
    private final int port;
    private volatile String accessToken=null;
    public RedirectHttpServer(){
        HttpHandler handle = httpExchange -> {
            httpExchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");
            String html = """
            <html>
                <head>
                    <title>LightCat Network</title>
                </head>
                <body>
                    <script language="javascript">
                        var currentUrl = window.location.href;
                        var newUrl = currentUrl.replace('#', '?');
                        if(newUrl != currentUrl){
                            window.location.href = newUrl;
                        }
                    </script>
                    <h1>微软登录完成，您可以放心的关闭此页面。</h1>
                </body>
            </html>
            """;
            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
            String uri=httpExchange.getRequestURI().toString();
            if(uri.contains("access_token"))
                accessToken=uri
                        .replace("/?access_token=","")
                        .split("&")[0];
        };
        HttpServer server;
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
            server = HttpServer.create(
                    new InetSocketAddress(port),0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", handle);
        server.setExecutor(null);
        server.start();
    }

    public int getPort() {
        return port;
    }
    public String getAccessToken(){
        while (accessToken == null)
            Thread.onSpinWait();
        return accessToken;
    }
}
