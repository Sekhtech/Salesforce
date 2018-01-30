
package com.salesforce.nvisio.salesforce.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistanceApiResponse {

    @SerializedName("destination_addresses")
    @Expose
    public List<String> destinationAddresses = null;
    @SerializedName("origin_addresses")
    @Expose
    public List<String> originAddresses = null;
    @SerializedName("rows")
    @Expose
    public List<Row> rows = null;
    @SerializedName("status")
    @Expose
    public String status;

}
