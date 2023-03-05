package xor7studio.lightcat.auth;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Account {
    public String username;
    public String authServer;
    public String userId;
    public String bearerToken;
    public int ID;
    @NotNull
    @Contract(pure = true)
    public static Account generate(String authServer,String username,String userId){
        Account account=new Account();
        account.username=username;
        account.authServer=authServer;
        account.userId=userId;
        account.bearerToken=AccountManager.generateToken(username+":"+userId+":"+authServer);
        return account;
    }
}
