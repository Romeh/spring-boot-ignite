package com.romeh.ignitemanager.integration;

import com.romeh.ignitemanager.AlertManagerApplication;
import com.romeh.ignitemanager.compute.MapReduceResponse;
import com.romeh.ignitemanager.compute.ServiceResponse;
import com.romeh.ignitemanager.services.ComputeService;
import org.apache.ignite.lang.IgniteCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlertManagerApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("INTEGRATION_TEST")
public class AlertManagerApplicationIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate template;
    @Autowired
    ComputeService computeService;
    private URL base;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void contextLoads() {
        assertTrue(template.getForEntity(base+"/health",String.class).getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testMapReducedJobsWithFailFastSync(){
        // example of ignite jobs, first one succeeded , second fail, third succeeded , but the reducer will fail fast once he collect the failed job
        IgniteCallable validationServiceJob1=() -> ServiceResponse.<String>builder().response("Job 1 is valid").serviceOrigin("job1")
                .success(true).build();
        IgniteCallable validationServiceJob2=() -> ServiceResponse.<String>builder().response("Job 2 is failed").serviceOrigin("job2")
                .success(false).build();
        IgniteCallable validationServiceJob3=() -> ServiceResponse.<String>builder().response("Job 3 is valid").serviceOrigin("job3")
                .success(true).build();

        final MapReduceResponse mapReduceResponse = computeService.validateWithAllServicesInParallelSync(
                Arrays.asList(validationServiceJob1,validationServiceJob2,validationServiceJob3)
        );
        boolean status=true;
            for(ServiceResponse serviceResponse: mapReduceResponse.getReducedResponses().values()){

                status=status && serviceResponse.isSuccess();
            }
        // make sure the aggregated status is failed
        assertEquals(status,false);
        assertEquals(mapReduceResponse.isSuccess(),false);

    }


    @Test
    public void testMapReducedJobsWithFailFastSyncFirstAllSuccess(){
        // example of ignite jobs, all succeeded , so the reducer collect all and return successfully
        IgniteCallable validationServiceJob1=() -> ServiceResponse.<String>builder().serviceOrigin("job1")
                .response("Job 1 is valid").success(true).build();
        IgniteCallable validationServiceJob2=() -> ServiceResponse.<String>builder().serviceOrigin("job2")
                .response("Job 2 is valid").success(true).build();
        IgniteCallable validationServiceJob3=() -> ServiceResponse.<String>builder().serviceOrigin("job3")
                .response("Job 3 is valid").success(true).build();
        final MapReduceResponse mapReduceResponse = computeService.validateWithAllServicesInParallelSync(
                Arrays.asList(validationServiceJob1,validationServiceJob2,validationServiceJob3)
        );
        boolean status=true;
           for(ServiceResponse serviceResponse: mapReduceResponse.getReducedResponses().values()){

               status=status && serviceResponse.isSuccess();
           }
        // make sure the aggregated status is success
        assertEquals(status,true);
        assertEquals(mapReduceResponse.isSuccess(),true);

    }

}
