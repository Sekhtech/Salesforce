package com.salesforce.nvisio.salesforce.Sectioned;

/**
 * Created by USER on 08-May-17.
 */

public interface SectionStateChangeListener {
    void onSectionStateChanged(Section section, boolean isOpen);
}
