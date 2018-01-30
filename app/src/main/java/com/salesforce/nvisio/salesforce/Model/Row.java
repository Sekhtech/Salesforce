
package com.salesforce.nvisio.salesforce.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Row {

    @SerializedName("elements")
    @Expose
    public List<Element> elements = null;

}
