package ua.com.zmike.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.com.zmike.userservice.validator.MinAge;
import ua.com.zmike.userservice.validator.Phone;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@ApiModel(value = "User", description = "DTO for User model")
public class UserDto {

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Long id;

    @ApiModelProperty(example = "example@email.ex", required = true)
    @NotBlank(message = "Email address should not be blank")
    @Email(regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+.[A-Za-z0-9]+", message = "Input valid Email address please")
    protected String email;

    @ApiModelProperty(example = "Boris", required = true)
    @NotBlank(message = "First Name should not be blank")
    @Size(min = 2, max = 30, message = "First name should be between 2 and 30 characters")
    protected String firstName;

    @ApiModelProperty(example = "Jonson", required = true)
    @NotBlank(message = "Last Name should not be blank")
    @Size(min = 2, max = 30, message = "Last Name should be between 2 and 30 characters")
    protected String lastName;

    @ApiModelProperty(notes = "Contain just 10 digits", example = "0979876543", required = true)
    @NotBlank(message = "Phone number should not be blank")
    @Phone(pattern = "[0-9]+", min = 10, max = 10, message = "Invalid phone number! Example: 0979876543")
    protected String phoneNumber;

    @MinAge
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Valid
    @NotNull
    private AddressDto addressDto;

}
