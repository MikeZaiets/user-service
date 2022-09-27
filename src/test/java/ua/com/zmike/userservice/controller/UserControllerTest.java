package ua.com.zmike.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.zmike.userservice.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getUserDto;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final Long FIRST_ENTITY_ID = 1L;
    private static final Long NEW_ENTITY_ID = 6L;

    private static ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void getAllUsersByBirthDateRange_shouldReturn_whenUsersWithBirthDateInRangeExistInDb() throws Exception {
        // given
        var from = "2000-01-01";
        var to = "2005-01-01";
        var result = Set.of(getUserDto(FIRST_ENTITY_ID));
        var expectedResponse = mapper.writeValueAsString(result);

        when(userService
                .getAllByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to)))
                .thenReturn(result);

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
        verify(userService, only()).getAllByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to));
    }

    @Test
    void getAllUsersByBirthDateRange_shouldReturnEmpty_whenUsersWithBirthDateInRangeNotExistInDb() throws Exception {
        // given
        var from = "2005-01-01";
        var to = "2010-01-01";
        var expectedResponse = "[]";

        when(userService
                .getAllByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to)))
                .thenReturn(Collections.emptySet());

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
        verify(userService, only()).getAllByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to));
    }

    @Test
    void getUserById_shouldReturnUserDto() throws Exception {
        // given
        var incomingId = 1L;
        var foundUser = getUserDto(incomingId);

        var expectedResponse = mapper.writeValueAsString(getUserDto(incomingId));

        when(userService
                .getOneById(incomingId))
                .thenReturn(foundUser);

        // test
        var response = mockMvc
                .perform(get("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
        verify(userService, only()).getOneById(incomingId);
    }

    @Test
    void createUser_shouldReturnUserDto() throws Exception {
        // given
        var incomingUserDto = getUserDto();
        var createdUser = getUserDto(NEW_ENTITY_ID);
        var expectedResponse = mapper.writeValueAsString(createdUser);

        when(userService
                .addOne(incomingUserDto))
                .thenReturn(createdUser);

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
        verify(userService, only()).addOne(incomingUserDto);
    }

    @Test
    void updateUser_shouldReturnUserDto() throws Exception {
        // given
        var incomingId = 1L;
        var incomingUserDto = getUserDto();

        var updatedUser = getUserDto(incomingId);
        var expectedResponse = mapper.writeValueAsString(updatedUser);

        when(userService
                .updateOne(incomingId, incomingUserDto))
                .thenReturn(updatedUser);

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
        verify(userService, only()).updateOne(incomingId, incomingUserDto);
    }

    @Test
    void updateUserAddress_shouldReturnUserDto() throws Exception {
        // given
        var incomingId = 1L;
        var newAddress = getAddressDto();

        var updatedUser = getUserDto(incomingId);
        var expectedResponse = mapper.writeValueAsString(updatedUser);

        when(userService
                .updateAddress(incomingId, newAddress))
                .thenReturn(updatedUser);

        // test
        var response = mockMvc
                .perform(patch("/users/{id}", incomingId)
                        .content(mapper.writeValueAsString(newAddress))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
        verify(userService, only()).updateAddress(incomingId, newAddress);
    }

    @Test
    void deleteUser_shouldReturnNothing() throws Exception {
        // given
        var incomingId = 1L;

        doNothing()
                .when(userService)
                .deleteOneById(incomingId);

        // test
        var response = mockMvc
                .perform(delete("/users/{id}", incomingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // verify
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isBlank();
        verify(userService).deleteOneById(incomingId);
    }
}
