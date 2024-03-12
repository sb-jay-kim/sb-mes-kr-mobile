package com.sambufc.mes.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class BaseModel {

    @Element(name="Parameters")
    public ParameterMap parameters;
//    @ElementList(name="Parameters")
//    private List<Parameter> parameters;
//
//
//    public List getParameters() {
//        return parameters;
//    }
//
//    public void setParameters(List<Parameter> parameters) {
//        this.parameters = parameters;
//    }
}
