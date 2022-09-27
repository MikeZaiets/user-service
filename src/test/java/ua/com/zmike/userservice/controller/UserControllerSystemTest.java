package ua.com.zmike.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.exception.IncorrectValueException;
import ua.com.zmike.userservice.exception.TargetNotFoundException;
import ua.com.zmike.userservice.testUtil.TestDbManager;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getUserDto;

@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerSystemTest {

    private static final Long FIRST_ENTITY_ID = 1L;
    private static final Long NEW_ENTITY_ID = 6L;

    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestDbManager dbManager;

    @BeforeAll
    void beforeAll() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        dbManager.initDataBase();
    }

    @BeforeEach
    void setUp() {
        dbManager.fillTables();
    }

    @AfterEach
    void tearDown() {
        dbManager.cleanTables();
    }

    @Test
    void getAllUsersByBirthDateRange_shouldReturn_whenValidDatesAndUserWithBirthDateInRangeExistInDB() throws Exception {
        // given
        var from = "2000-01-01";
        var to = "2001-01-01";

        var foundUserDto = getUserDto(FIRST_ENTITY_ID);
        foundUserDto.setAddressDto(getAddressDto(FIRST_ENTITY_ID));

        var result = Set.of(foundUserDto);
        var expectedResponse = mapper.writeValueAsString(result);

        // test
        var response = mockMvc
                .perform(get("/users")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void getAllUsersByBirthDateRange_shouldReturnEmpty_whenUserWithBirthDateInRangeNotExistInDB() throws Exception {
        // given
        var from = "2010-01-01";
        var to = "2020-01-01";
        var expectedResponse = "[]";

        // test
        var response = mockMvc
                .perform(get("/users")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }


    @Test
    void getAllUsersByBirthDateRange_shouldReturnExceptionDto_whenDateToEqualsOrLessDateFrom() throws Exception {
        // given
        var from = "2005-01-01";
        var to = "2000-01-01";
        var exception = new IncorrectValueException("Date 'date from' must be less then 'date to'");
        var expectedResponse = mapper.writeValueAsString(exception.getExceptionDto());

        // test
        var response = mockMvc
                .perform(get("/users")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void getAllUsersByBirthDateRange_shouldReturnExceptionDto_whenSomeValueNotValidDate() throws Exception {
        // given
        var from = "2000-01-01";
        var to = "01-01-2001";
        var exceptionText = String.format("Parse attempt failed for value [%s]", to);

        // test
        var response = mockMvc
                .perform(get("/users")
                        .param("from", from)
                        .param("to", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().contains(ConversionFailedException.class.getSimpleName()));
        assertTrue(response.getContentAsString().contains(exceptionText));
    }

    @Test
    void getUserById_shouldReturnUserDto() throws Exception {
        // given
        var incomingId = 1L;

        var userById = getUserDto(FIRST_ENTITY_ID);
        userById.setAddressDto(getAddressDto(FIRST_ENTITY_ID));
        var expectedResponse = mapper.writeValueAsString(userById);

        // test
        var response = mockMvc
                .perform(get("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void getUserById_shouldReturnExceptionDto_whenUserByIdNotExistInDb() throws Exception {
        // given
        var incomingId = 1000L;

        var notFoundException = new TargetNotFoundException("User", "id", incomingId);
        var expectedResponse = mapper.writeValueAsString(notFoundException.getExceptionDto());

        // test
        var response = mockMvc
                .perform(get("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void createUser_shouldReturnUserDtoWithAddressFromDb_whenUserAddressExistInDb() throws Exception {
        // given
        // for user - email and id is unique field
        var email = "some@email.com";

        var incomingUserDto = getUserDto();
        incomingUserDto.setEmail(email);

        var createdUser = getUserDto(NEW_ENTITY_ID);
        createdUser.setAddressDto(getAddressDto(FIRST_ENTITY_ID));
        createdUser.setEmail(email);
        var expectedResponse = mapper.writeValueAsString(createdUser);

        // test
        var response = mockMvc
                .perform(post("/users")
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void createUser_shouldReturnUserDtoWithCreatedAddress_whenUserAddressNotExistInDb() throws Exception {
        // given
        // for address:  any new value in any field - is a new Address, that needs to be saved in the database
        var email = "some@email.com";
        var someCountry = "SomeCountry";

        var newAddress = getAddressDto();
        newAddress.setCountry(someCountry);

        var incomingUserDto = getUserDto();
        incomingUserDto.setEmail(email);
        incomingUserDto.setAddressDto(newAddress);

        var createdAddressDto = getAddressDto(NEW_ENTITY_ID);
        createdAddressDto.setCountry(someCountry);
        var createdUser = getUserDto(NEW_ENTITY_ID);
        createdUser.setAddressDto(createdAddressDto);
        createdUser.setEmail(email);

        var expectedResponse = mapper.writeValueAsString(createdUser);

        // test
        var response = mockMvc
                .perform(post("/users")
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void createUser_shouldReturnExceptionDto_whenUserWithUniqueFieldExistInDb() throws Exception {
        // given
        // for user - email and id is unique field
        var existingEmail = "User2@gmail.com";
        var incomingUserDto = getUserDto();
        incomingUserDto.setEmail(existingEmail);

        // test
        var response = mockMvc
                .perform(post("/users")
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertTrue(response.getContentAsString().contains("GENERIC_EXCEPTION"));
        assertTrue(response.getContentAsString().contains("could not execute statement"));
    }

    @Test
    void createUser_shouldReturnExceptionDto_whenIncomingDataNotValid() throws Exception {
        // given
        var incomingId = 1L;
        // for example set birth date less than now() or Age less than 18 yo
        var birthDate = LocalDate.now();

        var incomingUserDto = getUserDto();
        incomingUserDto.setBirthDate(birthDate);

        // test
        var response = mockMvc
                .perform(post("/users")
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().contains("CONSTRAINT_VIOLATION"));
        assertTrue(response.getContentAsString().contains("Unacceptable data: "));
    }

    @Test
    void updateUser_shouldReturnUserDtoWithUpdatedData_whenValidIncomingDataAndUserExistInDb() throws Exception {
        // given
        var incomingId = 1L;
        var newEmail = "new@email.com";

        var incomingUserDto = getUserDto();
        incomingUserDto.setEmail(newEmail);

        var updatedUser = getUserDto(incomingId);
        updatedUser.setEmail(newEmail);
        updatedUser.setAddressDto(getAddressDto(FIRST_ENTITY_ID));
        var expectedResponse = mapper.writeValueAsString(updatedUser);

        // test
        var response = mockMvc
                .perform(put("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void updateUser_shouldReturnExceptionDto_whenUserByIdNotExistInDb() throws Exception {
        // given
        var incomingId = 1000L;
        var incomingUserDto = getUserDto();

        var notFoundException = new TargetNotFoundException("User", "id", incomingId);
        var expectedResponse = mapper.writeValueAsString(notFoundException.getExceptionDto());

        // test
        var response = mockMvc
                .perform(put("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void updateUser_shouldReturnExceptionDto_whenIncomingDataNotValid() throws Exception {
        // given
        var incomingId = 1L;
        // for example set birth date less than now() or Age less than 18 yo
        var birthDate = LocalDate.now();

        var incomingUserDto = getUserDto();
        incomingUserDto.setBirthDate(birthDate);

        // test
        var response = mockMvc
                .perform(put("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().contains("CONSTRAINT_VIOLATION"));
        assertTrue(response.getContentAsString().contains("Unacceptable data: "));
    }

    @Test
    void updateUserAddress_shouldReturnUserDtoWithAddressFromDb_whenAddressExistInDb() throws Exception {
        // given
        var incomingId = 1L;
        var incomingAddressDto = getAddressDto();

        var updatedUser = getUserDto(incomingId);
        updatedUser.setAddressDto(getAddressDto(FIRST_ENTITY_ID));
        var expectedResponse = mapper.writeValueAsString(updatedUser);

        // test
        var response = mockMvc
                .perform(patch("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingAddressDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    void updateUserAddress_shouldReturnUserDtoWithCreatedAddress_whenAddressNotExistInDb() throws Exception {
        // given
        var incomingId = 1L;
        // for address:  any new value in any field - is a new Address, that needs to be saved in the database
        var someCountry = "SomeCountry";

        var incomingAddressDto = getAddressDto();
        incomingAddressDto.setCountry(someCountry);
        var incomingUserDto = getUserDto();
        incomingUserDto.setAddressDto(incomingAddressDto);

        var addressDto = getAddressDto(NEW_ENTITY_ID);
        addressDto.setCountry(someCountry);
        var createdUser = getUserDto(incomingId);
        createdUser.setAddressDto(addressDto);

        var expectedResponse = mapper.writeValueAsString(createdUser);

        // test
        var response = mockMvc
                .perform(patch("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingAddressDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test()
    void updateUserAddress_shouldReturnExceptionDto_whenUserByIdNotExistInDb() throws Exception {
        // given
        var incomingId = 1000L;
        var incomingAddressDto = getAddressDto();

        var notFoundException = new TargetNotFoundException("User", "id", incomingId);
        var expectedResponse = mapper.writeValueAsString(notFoundException.getExceptionDto());

        // test
        var response = mockMvc
                .perform(patch("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingAddressDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test()
    void updateUserAddress_shouldReturnExceptionDto_whenNotValidIncomingAddressData() throws Exception {
        // given
        var incomingId = 1L;
        var incomingAddressDto = AddressDto.builder().build();

        // test
        var response = mockMvc
                .perform(patch("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(incomingAddressDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().contains("CONSTRAINT_VIOLATION"));
        assertTrue(response.getContentAsString().contains("Unacceptable data: "));
    }

    @Test
    void deleteUser_shouldReturnNothing() throws Exception {
        // given
        var incomingId = 1L;

        // test
        var response = mockMvc
                .perform(delete("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isBlank();
    }

    @Test
    void deleteUser_shouldReturnExceptionDto_whenUserByIdNotExistInDb() throws Exception {
        // given
        var incomingId = 1000L;
        var exceptionText = String.format("{\"" +
                "name\":\"GENERIC_EXCEPTION\",\"" +
                "message\":\"Exception: Unexpected 500 error: " +
                "No class ua.com.zmike.userservice.model.User entity with id %d exists!\"}", incomingId);

        // test
        var response = mockMvc
                .perform(delete("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo(exceptionText);
    }
}
