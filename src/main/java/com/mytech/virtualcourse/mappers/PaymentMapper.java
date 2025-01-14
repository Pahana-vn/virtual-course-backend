// src/main/java/com/mytech/virtualcourse/mappers/PaymentMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.PaymentDTO;
import com.mytech.virtualcourse.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "course.id", target = "courseId")
    PaymentDTO toDTO(Payment payment);

    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "courseId", target = "course.id")
    Payment toEntity(PaymentDTO paymentDTO);
}
