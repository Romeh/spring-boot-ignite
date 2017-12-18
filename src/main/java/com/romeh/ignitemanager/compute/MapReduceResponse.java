package com.romeh.ignitemanager.compute;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 *  the generic reduce response that contain all single collected jobs responses
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MapReduceResponse implements Serializable {
    private Map<String, ServiceResponse> reducedResponses;
    boolean success;
}
