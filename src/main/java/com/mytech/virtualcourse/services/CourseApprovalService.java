package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.ECourseStatus;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.CourseApprovalHistoryRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseApprovalService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseApprovalHistoryRepository approvalHistoryRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    /**
     * Approves a course with the given ID
     */
    @Transactional
    public void approveCourse(Long courseId, Account reviewer, String notes) {
        System.out.println("Attempting to approve course with ID: " + courseId);

        boolean exists = courseRepository.existsById(courseId);
        System.out.println("Course exists in database: " + exists);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    String errorMsg = "Course with ID " + courseId + " not found";
                    System.err.println(errorMsg);
                    return new ResourceNotFoundException(errorMsg);
                });

        System.out.println("Found course: ID=" + course.getId() + ", Title=" + course.getTitleCourse() + ", Status=" + course.getStatus());

        String instructorEmail = course.getInstructor().getAccount().getEmail();

        course.setStatus(ECourseStatus.PUBLISHED);
        courseRepository.save(course);
        System.out.println("Course status updated to APPROVED");

        CourseApprovalHistory history = CourseApprovalHistory.builder()
                .course(course)
                .reviewer(reviewer)
                .status(ECourseStatus.PUBLISHED)
                .notes(notes)
                .build();
        approvalHistoryRepository.save(history);
        System.out.println("Approval history record created");

        try {
            sendApprovalNotification(course);
        } catch (Exception e) {
            System.err.println("Error in notification process: " + e.getMessage());
        }

        try {
            sendApprovalEmail(course, instructorEmail);
        } catch (Exception e) {
            System.err.println("Error in email process: " + e.getMessage());
        }

        System.out.println("Course approval process completed successfully");
    }

    /**
     * Sends a notification about course approval in a separate transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendApprovalNotification(Course course) {
        try {
            // Sử dụng NotificationType.CourseApproved (PascalCase) thay vì SYSTEM
            notificationService.sendNotification(
                    course.getInstructor().getAccount().getId(),
                    "Your course '" + course.getTitleCourse() + "' has been approved",
                    NotificationType.CrsApprv, // Sử dụng giá trị enum phù hợp
                    course.getId(),
                    null
            );
            System.out.println("Notification sent to instructor");
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());

            // Nếu CourseApproved gây lỗi, thử với General
            try {
                notificationService.sendNotification(
                        course.getInstructor().getAccount().getId(),
                        "Your course '" + course.getTitleCourse() + "' has been approved",
                        NotificationType.General, // Thử với giá trị khác
                        course.getId(),
                        null
                );
                System.out.println("Notification sent using fallback type");
            } catch (Exception e2) {
                System.err.println("Error sending notification with fallback type: " + e2.getMessage());

                // Nếu cả hai đều thất bại, thử với SYSTEM (UPPERCASE)
                try {
                    notificationService.sendNotification(
                            course.getInstructor().getAccount().getId(),
                            "Your course '" + course.getTitleCourse() + "' has been approved",
                            NotificationType.SYSTEM, // Thử với giá trị cũ
                            course.getId(),
                            null
                    );
                    System.out.println("Notification sent using legacy type");
                } catch (Exception e3) {
                    System.err.println("All notification attempts failed: " + e3.getMessage());
                    e3.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends an email about course approval in a separate transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendApprovalEmail(Course course, String instructorEmail) {
        try {
            emailService.sendCourseApprovalNotification(
                    instructorEmail,
                    course.getTitleCourse(),
                    true,
                    null
            );
            System.out.println("Email sent to instructor: " + instructorEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rejects a course with the given ID
     */
    @Transactional
    public void rejectCourse(Long courseId, Account reviewer, String rejectionReason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        String instructorEmail = course.getInstructor().getAccount().getEmail();

        course.setStatus(ECourseStatus.REJECTED);
        courseRepository.save(course);

        CourseApprovalHistory history = CourseApprovalHistory.builder()
                .course(course)
                .reviewer(reviewer)
                .status(ECourseStatus.REJECTED)
                .rejectionReason(rejectionReason)
                .build();
        approvalHistoryRepository.save(history);

        try {
            sendRejectionNotification(course, rejectionReason);
        } catch (Exception e) {
            System.err.println("Error in rejection notification process: " + e.getMessage());
        }

        try {
            sendRejectionEmail(course, instructorEmail, rejectionReason);
        } catch (Exception e) {
            System.err.println("Error in rejection email process: " + e.getMessage());
        }
    }

    /**
     * Sends a notification about course rejection in a separate transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendRejectionNotification(Course course, String rejectionReason) {
        try {
            // Sử dụng NotificationType.CourseRejected (PascalCase)
            notificationService.sendNotification(
                    course.getInstructor().getAccount().getId(),
                    "Your course '" + course.getTitleCourse() + "' needs revision. Reason: " + rejectionReason,
                    NotificationType.CrsRejct, // Sử dụng giá trị enum phù hợp
                    course.getId(),
                    null
            );
            System.out.println("Rejection notification sent to instructor");
        } catch (Exception e) {
            System.err.println("Error sending rejection notification: " + e.getMessage());

            // Nếu CourseRejected gây lỗi, thử với General
            try {
                notificationService.sendNotification(
                        course.getInstructor().getAccount().getId(),
                        "Your course '" + course.getTitleCourse() + "' needs revision. Reason: " + rejectionReason,
                        NotificationType.General, // Thử với giá trị khác
                        course.getId(),
                        null
                );
                System.out.println("Rejection notification sent using fallback type");
            } catch (Exception e2) {
                System.err.println("Error sending rejection notification with fallback type: " + e2.getMessage());

                // Nếu cả hai đều thất bại, thử với SYSTEM (UPPERCASE)
                try {
                    notificationService.sendNotification(
                            course.getInstructor().getAccount().getId(),
                            "Your course '" + course.getTitleCourse() + "' needs revision. Reason: " + rejectionReason,
                            NotificationType.SYSTEM, // Thử với giá trị cũ
                            course.getId(),
                            null
                    );
                    System.out.println("Rejection notification sent using legacy type");
                } catch (Exception e3) {
                    System.err.println("All rejection notification attempts failed: " + e3.getMessage());
                    e3.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends an email about course rejection in a separate transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendRejectionEmail(Course course, String instructorEmail, String rejectionReason) {
        try {
            emailService.sendCourseApprovalNotification(
                    instructorEmail,
                    course.getTitleCourse(),
                    false,
                    rejectionReason
            );
            System.out.println("Rejection email sent to instructor: " + instructorEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending rejection email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the approval history for a course
     */
    public List<CourseApprovalHistory> getCourseApprovalHistory(Long courseId) {
        return approvalHistoryRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }
}