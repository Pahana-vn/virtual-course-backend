package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TestDTO;
import com.mytech.virtualcourse.dtos.QuestionDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Question;
import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.StatusTest;
import com.mytech.virtualcourse.mappers.TestMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.QuestionRepository;
import com.mytech.virtualcourse.repositories.TestRepository;
import com.mytech.virtualcourse.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final QuestionRepository questionRepository;
    private final TestMapper testMapper;
    private final SecurityUtils securityUtils;

    public List<TestDTO> getTestsByCourse(Long courseId) {
        // Kiểm tra xem khóa học có tồn tại không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));

        // Kiểm tra quyền sở hữu khóa học
        Long loggedInInstructorId = getLoggedInInstructorId();
        if (!course.getInstructor().getId().equals(loggedInInstructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view tests for this course.");
        }

        // Trả về danh sách bài kiểm tra
        List<Test> tests = testRepository.findByCourseId(courseId);
        return tests.stream()
                .map(testMapper::testToTestDTO)
                .collect(Collectors.toList());
    }

    public TestDTO createTestForCourse(Long courseId, TestDTO testDTO, Long loggedInInstructorId) {
        // Lấy khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        // Xác thực giảng viên có quyền
        if (!course.getInstructor().getId().equals(loggedInInstructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to manage this course.");
        }

        // Chuyển DTO thành entity và lưu
        Test test = testMapper.testDTOToTest(testDTO);
        test.setCourse(course);
        test.setInstructor(course.getInstructor());

        if (test.getStatusTest() == null) {
            test.setStatusTest(StatusTest.INACTIVE);
        }

        // Lấy danh sách câu hỏi từ database
        if (testDTO.getQuestions() != null && !testDTO.getQuestions().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(
                    testDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
            );
            // Kiểm tra các câu hỏi có thuộc khóa học này không
            boolean allQuestionsBelongToCourse = questions.stream()
                    .allMatch(q -> q.getCourse().getId().equals(courseId));
            if (!allQuestionsBelongToCourse) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
            }
            test.setQuestions(questions); // Liên kết câu hỏi với bài kiểm tra
        } else {
            test.setQuestions(new ArrayList<>()); // Không có câu hỏi được chọn
        }

        testRepository.save(test);

        return testMapper.testToTestDTO(test);
    }

    public TestDTO updateTest(Long id, TestDTO updatedTestDTO, Long loggedInInstructorId) {
        // Lấy bài kiểm tra
        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

        // Kiểm tra quyền sở hữu bài kiểm tra
        if (!existingTest.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this test.");
        }

        // Chỉ cập nhật các trường cần thiết
        existingTest.setTitle(updatedTestDTO.getTitle());
        existingTest.setDescription(updatedTestDTO.getDescription());
        existingTest.setTotalMarks(updatedTestDTO.getTotalMarks());
        existingTest.setPassPercentage(updatedTestDTO.getPassPercentage());
        existingTest.setDuration(updatedTestDTO.getDuration());
        existingTest.setIsFinalTest(updatedTestDTO.getIsFinalTest());
        existingTest.setStatusTest(updatedTestDTO.getStatusTest());

        // Liên kết lại câu hỏi nếu có
        if (updatedTestDTO.getQuestions() != null && !updatedTestDTO.getQuestions().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(
                    updatedTestDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
            );

            // Kiểm tra câu hỏi có thuộc khóa học không
            boolean allQuestionsBelongToCourse = questions.stream()
                    .allMatch(q -> q.getCourse().getId().equals(existingTest.getCourse().getId()));
            if (!allQuestionsBelongToCourse) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
            }

            existingTest.setQuestions(questions);
        } else {
            existingTest.setQuestions(new ArrayList<>()); // Xóa câu hỏi nếu danh sách rỗng
        }

        // Lưu bài kiểm tra đã cập nhật
        Test savedTest = testRepository.save(existingTest);
        return testMapper.testToTestDTO(savedTest);
    }


    public void deleteTest(Long id, Long loggedInInstructorId) {
        // Lấy bài kiểm tra
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

        // Kiểm tra quyền sở hữu bài kiểm tra
        if (!test.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this test.");
        }

        // Xóa bài kiểm tra
        testRepository.delete(test);
    }

    public Long getLoggedInInstructorId() {
        Long accountId = securityUtils.getLoggedInAccountId();
        Instructor instructor = instructorRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found for account ID: " + accountId));
        return instructor.getId();
    }
}
