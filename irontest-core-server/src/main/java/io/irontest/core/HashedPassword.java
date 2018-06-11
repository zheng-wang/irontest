package io.irontest.core;

public class HashedPassword {
    private String hashedPassword;    //  base64 encoded
    private String salt;              //  base64 encoded

    public HashedPassword(String hashedPassword, String salt) {
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getSalt() {
        return salt;
    }
}
