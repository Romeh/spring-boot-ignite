package com.romeh.ignitemanager.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

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
    @QuerySqlField(index = true)
    String serviceCode;
    @QuerySqlField(index = true)
    String errorCode;
    List<String> emails;
    int maxCount;
    String mailTemplate;

}
