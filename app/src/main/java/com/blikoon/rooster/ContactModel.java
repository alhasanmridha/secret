package com.blikoon.rooster;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gakwaya on 4/16/2016.
 */
public class ContactModel {

    private static ContactModel sContactModel;
    private List<Contact> mContacts;

    public static ContactModel get(Context context)
    {
        if(sContactModel == null)
        {
            sContactModel = new ContactModel(context);
        }
        return  sContactModel;
    }

    private ContactModel(Context context)
    {
        mContacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    private void populateWithInitialContacts(Context context)
    {
        //Create the Foods and add them to the list;


        Contact contact3 = new Contact("alhasan1@ckotha.com");
        mContacts.add(contact3);
        Contact contact5 = new Contact("alhasan@ckotha.com");
        mContacts.add(contact5);
        Contact contact1 = new Contact("alhasan@localhost");
        mContacts.add(contact1);
        Contact contact4 = new Contact("alhasantest@ckotha.com");
        mContacts.add(contact4);
        Contact contact2 = new Contact("alhasantest@localhost");
        mContacts.add(contact2);
        Contact contact6 = new Contact("User6@server.com");
        mContacts.add(contact6);
        Contact contact7 = new Contact("User7@server.com");
        mContacts.add(contact7);
    }

    public List<Contact> getContacts()
    {
        return mContacts;
    }

}
