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

    // Phương thức để chuyển đổi từ string sang enum một cách an toàn
    public static NotificationType fromString(String type) {
        try {
            return NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            // Mặc định trả về SYSTEM nếu không tìm thấy
            return SYSTEM;
        }
    }
}