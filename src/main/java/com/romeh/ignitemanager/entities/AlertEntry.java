package com.romeh.ignitemanager.entities;

import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by romeh on 19/07/2017.
 */
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AlertEntry implements Serializable {
    private Map<String,String> alertContent;
    @QuerySqlField(index = true)
    private String errorCode;
    @QuerySqlField(index = true)
    private String serviceId;
    private String severity;
    @QuerySqlField(index = true)
    private Long timestamp;
    private String alertId;
}
