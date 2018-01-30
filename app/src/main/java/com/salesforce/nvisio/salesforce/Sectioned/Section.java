package com.salesforce.nvisio.salesforce.Sectioned;

/**
 * Created by USER on 08-May-17.
 */

public class Section {
    private final String name;

    public boolean isExpanded;

    public Section(String name) {
        this.name = name;
        isExpanded = false;
    }

    public String getName() {
        return name;
    }
}
