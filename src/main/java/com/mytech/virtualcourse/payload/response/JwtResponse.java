package com.mytech.virtualcourse.payload.response;

import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class JwtResponse {
    private Long id;
    private String token;
    private String username;
    private String firstName;
    private String lastName;
    private String fullname;
    private String type;
    private String image;
    private String email;
    private String avatar;
    private LocalDateTime createdAt;
    private List<String> roles;

    public JwtResponse() {
        this.fullname = (firstName != null && lastName != null) ? firstName + " " + lastName : null;
    }
    public void setDataFromInstructor(Instructor instructor){
        if(instructor != null){
            this.avatar = instructor.getPhoto();
            this.firstName = instructor.getFirstName();
            this.lastName = instructor.getLastName();
            this.fullname = firstName + " " + lastName;
        }
    }

    public void setDataFromStudent(Student student){
        if(student != null){
            this.avatar = student.getAvatar();
            this.firstName = student.getFirstName();
            this.lastName = student.getLastName();
            this.fullname = firstName + " " + lastName;
        }
    }

}
