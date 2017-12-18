package com.romeh.ignitemanager.compute;



import org.apache.ignite.lang.IgniteReducer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a fail fast map reducer to decide if it should keep waiting for other jobs to final reduce or it should terminate
 * and fail fast with the current responses if any failed
 */
@Component
@Scope("prototype")
public class FailFastReducer implements IgniteReducer<ServiceResponse, MapReduceResponse> {

    private final  Map<String, ServiceResponse> responseMap = new ConcurrentHashMap<>();

    /**
     * @param serviceCallResponse the job response
     * @return return a boolean to decide it is time to reduce or not
     */
    @Override
    public boolean collect(ServiceResponse serviceCallResponse) {
        if (serviceCallResponse != null) {
            if (serviceCallResponse.isSuccess()) {
                responseMap.put(serviceCallResponse.getServiceOrigin(), serviceCallResponse);
                return true;
            } else {
                responseMap.put(serviceCallResponse.getServiceOrigin(), serviceCallResponse);
                return false;
            }
        }
        return false;
    }

    /**
     * @return the final generic reduced response containing the list of jobs responses and global status
     */
    @Override
    public MapReduceResponse reduce() {
        return MapReduceResponse.builder().success(checkStatus()).reducedResponses(responseMap).build();
    }

    /**
     * @return the generic reduced response status based into the single status of each single collected jobs response
     */
    public boolean checkStatus() {
        boolean status = true;
        for (Map.Entry<String, ServiceResponse> key : responseMap.entrySet()) {
            status = status && responseMap.get(key.getKey()).isSuccess();
        }
        return status;
    }

}
