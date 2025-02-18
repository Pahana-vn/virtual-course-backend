package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.entities.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {AnswerOptionMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "courseId", source = "course.id")
    QuestionDTO questionToQuestionDTO(Question question);

    @Mapping(target = "course.id", source = "courseId")
    Question questionDTOToQuestion(QuestionDTO questionDTO);

    List<QuestionDTO> toDTOList(List<Question> questions);
}
