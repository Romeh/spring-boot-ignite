package com.romeh.ignitemanager.services;

import com.romeh.ignitemanager.entities.AlertEntry;
import com.romeh.ignitemanager.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;

//import org.springframework.retry.annotation.CircuitBreaker;
//import org.springframework.retry.annotation.Recover;

/**
 * Created by romeh on 17/08/2017.
 */
@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Value("${mail.service.undeliverable}")
    private String undeliverable;

    @Value("${mail.from.bpo.mailbox}")
    private String mailFromBpo;

    @Value("${mail.default.receiver}")
    private String defaultReceiver;

    @Autowired
    private TemplateEngine templateEngine;



    public boolean sendAlert(AlertEntry alertEntry, List<String> emails, String mailTemplate){
        logger.debug("Sending alert  e-mail to '{}'", emails.toString());
        Locale locale = Locale.getDefault();
        Context context = new Context(locale);
        context.setVariable("alert", alertEntry);
        String content = templateEngine.process(mailTemplate, context);
        return sendEmail(mailFromBpo, emails, "New Application ticket has been created", content);
    }

   @CircuitBreaker(maxAttempts = 2, openTimeout = 5000l, resetTimeout = 10000l,exclude = ResourceNotFoundException.class)
    public boolean sendEmail(String from, List<String> to, String subject, String content) {
        logger.debug("Sending to {}",to.toString());
        return true;

    }




    /**
     * The recover method needs to have same return type and parameters.
     *
     * @return
     */
   @Recover
    private boolean fallbackForCall() {
        logger.error("Fallback for mail service call invoked, mail service is NOT reachable");
        return false;
    }
}
