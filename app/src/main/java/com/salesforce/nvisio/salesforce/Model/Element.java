
package com.salesforce.nvisio.salesforce.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Element {

    @SerializedName("distance")
    @Expose
    public Distance distance;
    @SerializedName("duration")
    @Expose
    public Duration duration;
    @SerializedName("duration_in_traffic")
    @Expose
    public DurationInTraffic durationInTraffic;
    @SerializedName("status")
    @Expose
    public String status;

}
