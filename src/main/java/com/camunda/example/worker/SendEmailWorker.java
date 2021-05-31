package com.camunda.example.worker;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ExternalTaskSubscription("sendEmail")
public class SendEmailWorker implements ExternalTaskHandler {

    public JavaMailSender mailSender;

    public SendEmailWorker(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void execute(ExternalTask task, ExternalTaskService externalTaskService) {

        String emailContent = task.getVariable("emailContent");
        if (null == emailContent) {
            log.error("Process variable emailContent does not exist");
            externalTaskService.handleBpmnError(task, "001", "Email content must not be empty");
        } else {
            boolean success = false;
            try {
                //send email
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("no-reply@taskworker.com");
                message.setTo("robert.emsbach@camunda.com");
                message.setSubject("Completed task: " + task.getActivityInstanceId());
                message.setText(emailContent);
                mailSender.send(message);
                success = true;

            } catch (MailException mailEx) {
                log.error(mailEx.getMessage(), mailEx);
                // setting retries to 0. Initiate retry in Cockpit when incident is resolved.
                externalTaskService.handleFailure(task, mailEx.getMessage(), mailEx.toString(), 0, 100);
            }

            if (success) {
                // complete the external task
                Map<String, Object> variables = Map.of("result", "done");
                externalTaskService.complete(task, variables);
                log.info("{} task with instance id {} for process instance {} completed.", task.getActivityId(),
                        task.getActivityInstanceId(), task.getProcessInstanceId());
            }
        }
    }
}
