package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "firstName",
        "lastName",
        "yearOfBirth",
        "group",
        "studentID",
        "email",
        "phone",
        "street",
        "city",
        "state",
        "country",
        "postalCode",
        "faculty",
        "yearOfStudy",
        "major",
        "GPA",
        "status",
        "admissionDate",
        "graduationDate",
        "degree",
        "institution",
        "completionYear",
        "scholarship",
        "employer"
})
public class Person {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;

    @JsonProperty("group")
    private String group;

    @JsonProperty("studentID")
    private String studentID;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("faculty")
    private String faculty;

    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;

    @JsonProperty("major")
    private String major;

    @JsonProperty("GPA")
    private Double GPA;

    @JsonProperty("status")
    private String status;

    @JsonProperty("admissionDate")
    private String admissionDate;

    @JsonProperty("graduationDate")
    private String graduationDate;

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private Integer completionYear;

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @JsonProperty("employer")
    private String employer;
}
