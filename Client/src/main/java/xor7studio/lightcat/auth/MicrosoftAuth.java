package xor7studio.lightcat.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrosoftAuth {
    private static final Logger logger = LoggerFactory.getLogger("LightCat");

    public void getToken(){
        int port,nonce=(int)((Math.random()*9+1)*100000);
        logger.debug("�����nonce��Ϊ"+nonce);
        logger.info("��������΢��OAuth�����ض���Http������...");
        RedirectHttpServer server = new RedirectHttpServer();
        port = server.getPort();
        logger.info("�ض���������ڶ˿�"+port+"�������ɹ�");
        String microsoftLoginUri=
                "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize?" +
                        "client_id=153b7e99-4545-4c18-b1c1-619121bb36b6&" +
                        "response_type=token&" +
                        "redirect_uri=http%3A%2F%2Flocalhost%3A"+port+"&" +
                        "nonce="+nonce+"&" +
                        "prompt=select_account&" +
                        "scope=User.Read";
        logger.info("��¼URI������ϣ�"+microsoftLoginUri);
        logger.info("�ȴ��û���¼���...");
        logger.info(server.getAccessToken());
    }
}
