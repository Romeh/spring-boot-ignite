package com.romeh.ignitemanager.entities;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by romeh on 09/08/2017.
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class AlertHolder implements Serializable {
    private String serviceCode;
    private List<AlertEntry> alerts;
}
