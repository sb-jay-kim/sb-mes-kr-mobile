package com.sambufc.mes.model;

import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="Root")
public class MesResponse extends BaseModel{

    @ElementList(entry="Dataset",inline = true)
    @Nullable
    public List<Data> dataset;

//    @ElementMap(entry="Dataset", key="id", attribute=true, inline=true)
//    public Map<String, Data> dataset;

//    @Path("Dataset/Rows/Row[0]")
//    @Element(name="USER_NM_KOR")
//    private String userNameKor;
}
