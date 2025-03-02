package com.mytech.virtualcourse.enums;

/**
 * Enum representing the status of a transaction
 */
public enum StatusTransaction {
    PENDING,    // Đang chờ xử lý
    SUCCESS,    // Thành công
    FAILED,     // Thất bại
    CANCELLED,  // Đã hủy
    REFUNDED,   // Đã hoàn tiền
    COMPLETED,  // Hoàn thành
    REJECTED    // Từ chối
}