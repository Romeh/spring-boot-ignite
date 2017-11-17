package com.romeh.ignitemanager.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by romeh on 08/08/2017.
 */
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class AlertConfigEntry implements Serializable {
    @ApiModelProperty(notes = "alert service code required to be entered by user into REST API ", required = true)
    @QuerySqlField(index = true)
    @NotNull
    String serviceCode;
    @ApiModelProperty(notes = "alert error code required to be entered by user into REST API ", required = true)
    @QuerySqlField(index = true)
    @NotNull
    String errorCode;
    List<String> emails;
    int maxCount;
    String mailTemplate;

}
