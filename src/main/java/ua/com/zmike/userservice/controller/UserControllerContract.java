package ua.com.zmike.userservice.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.exception.ApplicationConstraintViolationException;
import ua.com.zmike.userservice.exception.GenericException;
import ua.com.zmike.userservice.exception.TargetNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface UserControllerContract {

    @ApiOperation(
            value = "Get all Users with birth date between entered dates",
            response = UserDto.class,
            responseContainer = "List",
            produces = APPLICATION_JSON_VALUE,
            httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "User search by birth date date range completed successfully.",
                    response = UserDto.class),
            @ApiResponse(
                    code = 400, message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 404, message = "Target not found error",
                    response = TargetNotFoundException.class),
            @ApiResponse(
                    code = 500, message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    Collection<UserDto> getAllUsersByBirthDateRange(
            @ApiParam(
                    name = "from", value = "Searching birth date 'from'. Format: yyyy-mm-dd", example = "2022-02-22",
                    required = true) @NotNull LocalDate from,
            @ApiParam(
                    name = "to", value = "Searching birth date  'to'. Format: yyyy-mm-dd", example = "2022-02-22",
                    required = true) @NotNull LocalDate to);

    @ApiOperation(
            value = "Get one user by ID",
            response = UserDto.class,
            produces = APPLICATION_JSON_VALUE,
            httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "User by id successfully found.",
                    response = UserDto.class),
            @ApiResponse(
                    code = 400, message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 404, message = "Target not found error",
                    response = TargetNotFoundException.class),
            @ApiResponse(
                    code = 500, message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    UserDto getUserById(
            @ApiParam(name = "id", defaultValue = "1", value = "User id", example = "1") @NotNull Long id);

    @ApiOperation(
            value = "Create one user by data from request",
            response = UserDto.class,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 201, message = "User successfully created.",
                    response = UserDto.class),
            @ApiResponse(
                    code = 400, message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 500, message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    UserDto createUsers(
            @Parameter(description = "User DTO for create", required = true) @Valid @NotNull UserDto user);

    @ApiOperation(
            value = "Update one user by id by DTO",
            response = UserDto.class,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "User successfully updated.",
                    response = UserDto.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 500,
                    message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    UserDto updateUsers(
            @ApiParam(name = "id", value = "User id", example = "1") @NotNull Long id,
            @Parameter(description = "User DTO for Update", required = true) @Valid @NotNull UserDto user);


    @ApiOperation(
            value = "Update address to one user by id",
            response = UserDto.class,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            httpMethod = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "User address successfully updated.",
                    response = UserDto.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 500,
                    message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    UserDto updateUserAddress(
            @ApiParam(name = "id", value = "User id", example = "1") @NotNull Long id,
            @Parameter(description = "User DTO for Update", required = true) @Valid @NotNull AddressDto address);

    @ApiOperation(
            value = "Delete one user by ID",
            httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "User by id successfully deleted."),
            @ApiResponse(
                    code = 400, message = "Bad Request. Custom error code will be provided",
                    response = ApplicationConstraintViolationException.class),
            @ApiResponse(
                    code = 404, message = "Target not found error",
                    response = TargetNotFoundException.class),
            @ApiResponse(
                    code = 500, message = "Server error. Something wrong happened!",
                    response = GenericException.class)})
    void deleteUsers(
            @ApiParam(name = "id", value = "User id", example = "1") @NotNull Long id);

}
