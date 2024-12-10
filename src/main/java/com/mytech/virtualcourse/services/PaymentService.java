package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.PaymentStatus;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.PaymentRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private APIContext apiContext;

    /**
     * Khởi tạo thanh toán PayPal cho một khóa học
     * @param courseId Id khóa học
     * @return approval_url để redirect user đến PayPal
     */
    public String initiatePaypalPayment(Long courseId) throws Exception {
        // Giả sử student id=1 tồn tại
        Student student = studentRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        BigDecimal amount = course.getBasePrice();
        if (amount == null) {
            throw new RuntimeException("Course price not found");
        }

        // Tạo Payment trong DB
        Payment dbPayment = new Payment();
        dbPayment.setAmount(amount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.PAYPAL);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);

        // Thay vì List.of(course) (immutable), dùng danh sách mutable:
        List<Course> singleCourseList = new ArrayList<>();
        singleCourseList.add(course);
        dbPayment.setCourses(singleCourseList);

        dbPayment = paymentRepository.save(dbPayment);

        // URLs PayPal redirect
        String cancelUrl = "http://localhost:3000/cancel";
        String successUrl = "http://localhost:3000/success";

        // Tạo PayPal Payment
        com.paypal.api.payments.Payment createdPayment = createPayPalPayment(
                amount,
                "USD",
                "paypal",
                "sale",
                "Payment for course: " + course.getTitleCourse(),
                cancelUrl,
                successUrl
        );

        // Lưu paypalPaymentId vào DB
        dbPayment.setPaypalPaymentId(createdPayment.getId());
        paymentRepository.save(dbPayment);

        // Lấy approval_url
        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equalsIgnoreCase(link.getRel())) {
                return link.getHref();
            }
        }
        throw new RuntimeException("No approval URL returned by PayPal");
    }

    /**
     * Khởi tạo thanh toán PayPal cho nhiều khóa học
     */
    public String initiatePaypalPaymentForMultipleCourses(List<Long> courseIds) throws Exception {
        // Lấy Student id=1
        Student student = studentRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Lấy danh sách courses từ courseIds
        List<Course> courses = courseRepository.findAllById(courseIds);

        if (courses.isEmpty()) {
            throw new RuntimeException("No courses found for given IDs");
        }

        // Tính tổng amount
        BigDecimal totalAmount = courses.stream()
                .map(course -> {
                    BigDecimal price = course.getBasePrice();
                    if (price == null) {
                        throw new RuntimeException("Course price not found for courseId: " + course.getId());
                    }
                    return price;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo Payment cho nhiều khóa học
        Payment dbPayment = new Payment();
        dbPayment.setAmount(totalAmount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.PAYPAL);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);
        // courses từ database trả về thường là mutable,
        // nếu lo lắng thì có thể tạo list mới: new ArrayList<>(courses)
        dbPayment.setCourses(new ArrayList<>(courses));

        dbPayment = paymentRepository.save(dbPayment);

        // URL PayPal redirect
        String cancelUrl = "http://localhost:3000/cancel";
        String successUrl = "http://localhost:3000/success";

        // Tạo PayPal Payment
        com.paypal.api.payments.Payment createdPayment = createPayPalPayment(
                totalAmount,
                "USD",
                "paypal",
                "sale",
                "Payment for multiple courses",
                cancelUrl,
                successUrl
        );

        // Lưu paypalPaymentId
        dbPayment.setPaypalPaymentId(createdPayment.getId());
        paymentRepository.save(dbPayment);

        // Lấy approval_url
        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equalsIgnoreCase(link.getRel())) {
                return link.getHref();
            }
        }

        throw new RuntimeException("No approval URL returned by PayPal");
    }


    /**
     * Thực hiện thanh toán sau khi người dùng đã đồng ý trên PayPal
     */
    public Payment completePaypalPayment(String paymentId, String payerId) throws PayPalRESTException {
        // Thực thi payment trên PayPal
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        com.paypal.api.payments.Payment executedPayment = payment.execute(apiContext, execution);

        // In ra trạng thái để debug
        System.out.println("PayPal executed payment state: " + executedPayment.getState());

        // Tìm Payment trong DB theo paypalPaymentId
        Payment dbPayment = paymentRepository.findByPaypalPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("No matching Payment found for paypalPaymentId"));

        // Cập nhật trạng thái Payment
        String state = executedPayment.getState();
        if ("approved".equalsIgnoreCase(state) || "completed".equalsIgnoreCase(state)) {
            dbPayment.setStatus(PaymentStatus.Completed);
        } else {
            dbPayment.setStatus(PaymentStatus.Failed);
        }
        paymentRepository.save(dbPayment);

        return dbPayment;
    }

    private com.paypal.api.payments.Payment createPayPalPayment(
            BigDecimal total, String currency, String method, String intent,
            String description, String cancelUrl, String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(Collections.singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }
}
