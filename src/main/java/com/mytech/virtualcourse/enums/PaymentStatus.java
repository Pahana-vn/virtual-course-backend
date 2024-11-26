package com.mytech.virtualcourse.enums;

public enum PaymentStatus {
    Pending,      // Thanh toán đang chờ xử lý
    Completed,    // Thanh toán đã hoàn thành
    Failed,       // Thanh toán thất bại
    Cancelled,    // Thanh toán đã bị hủy
    Refunded      // Thanh toán đã được hoàn tiền
}
