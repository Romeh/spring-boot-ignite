package com.romeh.ignitemanager.entities;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by romeh on 19/07/2017.
 */
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AlertEntry implements Serializable {
    @ApiModelProperty(notes = "the key value alert content for error description required to be entered by user into REST API ")
    private Map<String,String> alertContent;
    @ApiModelProperty(notes = "alert error code required to be entered by user into REST API ", required = true)
    @QuerySqlField(index = true)
    @NotNull
    private String errorCode;
    @ApiModelProperty(notes = "alert service code required to be entered by user into REST API ", required = true)
    @QuerySqlField(index = true)
    @NotNull
    private String serviceId;
    @ApiModelProperty(notes = "alert severity required to be entered by user into REST API ", required = true)
    @NotNull
    private String severity;
    @QuerySqlField(index = true)
    private Long timestamp;
    private String alertId;
}
