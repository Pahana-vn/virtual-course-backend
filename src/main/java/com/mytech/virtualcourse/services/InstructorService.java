package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.*;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.CourseMapper;
import com.mytech.virtualcourse.mappers.InstructorMapper;
import com.mytech.virtualcourse.repositories.*;
import com.mytech.virtualcourse.mappers.ReviewMapper;
import com.mytech.virtualcourse.mappers.TestMapper;
import com.mytech.virtualcourse.repositories.*;
import com.mytech.virtualcourse.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private InstructorMapper instructorMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PaymentRepository  paymentRepository;

    public List<InstructorDTO> getAllInstructors(String platform) {
        List<Instructor> instructors = instructorRepository.findAll();

        // Nếu có tham số platform=flutter, dùng 10.0.2.2 (Android Emulator)
        String baseUrl = (platform != null && platform.equals("flutter"))
                ? "http://10.0.2.2:8080"
                : "http://localhost:8080";

        return instructors.stream()
                .map(instructor -> {
                    InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
                    int totalCourses = instructorRepository.countPublishedCoursesByInstructorId(instructor.getId());
                    dto.setTotalCourses(totalCourses);
                    // Cập nhật đường dẫn ảnh
                    if (instructor.getPhoto() != null) {
                        dto.setPhoto(baseUrl + "/uploads/instructor/" + instructor.getPhoto());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public InstructorDTO getInstructorById(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        InstructorDTO dto = instructorMapper.instructorToInstructorDTO(instructor);
        // Cập nhật đường dẫn ảnh
        if (instructor.getPhoto() != null) {
            dto.setPhoto("http://localhost:8080/uploads/instructor/" + instructor.getPhoto());
        }
        return dto;
    }

    public InstructorDTO createInstructor(InstructorDTO instructorDTO) {
        Instructor instructor = instructorMapper.instructorDTOToInstructor(instructorDTO);

        if (instructorDTO.getGender() != null) {
            instructor.setGender(Gender.valueOf(instructorDTO.getGender().toUpperCase()));
        }
        Instructor savedInstructor = instructorRepository.save(instructor);
        return instructorMapper.instructorToInstructorDTO(savedInstructor);
    }


    public InstructorDTO updateInstructor(Long id, InstructorDTO instructorDTO) {
        Instructor existingInstructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));

        existingInstructor.setFirstName(instructorDTO.getFirstName());
        existingInstructor.setLastName(instructorDTO.getLastName());
        existingInstructor.setAddress(instructorDTO.getAddress());
        existingInstructor.setPhone(instructorDTO.getPhone());
        existingInstructor.setBio(instructorDTO.getBio());
        existingInstructor.setTitle(instructorDTO.getTitle());
        existingInstructor.setPhoto(instructorDTO.getPhoto());
        existingInstructor.setWorkplace(instructorDTO.getWorkplace());
        existingInstructor.setVerifiedPhone(instructorDTO.getVerifiedPhone());


        if (instructorDTO.getGender() != null) {
            existingInstructor.setGender(Gender.valueOf(instructorDTO.getGender().toUpperCase()));
        }

        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return instructorMapper.instructorToInstructorDTO(updatedInstructor);
    }

    public void deleteInstructor(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + id);
        }
        instructorRepository.deleteById(id);
    }

    public InstructorDetailsDTO getInstructorDetails(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        int totalCourses = courseRepository.countByInstructorId(instructorId);
        int totalSections = sectionRepository.countByInstructorId(instructorId);
        int totalStudents = studentRepository.countTotalCourseStudentsByInstructor(instructorId);
        double averageRating = instructorRepository.calculateAverageRatingByInstructorId(instructorId);

        return InstructorMapper.MAPPER.instructorToInstructorDetailsDTO(
                instructor, totalCourses, totalSections, totalStudents, averageRating
        );
    }

    public InstructorProfileDTO getProfileByInstructorId(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));

        return instructorMapper.instructorToInstructorProfileDTO(instructor);
    }

    public InstructorProfileDTO updateProfileByInstructorId(Long id, InstructorProfileDTO profileDTO) {

        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

        instructor.setFirstName(profileDTO.getFirstName());
        instructor.setLastName(profileDTO.getLastName());
        instructor.setGender(Gender.valueOf(profileDTO.getGender().toUpperCase()));
        instructor.setAddress(profileDTO.getAddress());
        instructor.setPhone(profileDTO.getPhone());
        instructor.setBio(profileDTO.getBio());
        instructor.setTitle(profileDTO.getTitle());
        instructor.setWorkplace(profileDTO.getWorkplace());

        if (profileDTO.getPhoto() != null) {
            instructor.setPhoto(profileDTO.getPhoto());
        }

        instructor = instructorRepository.save(instructor);

        // Map the updated instructor entity to the InstructorProfileDTO
        return InstructorMapper.MAPPER.instructorToInstructorProfileDTO(instructor);
    }

    public InstructorStatisticsDTO getInstructorStatistics(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        int totalCourses = instructorRepository.countCoursesByInstructorId(id);
        int totalPublishedCourses = instructorRepository.countPublishedCoursesByInstructorId(id);
        int totalPendingCourses = instructorRepository.countPendingCoursesByInstructorId(id);
        int totalStudents = studentRepository.countTotalCourseStudentsByInstructor(id);
        int totalPurchasedCourses = studentRepository.countTotalCourseStudentsByInstructor(id);
        int totalTransactions = transactionRepository.countTransactionsByInstructorId(id);
        int totalDeposits = transactionRepository.countDepositsInTransactionsByInstructorId(id);
        int totalWithdrawals = transactionRepository.countWithdrawalsInTransactionsByInstructorId(id);

        BigDecimal balance = instructor.getWallet() != null
                ? instructor.getWallet().getBalance()
                : BigDecimal.ZERO;

        return instructorMapper.toInstructorStatisticsDTO(instructor, totalCourses, totalPublishedCourses, totalPendingCourses, totalStudents,totalPurchasedCourses,totalTransactions,totalDeposits,totalWithdrawals, balance);
    }

    public InstructorProfileDTO getProfileByLoggedInInstructor(HttpServletRequest request) {
        Long instructorId = getInstructorIdFromRequest(request);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with Instructor ID: " + instructorId));

        return instructorMapper.instructorToInstructorProfileDTO(instructor);
    }


    public InstructorProfileDTO updateProfileByLoggedInInstructor(HttpServletRequest request, InstructorProfileDTO profileDTO) {
        Long instructorId = getInstructorIdFromRequest(request);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with instructor ID: " + instructorId));

        instructor.setFirstName(profileDTO.getFirstName());
        instructor.setLastName(profileDTO.getLastName());
        instructor.setGender(profileDTO.getGender() != null
                ? Gender.valueOf(profileDTO.getGender().toUpperCase())
                : instructor.getGender());
        instructor.setAddress(profileDTO.getAddress());
        instructor.setPhone(profileDTO.getPhone());
        instructor.setBio(profileDTO.getBio());
        instructor.setTitle(profileDTO.getTitle());
        instructor.setWorkplace(profileDTO.getWorkplace());
        instructor.setPhoto(profileDTO.getPhoto());
        instructor.setVerifiedPhone(profileDTO.getVerifiedPhone());

        Instructor updatedInstructor = instructorRepository.save(instructor);

        return instructorMapper.instructorToInstructorProfileDTO(updatedInstructor);
    }

    private Long getInstructorIdFromRequest(HttpServletRequest request) {
        String jwt = getJwtFromCookies(request);
        return jwtUtil.getInstructorIdFromJwtToken(jwt);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        return jwtUtil.getCookieValueByName(request, "token");
    }

    /**
     * Upload instructor document without using Document entity
     */
    public DocumentDTO uploadInstructorDocument(Long instructorId, MultipartFile file, String documentType) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        try {
            // Save file to storage
            String fileName = fileStorageService.storeFile(file, "instructor_documents");
            String fileUrl = fileStorageService.getFileUrl("instructor_documents", fileName);

            // Create and return DocumentDTO
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setInstructorId(instructorId);
            documentDTO.setFileName(fileName);
            documentDTO.setOriginalFileName(file.getOriginalFilename());
            documentDTO.setFileType(file.getContentType());
            documentDTO.setFileSize(file.getSize());
            documentDTO.setDocumentType(documentType);
            documentDTO.setUploadDate(LocalDateTime.now());
            documentDTO.setDownloadUrl(fileUrl);

            return documentDTO;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Get instructor documents without using Document entity
     */
    public List<DocumentDTO> getInstructorDocuments(Long instructorId) {
        // Validate instructor exists
        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        // Vì không có entity Document, chúng ta sẽ trả về một danh sách rỗng
        return new ArrayList<>();
    }

    /**
     * Get detailed statistics for an instructor
     */
    public Map<String, Object> getDetailedStatistics(Long instructorId, String timeRange) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Map<String, Object> statistics = new HashMap<>();

        // Basic statistics
        statistics.put("totalCourses", courseRepository.countByInstructorId(instructorId));
        statistics.put("totalPublishedCourses", courseRepository.countByInstructorIdAndStatus(instructorId, "PUBLISHED"));
        statistics.put("totalPendingCourses", courseRepository.countByInstructorIdAndStatus(instructorId, "PENDING"));
        statistics.put("totalStudents", paymentRepository.countDistinctStudentsByInstructorId(instructorId));

        // Try to get average rating, handle null case
        Double avgRating = reviewRepository.getAverageRatingForInstructor(instructorId);
        statistics.put("averageRating", avgRating != null ? avgRating : 0.0);

        // Revenue statistics
        BigDecimal totalRevenue = paymentRepository.getTotalRevenueForInstructor(instructorId);
        statistics.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Time-based statistics
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (timeRange.toLowerCase()) {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusMonths(1); // Default to month
        }

        // New enrollments in time period
        statistics.put("newEnrollments", paymentRepository.countEnrollmentsByInstructorIdAndDateRange(
                instructorId, startDate, endDate));

        // New revenue in time period
        BigDecimal periodRevenue = paymentRepository.getRevenueForInstructorInPeriod(
                instructorId, startDate, endDate);
        statistics.put("periodRevenue", periodRevenue != null ? periodRevenue : BigDecimal.ZERO);

        // New reviews in time period
        statistics.put("newReviews", reviewRepository.countReviewsByInstructorIdAndDateRange(
                instructorId, startDate, endDate));

        // Monthly revenue trend (last 6 months)
        List<Map<String, Object>> revenueTrend = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();

            BigDecimal monthlyRevenue = paymentRepository.getRevenueForInstructorInPeriod(
                    instructorId, monthStart, monthEnd);

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month.getMonth().toString());
            monthData.put("revenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);
            revenueTrend.add(monthData);
        }

        statistics.put("revenueTrend", revenueTrend);

        return statistics;
    }


    public Map<String, Object> getPerformanceMetrics(Long instructorId) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Map<String, Object> metrics = new HashMap<>();

        // Course completion rate
        Long totalEnrollments = paymentRepository.countEnrollmentsByInstructorId(instructorId);
        Long completedEnrollments = paymentRepository.countCompletedEnrollmentsByInstructorId(instructorId);

        double completionRate = totalEnrollments > 0 ? (double) completedEnrollments / totalEnrollments * 100 : 0;
        metrics.put("courseCompletionRate", Math.round(completionRate * 100.0) / 100.0);

        // Average student rating
        Double avgRating = reviewRepository.getAverageRatingForInstructor(instructorId);
        metrics.put("averageRating", avgRating != null ? avgRating : 0);

        // Rating distribution
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = reviewRepository.countReviewsByInstructorIdAndRating(instructorId, i);
            ratingDistribution.put(i, count != null ? count : 0L);
        }
        metrics.put("ratingDistribution", ratingDistribution);

        // Student engagement metrics
        metrics.put("totalStudents", paymentRepository.countDistinctStudentsByInstructorId(instructorId));
        metrics.put("activeStudents", paymentRepository.countActiveStudentsByInstructorId(instructorId));

        // Course metrics
        metrics.put("totalCourses", courseRepository.countByInstructorId(instructorId));

        Double avgDuration = courseRepository.getAverageCourseDurationByInstructorId(instructorId);
        metrics.put("averageCourseDuration", avgDuration != null ? avgDuration : 0);

        // Revenue metrics
        BigDecimal totalRevenue = paymentRepository.getTotalRevenueForInstructor(instructorId);
        metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Average revenue per course
        long courseCount = (long) courseRepository.countByInstructorId(instructorId);
        BigDecimal avgRevenuePerCourse = BigDecimal.ZERO;
        if (courseCount > 0 && totalRevenue != null) {
            try {
                avgRevenuePerCourse = totalRevenue.divide(new BigDecimal(courseCount), 2, BigDecimal.ROUND_HALF_UP);
            } catch (ArithmeticException e) {
                // Handle division by zero or other arithmetic errors
                avgRevenuePerCourse = BigDecimal.ZERO;
            }
        }
        metrics.put("averageRevenuePerCourse", avgRevenuePerCourse);

        // Average revenue per student
        long studentCount = (long) paymentRepository.countDistinctStudentsByInstructorId(instructorId);
        BigDecimal avgRevenuePerStudent = BigDecimal.ZERO;
        if (studentCount > 0 && totalRevenue != null) {
            try {
                avgRevenuePerStudent = totalRevenue.divide(new BigDecimal(studentCount), 2, BigDecimal.ROUND_HALF_UP);
            } catch (ArithmeticException e) {
                // Handle division by zero or other arithmetic errors
                avgRevenuePerStudent = BigDecimal.ZERO;
            }
        }
        metrics.put("averageRevenuePerStudent", avgRevenuePerStudent);

        return metrics;
    }

    /**
     * Get instructor courses with pagination and filtering
     */
    public Page<CourseDTO> getInstructorCourses(Long instructorId, String status, int page, int size) {
        // Validate instructor exists
        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> coursesPage;

        if (status != null && !status.isEmpty()) {
            coursesPage = courseRepository.findByInstructorIdAndStatus(instructorId, ECourseStatus.valueOf(status), pageable);
        } else {
            coursesPage = courseRepository.findByInstructorId(instructorId, pageable);
        }

        return coursesPage.map(courseMapper::courseToCourseDTO);
    }

    /**
     * Get instructor tests with pagination and filtering
     */
    public Page<TestDTO> getInstructorTests(Long instructorId, String status, int page, int size) {
        // Validate instructor exists
        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Test> testsPage;

        if (status != null && !status.isEmpty()) {
            try {
                StatusTest statusEnum = StatusTest.valueOf(status.toUpperCase());
                testsPage = testRepository.findByInstructorIdAndStatus(instructorId, statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                // Handle invalid status value
                testsPage = testRepository.findByInstructorId(instructorId, pageable);
            }
        } else {
            testsPage = testRepository.findByInstructorId(instructorId, pageable);
        }

        return testsPage.map(test -> testMapper.testToTestDTO(test));
    }
    /**
     * Get instructor reviews with pagination and filtering
     */
    public Page<ReviewDTO> getInstructorReviews(Long instructorId, Integer rating, int page, int size) {
        // Validate instructor exists
        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewsPage;

        if (rating != null) {
            reviewsPage = reviewRepository.findByInstructorIdAndRating(instructorId, rating, pageable);
        } else {
            reviewsPage = reviewRepository.findByInstructorId(instructorId, pageable);
        }

        return reviewsPage.map(reviewMapper::toDTO);
    }

    /**
     * Hide or delete inappropriate reviews (admin function)
     */
    public void hideInappropriateReview(Long reviewId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Xóa review khỏi database
        reviewRepository.delete(review);

        // Thông báo cho người dùng
        try {
            notificationService.sendNotification(
                    review.getStudent().getId(),
                    "Your review has been removed due to inappropriate content. Reason: " + reason,
                    NotificationType.SYSTEM,
                    review.getCourse().getId(),
                    null
            );
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    /**
     * Update instructor profile
     */
    public InstructorProfileDTO updateInstructorProfile(Long instructorId, InstructorProfileDTO profileDTO) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));

        instructor.setFirstName(profileDTO.getFirstName());
        instructor.setLastName(profileDTO.getLastName());
        instructor.setGender(profileDTO.getGender() != null
                ? Gender.valueOf(profileDTO.getGender().toUpperCase())
                : instructor.getGender());
        instructor.setAddress(profileDTO.getAddress());
        instructor.setPhone(profileDTO.getPhone());
        instructor.setBio(profileDTO.getBio());
        instructor.setTitle(profileDTO.getTitle());
        instructor.setWorkplace(profileDTO.getWorkplace());
        instructor.setPhoto(profileDTO.getPhoto());
        instructor.setVerifiedPhone(profileDTO.getVerifiedPhone());

        Instructor updatedInstructor = instructorRepository.save(instructor);

        return instructorMapper.instructorToInstructorProfileDTO(updatedInstructor);
    }

    /**
     * Get instructor dashboard data
     */
    public Map<String, Object> getInstructorDashboard(Long instructorId) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Map<String, Object> dashboardData = new HashMap<>();

        // Basic statistics
        dashboardData.put("totalCourses", courseRepository.countByInstructorId(instructorId));
        dashboardData.put("totalStudents", paymentRepository.countDistinctStudentsByInstructorId(instructorId));

        // Revenue statistics
        BigDecimal totalRevenue = paymentRepository.getTotalRevenueForInstructor(instructorId);
        dashboardData.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Recent activity
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // New enrollments in last 30 days
        dashboardData.put("recentEnrollments", paymentRepository.countEnrollmentsByInstructorIdAndDateRange(
                instructorId, thirtyDaysAgo, LocalDate.now()));

        // New revenue in last 30 days
        BigDecimal recentRevenue = paymentRepository.getRevenueForInstructorInPeriod(
                instructorId, thirtyDaysAgo, LocalDate.now());
        dashboardData.put("recentRevenue", recentRevenue != null ? recentRevenue : BigDecimal.ZERO);

        // Recent reviews
        dashboardData.put("recentReviews", reviewRepository.countReviewsByInstructorIdAndDateRange(
                instructorId, thirtyDaysAgo, LocalDate.now()));

        // Average rating
        Double avgRating = reviewRepository.getAverageRatingForInstructor(instructorId);
        dashboardData.put("averageRating", avgRating != null ? avgRating : 0.0);

        // Recent courses
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Course> recentCourses = courseRepository.findByInstructorId(instructorId, pageable);
        dashboardData.put("recentCourses", recentCourses.map(courseMapper::courseToCourseDTO).getContent());

        return dashboardData;
    }

    /**
     * Get instructor earnings by period
     */
    public Map<String, Object> getInstructorEarnings(Long instructorId, String period) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        Map<String, Object> earningsData = new HashMap<>();

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (period.toLowerCase()) {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            case "all":
                startDate = LocalDate.of(2000, 1, 1); // A date far in the past
                break;
            default:
                startDate = endDate.minusMonths(1); // Default to month
        }

        // Total earnings for the period
        BigDecimal periodRevenue = paymentRepository.getRevenueForInstructorInPeriod(
                instructorId, startDate, endDate);
        earningsData.put("totalEarnings", periodRevenue != null ? periodRevenue : BigDecimal.ZERO);

        // Number of sales
        Long salesCount = paymentRepository.countEnrollmentsByInstructorIdAndDateRange(
                instructorId, startDate, endDate);
        earningsData.put("salesCount", salesCount);

        // Average earnings per sale
        BigDecimal avgEarningsPerSale = BigDecimal.ZERO;
        if (salesCount > 0 && periodRevenue != null) {
            try {
                avgEarningsPerSale = periodRevenue.divide(new BigDecimal(salesCount), 2, BigDecimal.ROUND_HALF_UP);
            } catch (ArithmeticException e) {
                // Handle division by zero or other arithmetic errors
                avgEarningsPerSale = BigDecimal.ZERO;
            }
        }
        earningsData.put("averageEarningsPerSale", avgEarningsPerSale);

        // Earnings by course - Sử dụng cách tiếp cận khác vì không có phương thức getRevenueForCourseInPeriod
        List<Map<String, Object>> earningsByCourse = new ArrayList<>();
        List<Course> courses = (List<Course>) courseRepository.findByInstructorId(instructorId, Pageable.unpaged());

        for (Course course : courses) {
            // Tính toán doanh thu cho từng khóa học bằng cách truy vấn trực tiếp
            // Đây là một giải pháp tạm thời, trong thực tế bạn nên thêm phương thức vào repository
            BigDecimal courseRevenue = calculateCourseRevenue(course.getId(), startDate, endDate);

            if (courseRevenue != null && courseRevenue.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("courseId", course.getId());
                courseData.put("courseTitle", course.getTitleCourse());
                courseData.put("revenue", courseRevenue);
                courseData.put("salesCount", countCourseEnrollments(course.getId(), startDate, endDate));

                earningsByCourse.add(courseData);
            }
        }

        earningsData.put("earningsByCourse", earningsByCourse);

        return earningsData;
    }

    /**
     * Calculate revenue for a specific course in a period
     * This is a workaround since we don't have the repository method
     */
    private BigDecimal calculateCourseRevenue(Long courseId, LocalDate startDate, LocalDate endDate) {
        // Trong thực tế, bạn nên thêm phương thức này vào PaymentRepository
        // Đây chỉ là một giải pháp tạm thời
        List<Payment> allPayments = paymentRepository.findAll();

        return allPayments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.Completed)
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate().toLocalDateTime().toLocalDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .filter(payment -> payment.getCourses().stream()
                        .anyMatch(course -> course.getId().equals(courseId)))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Count enrollments for a specific course in a period
     * This is a workaround since we don't have the repository method
     */
    private Long countCourseEnrollments(Long courseId, LocalDate startDate, LocalDate endDate) {
        // Trong thực tế, bạn nên thêm phương thức này vào PaymentRepository
        // Đây chỉ là một giải pháp tạm thời
        List<Payment> allPayments = paymentRepository.findAll();

        return allPayments.stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate().toLocalDateTime().toLocalDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .filter(payment -> payment.getCourses().stream()
                        .anyMatch(course -> course.getId().equals(courseId)))
                .count();
    }

    /**
     * Get instructor notifications
     */
    public Page<NotificationDTO> getInstructorNotifications(Long instructorId, int page, int size) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        // Get account ID associated with the instructor
        Long accountId = instructor.getAccount().getId();

        // Get notifications for the account
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

        // Thay dòng này:
        // return notificationService.getNotificationsByUserPaginated(accountId, pageable);

        // Bằng dòng này (giả sử phương thức này nhận page và size thay vì Pageable):
        return notificationService.getNotificationsByUserPaginated(accountId, page, size);
    }

    /**
     * Mark notification as read
     */
    public void markNotificationAsRead(Long instructorId, Long notificationId) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        // Get account ID associated with the instructor
        Long accountId = instructor.getAccount().getId();

        // Mark notification as read
        notificationService.markAsRead(notificationId);
    }

    /**
     * Mark all notifications as read
     */
    public void markAllNotificationsAsRead(Long instructorId) {
        // Validate instructor exists
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        // Get account ID associated with the instructor
        Long accountId = instructor.getAccount().getId();

        // Mark all notifications as read
        notificationService.markAllAsReadForUser(accountId);
    }
}
