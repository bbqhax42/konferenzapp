package com.example.chris.konferenz_app;

/**
 * Created by Chris on 01.06.2017.
 */

public class Interestgroup {
    String name;
    boolean visible;



    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public Interestgroup(String name, boolean visible) {
        this.name = name;
        this.visible = visible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
