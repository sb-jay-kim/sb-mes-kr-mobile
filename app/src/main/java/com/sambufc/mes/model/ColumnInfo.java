package com.sambufc.mes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Map;

@Root(name="ColumnInfo")
public class ColumnInfo {

//    @ElementMap(entry="Column", key="id", attribute=true, inline=true)
//    private Map<String, String> map;
//
//    public Map<String, String> getMap() {
//        return map;
//    }
    @ElementList(entry="Column")
    private List<Column> map;

    public List<Column> getMap() {
        return map;
    }
}
