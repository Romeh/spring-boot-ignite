package com.romeh.ignitemanager.compute;


import lombok.*;

import java.io.Serializable;

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
