package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.entities.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AnswerOptionMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring") // Sử dụng AnswerOptionMapper
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

//    @Mapping(target = "testId", source = "test.id")
//    @Mapping(target = "courseId", source = "course.id")
    QuestionDTO questionToQuestionDTO(Question question);

//    @Mapping(target = "test.id", source = "testId")
//    @Mapping(target = "course.id", source = "courseId")
    Question questionDTOToQuestion(QuestionDTO questionDTO);
}
