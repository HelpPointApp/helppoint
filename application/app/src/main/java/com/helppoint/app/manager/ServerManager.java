package com.helppoint.app.manager;

import android.content.Context;

import com.helppoint.app.model.*;
import com.parse.Parse;
import com.parse.ParseObject;

public class ServerManager {

    private static ServerManager instance;

    public static ServerManager getInstance(){
        if(instance == null){
            instance = new ServerManager();
        }

        return instance;
    }

    public void initialize(Context applicationContext){
        registerSubclasses();

        Parse.initialize(new Parse.Configuration.Builder(applicationContext)
                .applicationId("Lw7z2ythkSBQRww4bn9Rb5Zb15Ss202TYgrcfFdE")
                .clientKey("fNt452wjdnk0sxbrA5SixH8DhFNJLCU0BggEiYAO")
                .server("https://parseapi.back4app.com").build()
        );
    }

    private void registerSubclasses(){
        ParseObject.registerSubclass(Help.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Rating.class);
    }
}
