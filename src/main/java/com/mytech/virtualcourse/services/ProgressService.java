package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.entities.LearningProgress;
import com.mytech.virtualcourse.entities.Lecture;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.entities.StudentLectureProgress;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.LearningProgressRepository;
import com.mytech.virtualcourse.repositories.LectureRepository;
import com.mytech.virtualcourse.repositories.StudentLectureProgressRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private StudentLectureProgressRepository studentLectureProgressRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private CourseRepository courseRepository;

    public void markLectureAsCompleted(Long studentId, Long lectureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));

        // Tìm hoặc tạo StudentLectureProgress
        StudentLectureProgress slp = studentLectureProgressRepository.findByStudentIdAndLectureId(studentId, lectureId);
        if (slp == null) {
            slp = new StudentLectureProgress();
            slp.setStudent(student);
            slp.setLecture(lecture);
        }

        // Đánh dấu completed
        slp.setCompleted(true);
        studentLectureProgressRepository.save(slp);

        // Cập nhật LearningProgress
        updateLearningProgress(studentId, lecture.getSection().getCourse().getId());
    }

    private void updateLearningProgress(Long studentId, Long courseId) {
        // Lấy LearningProgress cho khóa học
        LearningProgress lp = learningProgressRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Progress not found for studentId: "
                        + studentId + " and courseId: " + courseId));

        // Tính lại số bài giảng đã hoàn thành
        int completedLecturesCount = studentLectureProgressRepository.countCompletedLecturesByStudentAndCourse(studentId, courseId);

        // Tính tổng số bài giảng trong khóa học
        int totalLectures = courseRepository.findById(courseId).get()
                .getSections().stream()
                .mapToInt(section -> section.getLectures().size())
                .sum();

        // Tính phần trăm tiến độ
        int progressPercentage = (int)((completedLecturesCount * 100.0) / totalLectures);

        // Cập nhật LearningProgress
        lp.setProgressPercentage(progressPercentage);
        lp.setCompleted(progressPercentage == 100);

        learningProgressRepository.save(lp);
    }
}
