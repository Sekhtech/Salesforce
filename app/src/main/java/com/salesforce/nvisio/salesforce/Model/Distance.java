
package com.salesforce.nvisio.salesforce.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance {

    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("value")
    @Expose
    public Integer value;

}
