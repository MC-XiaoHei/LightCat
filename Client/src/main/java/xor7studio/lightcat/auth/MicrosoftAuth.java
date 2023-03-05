package xor7studio.lightcat.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrosoftAuth {
    private static final Logger logger = LoggerFactory.getLogger("LightCat");

    public void getToken(){
        int port,nonce=(int)((Math.random()*9+1)*100000);
        logger.debug("将随机nonce设为"+nonce);
        logger.info("正在启动微软OAuth本地重定向Http服务器...");
        RedirectHttpServer server = new RedirectHttpServer();
        port = server.getPort();
        logger.info("重定向服务器在端口"+port+"上启动成功");
        String microsoftLoginUri=
                "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?" +
                        "client_id=153b7e99-4545-4c18-b1c1-619121bb36b6&" +
                        "response_type=token&" +
                        "redirect_uri=http%3A%2F%2Flocalhost%3A"+port+"&" +
                        "nonce="+nonce+"&" +
                        "prompt=select_account&" +
                        "scope=User.Read";
        logger.info("登录URI构建完毕："+microsoftLoginUri);
        logger.info("等待用户登录完成...");
        logger.info(server.getAccessToken());
    }
}
