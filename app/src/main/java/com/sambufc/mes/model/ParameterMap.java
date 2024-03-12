package com.sambufc.mes.model;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root(name="Parameters")
public class ParameterMap {
    @ElementMap(entry="Parameter", key="id", attribute=true, inline=true)
    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }
}
