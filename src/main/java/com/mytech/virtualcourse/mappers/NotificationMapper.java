// src/main/java/com/mytech/virtualcourse/mappers/NotificationMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.NotificationDTO;
import com.mytech.virtualcourse.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "payment.id", target = "paymentId")
    NotificationDTO toDTO(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "courseId", target = "course.id")
    @Mapping(source = "paymentId", target = "payment.id")
     Notification toEntity(NotificationDTO notificationDTO);
}
