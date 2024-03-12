package com.sambufc.mes.model;

import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Map;

@Root(name="Dataset")
public class Data {

//    @Element(name="ColumnInfo")
//    public ColumnInfo columnInfo;

    @Attribute
    public String id;
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    @Path("ColumnInfo")
    @ElementMap(entry="Column", key="id", attribute=true, inline=true)
    @Nullable
    public Map<String, String> map;

//    public Map<String, String> getMap() {
//        return map;
//    }

    @Element(name="Rows")
    @Nullable
    public Rows rows;
}
