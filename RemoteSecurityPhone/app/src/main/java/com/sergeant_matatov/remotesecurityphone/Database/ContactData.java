package com.sergeant_matatov.remotesecurityphone.Database;

public class ContactData {
    String nameContact;
    String phoneContact;

    public ContactData() {
    }

    public ContactData(String nameContact, String phoneContact) {
        this.nameContact = nameContact;
        this.phoneContact = phoneContact;
    }

    public String getNameContact() {
        return nameContact;
    }

    public String getPhoneContact() {
        return phoneContact;
    }
}