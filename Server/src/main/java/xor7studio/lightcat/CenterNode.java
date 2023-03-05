package xor7studio.lightcat;

import io.javalin.Javalin;
import io.javalin.community.ssl.SSLPlugin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xor7studio.lightcat.auth.Account;
import xor7studio.lightcat.auth.AccountManager;

public class CenterNode {
    private static final Logger logger = LoggerFactory.getLogger("LightCat");
    private final Javalin app;
    private final int port;
    private final AccountManager accountManager=new AccountManager();
    private final Client client=new Client(Protocol.HTTPS);
    private boolean running=false;
    public CenterNode(int p){
        logger.debug("设置监听端口为"+p);
        port=p;
        logger.debug("创建Javalin实例");
        app=Javalin.create(this::setAppConfig);
        app.post("/signin/{server}", this::signinAPI);
        app.error(HttpStatus.NOT_FOUND,ctx -> ctx.redirect("/404.html"));
    }
    private void signinAPI(@NotNull Context ctx){
        String server=ctx.pathParam("server"),
                token=ctx.header("Authorization");
        ctx.result(switch (server) {
            case "microsoft" -> signinMicrosoft(token);
        });
    }
    private void setAppConfig(@NotNull JavalinConfig config){
        config.plugins.register(new SSLPlugin(ssl->{
            logger.debug("设置证书");
            ssl.pemFromPath("cert","key");
            ssl.securePort=port;
            ssl.http2=true;
            ssl.secure=true;
            ssl.insecure=false;
        }));
        config.staticFiles.add("/public");
    }
    @NotNull
    private String signinMicrosoft(String token){
        Request request = new Request(Method.GET,
                "https://graph.microsoft.com/v1.0/me");
        request.getHeaders().add(
                "Authorization",
                "Bearer "+token);
        request.getHeaders().add(
                "Content-Type",
                "application/json");
        Response response = client.handle(request);
        if(!response.getStatus().equals(Status.SUCCESS_OK))
            return "Error";
        String responseBody = response.getEntityAsText();
        System.out.println(responseBody);
        Account account=Account.generate("microsoft","","");
        accountManager.updateAccount(account);
        return account.bearerToken;
    }
    public boolean isRunning(){
        return running;
    }
    public void stop(){
        running=false;
        app.stop();
    }
    public void start(){
        running=true;
        app.start(port);
    }
}
