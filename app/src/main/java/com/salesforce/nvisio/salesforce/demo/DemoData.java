package com.salesforce.nvisio.salesforce.demo;

import com.salesforce.nvisio.salesforce.Model.OutletInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shakil on 1/31/2018.
 */

public class DemoData {

    public Map<String,OutletInformation> outletNameOnly(){
        OutletInformation outletInformation1=new OutletInformation();
        outletInformation1.setOutletName("Satmosjid Road");
        OutletInformation outletInformation2=new OutletInformation();
        outletInformation2.setOutletName("Gulshan 1");

        Map<String,OutletInformation> mapData=new HashMap<>();
        mapData.put("01",outletInformation1);
        mapData.put("02",outletInformation2);
        return mapData;
    }

    public void OutletWithAddress(){
        OutletInformation outletInformation1=new OutletInformation();
        outletInformation1.setOutletName("Satmosjid Road");
        OutletInformation outletInformation2=new OutletInformation();
        outletInformation2.setOutletName("Gulshan 1");
    }
}
