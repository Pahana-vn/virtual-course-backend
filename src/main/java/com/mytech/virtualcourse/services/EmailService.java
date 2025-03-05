package com.mytech.virtualcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // true enables HTML content

        mailSender.send(message);
    }

    public void sendCourseApprovalNotification(String instructorEmail, String courseName, boolean isApproved, String reason) throws MessagingException {
        String subject = isApproved ?
                "Course Approved: " + courseName :
                "Course Needs Revision: " + courseName;

        String content = isApproved ?
                String.format(
                        "Dear Instructor,<br><br>" +
                                "Your course '%s' has been approved and is now live on the platform.<br><br>" +
                                "Best regards,<br>Admin Team",
                        courseName
                ) :
                String.format(
                        "Dear Instructor,<br><br>" +
                                "Your course '%s' requires some revisions:<br><br>" +
                                "%s<br><br>" +
                                "Please review and update accordingly.<br><br>" +
                                "Best regards,<br>Admin Team",
                        courseName,
                        reason
                );

        sendEmail(instructorEmail, subject, content);
    }
}
