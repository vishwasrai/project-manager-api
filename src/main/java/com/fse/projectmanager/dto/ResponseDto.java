package com.fse.projectmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto {
    @JsonProperty("result")
    private String result;

    @JsonProperty("data")
    private Object object;

    @JsonProperty("dataList")
    private Object objectList;

}
