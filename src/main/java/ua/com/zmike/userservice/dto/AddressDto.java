package ua.com.zmike.userservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@EqualsAndHashCode
public class AddressDto {

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Country should not be blank")
    private String country;

    @NotBlank(message = "City should not be blank")
    private String city;

    @NotBlank(message = "Street should not be blank")
    private String street;

    @NotBlank(message = "House number should not be blank")
    @Size(min = 1, max = 30, message = "House number should be between 1 and 30 symbols")
    @Pattern(regexp = "[\\p{LD}\\-/]+", message = "House number should be like: 120-A or 44D or 150/2")
    private String houseNumber;

    @Positive
    private Integer apartment;

    @Positive
    @NotNull(message = "Zipcode should not be null")
    private Integer zipcode;
}
