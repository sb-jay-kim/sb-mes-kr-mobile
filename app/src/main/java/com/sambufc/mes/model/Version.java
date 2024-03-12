package com.sambufc.mes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Version
{
    @JsonProperty("ver")
    public int ver;

    @JsonProperty("size")
    public int size;

    @JsonProperty("name")
    public String name;

    @JsonProperty("etc")
    public String etc;
}
