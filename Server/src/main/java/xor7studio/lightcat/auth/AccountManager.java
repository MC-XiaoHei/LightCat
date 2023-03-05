package xor7studio.lightcat.auth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class AccountManager {
    private static final String PRIVATE_KEY_FILE =
            "PRIVATE_KAY";
    private static final String PUBLIC_KEY_FILE =
            "PUBLIC_KEY";
    private HikariDataSource dataSource;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    public AccountManager(){
        try{
            privateKey = getPrivateKey();
            publicKey = getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initDatabase();
    }
    public void updateAccount(Account account){

    }
    private Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void initDatabase(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:account.db");
        config.setMaximumPoolSize(64);
        config.setMinimumIdle(16);
        config.setConnectionTimeout(5000);
        dataSource = new HikariDataSource(config);
    }
    static String generateToken(String data) {
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L);
        return Jwts.builder()
                .setSubject(data)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }
    @Nullable
    private String readToken(String token) {
        try {
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    private PrivateKey getPrivateKey() throws Exception {
        File privateKeyFile = new File(PRIVATE_KEY_FILE);
        if (privateKeyFile.exists()) {
            byte[] keyBytes = FileUtils.readFileToByteArray(privateKeyFile);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } else {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            FileOutputStream outputStream = new FileOutputStream(privateKeyFile);
            outputStream.write(privateKey.getEncoded());
            outputStream.close();
            return privateKey;
        }
    }
    private PublicKey getPublicKey() throws Exception {
        File publicKeyFile = new File(PUBLIC_KEY_FILE);
        if (publicKeyFile.exists()) {
            byte[] keyBytes = FileUtils.readFileToByteArray(publicKeyFile);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } else {
            PrivateKey privateKey = getPrivateKey();
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(privateKey.getEncoded()));
            FileOutputStream outputStream = new FileOutputStream(publicKeyFile);
            outputStream.write(publicKey.getEncoded());
            outputStream.close();
            return publicKey;
        }
    }

}
