package com.salesforce.nvisio.salesforce.Sectioned;

/**
 * Created by USER on 08-May-17.
 */

public class section_item {
    public section_item() {
    }
    public String name;
    public int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public section_item(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
