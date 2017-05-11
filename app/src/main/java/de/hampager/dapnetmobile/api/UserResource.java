package de.hampager.dapnetmobile.api;

public class UserResource {
    private String name;
    private String mail;
    private boolean admin;
    private String hash;

    public UserResource(String name, String mail, boolean admin) {
        this.name=name;
        this.mail=mail;
        this.admin = admin;
    }

    public boolean admin() {
        return admin;
    }

    public String toString() {
        return "Name: " + name + ", Mail: " + mail + ", admin: " + admin + ", Hash: " + hash;
    }
}
