package xor7studio.lightcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xor7studio.lightcat.auth.MicrosoftAuth;
import xor7studio.lightcat.auth.RedirectHttpServer;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("LightCat");

    public static void main(String[] args) {
        new MicrosoftAuth().getToken();
    }
}