package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.AnswerOptionDTO;
import com.mytech.virtualcourse.entities.AnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerOptionMapper {
    AnswerOptionMapper INSTANCE = Mappers.getMapper(AnswerOptionMapper.class);

    @Mapping(source = "question.id", target = "questionId")
    AnswerOptionDTO answerOptionToAnswerOptionDTO(AnswerOption answerOption);

    @Mapping(source = "questionId", target = "question.id")
    AnswerOption answerOptionDTOToAnswerOption(AnswerOptionDTO answerOptionDTO);
}
