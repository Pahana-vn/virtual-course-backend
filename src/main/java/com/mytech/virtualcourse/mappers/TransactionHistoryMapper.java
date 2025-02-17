package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TransactionHistoryDTO;
import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionHistoryMapper {
    public TransactionHistoryDTO toTransactionHistoryDTO(Payment payment) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate().toLocalDateTime());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setStatus(payment.getStatus().name());

        // Chuyển danh sách khóa học sang DTO
        List<CourseDTO> courseDTOs = payment.getCourses().stream()
                .map(this::toCourseDTO)
                .collect(Collectors.toList());
        dto.setCourses(courseDTOs);

        return dto;
    }

    private CourseDTO toCourseDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitleCourse(course.getTitleCourse());
        dto.setBasePrice(course.getBasePrice());
        dto.setImageCover("http://localhost:8080/uploads/course/" + course.getImageCover());
        return dto;
    }
}
