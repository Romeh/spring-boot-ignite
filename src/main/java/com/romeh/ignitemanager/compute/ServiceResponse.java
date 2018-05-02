package com.romeh.ignitemanager.compute;


import java.io.Serializable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @param <T> the service call response type
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ServiceResponse<T> implements Serializable {
    private T response;
    private boolean success ;
    private String serviceOrigin;
}
