package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.TestDTO;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(target = "questions", source = "questions")
    TestDTO testToTestDTO(Test test);

    @Mapping(target = "questions", ignore = true)  // Sử dụng QuestionMapper ở service
    Test testDTOToTest(TestDTO testDTO);

    default Test testDTOToTestWithInstructor(TestDTO testDTO, Instructor instructor) {
        Test test = testDTOToTest(testDTO);
        test.setInstructor(instructor); // Gán instructor thủ công
        return test;
    }
}
