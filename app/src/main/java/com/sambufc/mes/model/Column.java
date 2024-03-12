package com.sambufc.mes.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="Column")
public class Column {
    @Attribute
    private String id;
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    @Attribute
    private String type;
    public String getType(){ return type; }
    public void setType(String type){ this.type = type; }

    @Attribute
    private String size;
    public String getSize(){ return size; }
    public void setSize(String size){ this.size = size; }
}
