package com.sambufc.mes.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root(name="Row")
public class Row {
    @ElementMap(entry="Col", key="id", attribute=true, inline=true)
    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }


}
