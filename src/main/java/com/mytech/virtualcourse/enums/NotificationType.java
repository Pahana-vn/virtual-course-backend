package com.mytech.virtualcourse.enums;

/**
 * Enum representing different types of notifications in the system.
 * Used to categorize notifications for filtering and display purposes.
 */
public enum NotificationType {
    // Các giá trị mới sử dụng PascalCase
    Payment,
    Enrollment,
    CourseUpdate,
    Assignment,
    TestReminder,
    General,

    // Các giá trị rút gọn
    CrsApprv,    // Course Approved
    CrsRejct,    // Course Rejected
    CrsSubmt,    // Course Submitted
    CrsRevsn,    // Course Revision
    SysAlert,    // System Alert
    AccStatus,   // Account Status
    InstApprv,   // Instructor Approved
    InstRejct,   // Instructor Rejected
    WalletCredit,
    WalletDebit,
    WalletWithdrawal,

    // Các giá trị cũ (sẽ dần loại bỏ)
    COURSE,
    SYSTEM;

    /**
     * Phương thức để chuyển đổi từ string sang enum một cách an toàn
     * Hỗ trợ cả các giá trị cũ và mới
     *
     * @param type String representation of notification type
     * @return NotificationType enum value
     */
    public static NotificationType fromString(String type) {
        if (type == null) {
            return SYSTEM; // Default value
        }

        try {
            // Try direct conversion first
            return NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            // Handle legacy mappings
            switch (type.toUpperCase()) {
                case "PAYMENT":
                case "PAYMENT_PROCESSED":
                    return Payment;
                case "ENROLLMENT":
                    return Enrollment;
                case "COURSE_UPDATE":
                    return CourseUpdate;
                case "ASSIGNMENT":
                    return Assignment;
                case "TEST_REMINDER":
                    return TestReminder;
                case "COURSE_APPROVAL":
                    return CrsApprv;
                case "COURSE_REJECTION":
                    return CrsRejct;
                case "COURSE_SUBMISSION":
                    return CrsSubmt;
                case "COURSE_REVISION":
                    return CrsRevsn;
                case "SYSTEM_ALERT":
                    return SysAlert;
                case "ACCOUNT_STATUS":
                    return AccStatus;
                case "INSTRUCTOR_APPROVAL":
                    return InstApprv;
                case "INSTRUCTOR_REJECTION":
                    return InstRejct;
                case "WALLET_CREDIT":
                    return WalletCredit;
                case "WALLET_DEBIT":
                    return WalletDebit;
                case "WALLET_WITHDRAWAL":
                    return WalletWithdrawal;
                default:
                    return SYSTEM; // Default fallback
            }
        }
    }
}