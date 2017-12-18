package com.romeh.ignitemanager.services;

import com.romeh.ignitemanager.compute.DataGridCompute;
import com.romeh.ignitemanager.compute.FailFastReducer;
import com.romeh.ignitemanager.compute.MapReduceResponse;
import com.romeh.ignitemanager.compute.ServiceResponse;
import org.apache.ignite.lang.IgniteCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * sample service for how to call map reduce jobs in parallel asynchronous with fail fast reducer
 */
@Service
public class ComputeService {

    private static final Logger logger = LoggerFactory.getLogger(AlertsService.class);
    private final DataGridCompute dataGridCompute;
    @Autowired
    private  FailFastReducer failFastReducer;

    @Autowired
    public ComputeService(DataGridCompute dataGridCompute) {
        this.dataGridCompute = dataGridCompute;
    }

    /**
     * call to ignite compute grid with list if jobs in parallel asynchronous
     */
    public void validateWithAllServicesInParallelAsync(List<IgniteCallable<ServiceResponse>> jobs){
        // execute the jobs with the fail fast reducer in parallel and async the just log the final aggregated response
        dataGridCompute.executeMapReduceFailFast(jobs,failFastReducer,
                mapReduceResponse -> logger.debug(mapReduceResponse.toString()));

    }
    /**
     * call to ignite compute grid with list if jobs in parallel synchronous
     */
    public MapReduceResponse validateWithAllServicesInParallelSync(List<IgniteCallable<ServiceResponse>> jobs){
        // execute the jobs with the fail fast reducer in parallel and sync the just log the final aggregated response
        return dataGridCompute.executeMapReduceFailFastSync(jobs,failFastReducer);
    }
}
