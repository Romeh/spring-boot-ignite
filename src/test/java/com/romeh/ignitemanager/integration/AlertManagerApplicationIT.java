package com.romeh.ignitemanager.integration;

import com.romeh.ignitemanager.AlertManagerApplication;
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

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlertManagerApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("INTEGRATION_TEST")
public class AlertManagerApplicationIT {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate template;
    private URL base;
    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void contextLoads() {
        assertTrue(template.getForEntity(base+"/health",String.class).getStatusCode().is2xxSuccessful());

    }

}
