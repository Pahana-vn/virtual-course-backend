// src/main/java/com/mytech/virtualcourse/services/StatisticsService.java

package com.mytech.virtualcourse.services;
import com.mytech.virtualcourse.dtos.StatisticsDTO;
import com.mytech.virtualcourse.dtos.TrendsDTO;
import com.mytech.virtualcourse.mappers.StatisticsMapper;
import com.mytech.virtualcourse.mappers.TrendsMapper;
import com.mytech.virtualcourse.entities.Statistics;
import com.mytech.virtualcourse.entities.Trends;
import com.mytech.virtualcourse.repositories.AdminAccountRepository;
import com.mytech.virtualcourse.repositories.CategoryRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private AdminAccountRepository accountRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private TrendsMapper trendsMapper;

    /**
     * Chuyển đổi LocalDateTime sang Date
     *
     * @param localDateTime Thời gian LocalDateTime cần chuyển đổi
     * @return Đối tượng Date tương ứng
     */
    private Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Lấy thống kê dựa trên bộ lọc.
     *
     * @param filter Bộ lọc: "today", "week", "month", "year", "all"
     * @return StatisticsDTO chứa số liệu thống kê.
     */
    public StatisticsDTO getStatistics(String filter) {
        StatisticsDTO stats = new StatisticsDTO();

        switch (filter.toLowerCase()) {
            case "today":
                stats.setAccounts(countAccountsToday());
                stats.setInstructors(countInstructorsToday());
                stats.setStudents(countStudentsToday());
                stats.setCourses(countCoursesToday());
                stats.setCategories(countCategoriesToday());
                stats.setRoles(countRolesToday());
                break;
            case "week":
                stats.setAccounts(countAccountsThisWeek());
                stats.setInstructors(countInstructorsThisWeek());
                stats.setStudents(countStudentsThisWeek());
                stats.setCourses(countCoursesThisWeek());
                stats.setCategories(countCategoriesThisWeek());
                stats.setRoles(countRolesThisWeek());
                break;
            case "month":
                stats.setAccounts(countAccountsThisMonth());
                stats.setInstructors(countInstructorsThisMonth());
                stats.setStudents(countStudentsThisMonth());
                stats.setCourses(countCoursesThisMonth());
                stats.setCategories(countCategoriesThisMonth());
                stats.setRoles(countRolesThisMonth());
                break;
            case "year":
                stats.setAccounts(countAccountsThisYear());
                stats.setInstructors(countInstructorsThisYear());
                stats.setStudents(countStudentsThisYear());
                stats.setCourses(countCoursesThisYear());
                stats.setCategories(countCategoriesThisYear());
//                stats.setRoles(countRolesThisYear());
                break;
            default:
                // Default to all-time counts
                stats.setAccounts(accountRepository.count());
                stats.setInstructors(instructorRepository.count());
                stats.setStudents(studentRepository.count());
                stats.setCourses(courseRepository.count());
                stats.setCategories(categoryRepository.count());
                stats.setRoles(roleRepository.count());
                break;
        }

        return stats;
    }

    /**
     * Lấy xu hướng thống kê dựa trên bộ lọc.
     *
     * @param filter Bộ lọc: "today", "week", "month", "year", "all"
     * @return TrendsDTO chứa dữ liệu xu hướng.
     */
    public TrendsDTO getTrends(String filter) {
        TrendsDTO trends = new TrendsDTO();

        switch (filter.toLowerCase()) {
            case "today":
                trends.setDates(Collections.singletonList(LocalDate.now().toString()));
                trends.setAccounts(Collections.singletonList(countAccountsToday()));
                trends.setInstructors(Collections.singletonList(countInstructorsToday()));
                trends.setStudents(Collections.singletonList(countStudentsToday()));
                trends.setCourses(Collections.singletonList(countCoursesToday()));
                trends.setCategories(Collections.singletonList(countCategoriesToday()));
                trends.setRoles(Collections.singletonList(countRolesToday()));
                break;
            case "week":
                trends.setDates(getLast7Days());
                trends.setAccounts(getCountsLast7Days("account"));
                trends.setInstructors(getCountsLast7Days("instructor"));
                trends.setStudents(getCountsLast7Days("student"));
                trends.setCourses(getCountsLast7Days("course"));
                trends.setCategories(getCountsLast7Days("category"));
                trends.setRoles(getCountsLast7Days("role"));
                break;
            case "month":
                trends.setDates(getLast30Days());
                trends.setAccounts(getCountsLast30Days("account"));
                trends.setInstructors(getCountsLast30Days("instructor"));
                trends.setStudents(getCountsLast30Days("student"));
                trends.setCourses(getCountsLast30Days("course"));
                trends.setCategories(getCountsLast30Days("category"));
                trends.setRoles(getCountsLast30Days("role"));
                break;
            case "year":
                trends.setDates(getLast12Months());
                trends.setAccounts(getCountsLast12Months("account"));
                trends.setInstructors(getCountsLast12Months("instructor"));
                trends.setStudents(getCountsLast12Months("student"));
                trends.setCourses(getCountsLast12Months("course"));
                trends.setCategories(getCountsLast12Months("category"));
                trends.setRoles(getCountsLast12Months("role"));
                break;
            default:
                // Default to all-time trends
                trends.setDates(Collections.singletonList("All Time"));
                trends.setAccounts(Collections.singletonList(accountRepository.count()));
                trends.setInstructors(Collections.singletonList(instructorRepository.count()));
                trends.setStudents(Collections.singletonList(studentRepository.count()));
                trends.setCourses(Collections.singletonList(courseRepository.count()));
                trends.setCategories(Collections.singletonList(categoryRepository.count()));
                trends.setRoles(Collections.singletonList(roleRepository.count()));
                break;
        }

        return trends;
    }

    // Helper methods to count based on filter
    private long countAccountsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return accountRepository.countByCreatedAtBetween(start, end);
    }

    private long countInstructorsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return instructorRepository.countByCreatedAtBetween(start, end);
    }

    private long countStudentsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return studentRepository.countByCreatedAtBetween(start, end);
    }

    private long countCoursesToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return courseRepository.countByCreatedAtBetween(start, end);
    }

    private long countCategoriesToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return categoryRepository.countByCreatedAtBetween(start, end);
    }

    private long countRolesToday() {
        // Assuming roles are predefined and not created dynamically
        // Return total number of roles
        return roleRepository.count();
    }

    private long countAccountsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return accountRepository.countByCreatedAtBetween(start, end);
    }

    private long countInstructorsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return instructorRepository.countByCreatedAtBetween(start, end);
    }

    private long countStudentsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return studentRepository.countByCreatedAtBetween(start, end);
    }

    private long countCoursesThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return courseRepository.countByCreatedAtBetween(start, end);
    }

    private long countCategoriesThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return categoryRepository.countByCreatedAtBetween(start, end);
    }

    private long countRolesThisWeek() {
        // Assuming roles are predefined
        return roleRepository.count();
    }

    private long countAccountsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return accountRepository.countByCreatedAtBetween(start, end);
    }

    private long countInstructorsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return instructorRepository.countByCreatedAtBetween(start, end);
    }

    private long countStudentsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return studentRepository.countByCreatedAtBetween(start, end);
    }

    private long countCoursesThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return courseRepository.countByCreatedAtBetween(start, end);
    }

    private long countCategoriesThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return categoryRepository.countByCreatedAtBetween(start, end);
    }

    private long countRolesThisMonth() {
        // Assuming roles are predefined
        return roleRepository.count();
    }

    private long countAccountsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return accountRepository.countByCreatedAtBetween(start, end);
    }

    private long countInstructorsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return instructorRepository.countByCreatedAtBetween(start, end);
    }

    private long countStudentsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return studentRepository.countByCreatedAtBetween(start, end);
    }

    private long countCoursesThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return courseRepository.countByCreatedAtBetween(start, end);
    }

    private long countCategoriesThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return categoryRepository.countByCreatedAtBetween(start, end);
    }

    // Các phương thức đếm cho xu hướng

    private List<String> getLast7Days() {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.getDayOfWeek().toString());
        }
        return days;
    }

    private List<Long> getCountsLast7Days(String type) {
        LocalDate today = LocalDate.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            counts.add(getCountByDate(type, date));
        }
        return counts;
    }

    private List<String> getLast30Days() {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.toString());
        }
        return days;
    }

    private List<Long> getCountsLast30Days(String type) {
        LocalDate today = LocalDate.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            counts.add(getCountByDate(type, date));
        }
        return counts;
    }

    private List<String> getLast12Months() {
        YearMonth currentMonth = YearMonth.now();
        List<String> months = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            months.add(month.getMonth().toString());
        }
        return months;
    }

    private List<Long> getCountsLast12Months(String type) {
        YearMonth currentMonth = YearMonth.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            counts.add(getCountByMonth(type, month));
        }
        return counts;
    }

    /**
     * Lấy số lượng dựa trên loại và ngày cụ thể.
     *
     * @param type Loại: "account", "instructor", "student", "course", "category", "role"
     * @param date Ngày cụ thể.
     * @return Số lượng.
     */
    private long getCountByDate(String type, LocalDate date) {
        LocalDateTime startLDT = date.atStartOfDay();
        LocalDateTime endLDT = date.atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return switch (type.toLowerCase()) {
            case "account" -> accountRepository.countByCreatedAtBetween(start, end);
            case "instructor" -> instructorRepository.countByCreatedAtBetween(start, end);
            case "student" -> studentRepository.countByCreatedAtBetween(start, end);
            case "course" -> courseRepository.countByCreatedAtBetween(start, end);
            case "category" -> categoryRepository.countByCreatedAtBetween(start, end);
            case "role" -> roleRepository.count();
            default -> 0;
        };
    }

    /**
     * Lấy số lượng dựa trên loại và tháng cụ thể.
     *
     * @param type  Loại: "account", "instructor", "student", "course", "category", "role"
     * @param month Tháng cụ thể.
     * @return Số lượng.
     */
    private long getCountByMonth(String type, YearMonth month) {
        LocalDateTime startLDT = month.atDay(1).atStartOfDay();
        LocalDateTime endLDT = month.atEndOfMonth().atTime(LocalTime.MAX);
        Date start = convertLocalDateTimeToDate(startLDT);
        Date end = convertLocalDateTimeToDate(endLDT);
        return switch (type.toLowerCase()) {
            case "account" -> accountRepository.countByCreatedAtBetween(start, end);
            case "instructor" -> instructorRepository.countByCreatedAtBetween(start, end);
            case "student" -> studentRepository.countByCreatedAtBetween(start, end);
            case "course" -> courseRepository.countByCreatedAtBetween(start, end);
            case "category" -> categoryRepository.countByCreatedAtBetween(start, end);
            case "role" -> roleRepository.count();
            default -> 0;
        };
    }
}
