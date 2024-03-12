package com.sambufc.mes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.Map;

@Root(name="Rows")
public class Rows {
    @ElementList(entry="Row", inline = true)
    public List<Row> row;
}
