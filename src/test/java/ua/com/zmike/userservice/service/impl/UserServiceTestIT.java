package ua.com.zmike.userservice.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.exception.IncorrectValueException;
import ua.com.zmike.userservice.exception.TargetNotFoundException;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;
import ua.com.zmike.userservice.repository.UserRepository;
import ua.com.zmike.userservice.service.AddressService;
import ua.com.zmike.userservice.service.UserService;
import ua.com.zmike.userservice.testUtil.TestDbManager;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getUserDto;

@SpringBootTest
//@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTestIT {
    // db contain 5 users and 5 address
    private static final Long FIRST_ENTITY_ID = 1L;
    private static final Long LAST_ENTITY_ID = 5L;
    private static final Long NEW_ENTITY_ID = 6L;

    @Autowired
    private DtoConverter<UserDto, User> userDtoConverter;
    @Autowired
    private DtoConverter<AddressDto, Address> addressDtoConverter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestDbManager dbManager;

    @BeforeAll
    void beforeAll() {
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

    // когда почта существует - ошибка

    @Test
    void addOne_shouldAddUserAndNewAddress_whenUserDataValidAndAddressNotExistInDb() {
        // given
        // for user - email is unique field
        var newMail = "new.mail@gmail.com";
        // for address - all fields is unique fields set
        var newCountry = "newCountry";

        var addressDto = getAddressDto();
        addressDto.setCountry(newCountry);
        var userDto = getUserDto();
        userDto.setEmail(newMail);
        userDto.setAddressDto(addressDto);

        var newAddressDto = getAddressDto(NEW_ENTITY_ID);
        newAddressDto.setCountry(newCountry);
        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setEmail(newMail);
        expectedResult.setAddressDto(newAddressDto);

        // test
        var result = userService.addOne(userDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(NEW_ENTITY_ID, result.getId());
        assertEquals(newAddressDto, result.getAddressDto());
        assertEquals(NEW_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void addOne_shouldAddUserWithAddressFromDb_whenUserDataValidAndAAddressExistInDb() {
        // given
        // for user - email is unique field
        var newMail = "new.mail@gmail.com";

        var addressDto = getAddressDto();
        var userDto = getUserDto();
        userDto.setEmail(newMail);
        userDto.setAddressDto(addressDto);

        var addressFromDb = getAddressDto(FIRST_ENTITY_ID);
        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setEmail(newMail);
        expectedResult.setAddressDto(addressFromDb);

        // test
        var result = userService.addOne(userDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(NEW_ENTITY_ID, result.getId());
        assertEquals(addressFromDb, result.getAddressDto());
        assertEquals(FIRST_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void addOne_shouldThrowException_whenUniqueUserExistInDb() {
        // given
        var userDto = getUserDto();
        var addressDto = getAddressDto();
        userDto.setAddressDto(addressDto);

        // in DB contain 5 addresses
        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setAddressDto(addressDto);

        // test and verify
        var resultException = assertThrows(
                DataIntegrityViolationException.class, () -> userService.addOne(userDto));

        // for user - email is unique identifier
        assertTrue(resultException.getMostSpecificCause()
                .getMessage().contains(String.format("(email)=(%s)", userDto.getEmail())));
    }

    @Test
    void getOneById_shouldReturn() {
        // given
        var incomingId = 1L;
        var addressDto = getAddressDto(FIRST_ENTITY_ID);
        var expectedResult = getUserDto(FIRST_ENTITY_ID);
        expectedResult.setAddressDto(addressDto);

        // test
        var result = userService.getOneById(incomingId);

        // verify
        assertNotNull(result);
        assertEquals(result.getId(), incomingId);
        assertEquals(expectedResult, result);
        assertNotNull(result.getAddressDto());
    }

    @Test
    void getOneById_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var incomingId = 1000L;
        var expectedMessage = String.format("Target: User not found by: id = %d", incomingId);

        // test
        var resultExceptionMessage = assertThrows(
                TargetNotFoundException.class, () -> userService.getOneById(incomingId)).getMessage();

        // verify
        assertEquals(expectedMessage, resultExceptionMessage);
    }

    @Test
    void updateOne_shouldCreateNewAddressAndUpdateUserById_whenUserDataValidAndAddressNotExistInDb() {
        // given
        var updatedUserId = 1L;
        var newMail = "new.mail@gmail.com";
        var newFirstName = "newFirstName";
        // for address - all fields is unique field
        var newCountry = "newCountry";

        var addressDto = getAddressDto();
        addressDto.setCountry(newCountry);
        var userDto = getUserDto();
        userDto.setEmail(newMail);
        userDto.setFirstName(newFirstName);
        userDto.setAddressDto(addressDto);

        var newAddressDto = getAddressDto(NEW_ENTITY_ID);
        newAddressDto.setCountry(newCountry);
        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setEmail(newMail);
        expectedResult.setFirstName(newFirstName);
        expectedResult.setAddressDto(newAddressDto);

        // test
        var result = userService.updateOne(updatedUserId, userDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(updatedUserId, result.getId());
        assertEquals(newAddressDto, result.getAddressDto());
        assertEquals(NEW_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void updateOne_shouldSetAddressFromDbAndUpdateUserData_whenUserDataValidAndAddressExistInDb() {
        // given
        var updatedUserId = 1L;
        var newFirstName = "newFirstName";

        var addressDto = getAddressDto();
        var userDto = getUserDto();
        userDto.setFirstName(newFirstName);
        userDto.setAddressDto(addressDto);

        var addressFromDb = getAddressDto(FIRST_ENTITY_ID);
        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setFirstName(newFirstName);
        expectedResult.setAddressDto(addressFromDb);

        // test
        var result = userService.updateOne(updatedUserId, userDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(updatedUserId, result.getId());
        assertEquals(addressFromDb, result.getAddressDto());
        assertEquals(FIRST_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void updateOne_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var updatedUserId = 1000L;
        var newFirstName = "someName";
        var expectedMessage = String.format("Target: User not found by: id = %d", updatedUserId);

        var userDto = getUserDto();
        userDto.setFirstName(newFirstName);

        // test
        var resultExceptionMessage = assertThrows(
                TargetNotFoundException.class, () -> userService.updateOne(updatedUserId, userDto)).getMessage();

        // verify
        assertEquals(expectedMessage, resultExceptionMessage);
    }

    @Test
    void updateOne_shouldThrowException_whenUserWithUniqueKeyExistInDb() {
        // given
        var updatedUserId = 1L;
        var newMail = "User2@gmail.com";

        var userDto = getUserDto();
        userDto.setEmail(newMail);

        // test
        var resultException = assertThrows(
                DataIntegrityViolationException.class, () -> userService.updateOne(updatedUserId, userDto));

        // for user - email is unique identifier
        assertTrue(resultException.getMostSpecificCause()
                .getMessage().contains(String.format("(email)=(%s)", userDto.getEmail())));
    }

    @Test
    void updateAddress_shouldCreateNewAddressAndSetToUser_whenAddressNotExistInDb() {
        // given
        var userId = 1L;
        var newCountry = "newCountry";

        var addressDto = getAddressDto();
        addressDto.setCountry(newCountry);

        // in DB contain 5 addresses
        var newAddressDto = getAddressDto(NEW_ENTITY_ID);
        newAddressDto.setCountry(newCountry);
        var expectedResult = getUserDto(userId);
        expectedResult.setAddressDto(newAddressDto);

        // test
        var result = userService.updateAddress(userId, addressDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(addressDto, result.getAddressDto());
        assertEquals(NEW_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void updateAddress_shouldSetAddressFromDbToUser_whenAddressExistInDb() {
        // given
        var userId = 1L;
        var addressDto = getAddressDto();

        var expectedResult = getUserDto(FIRST_ENTITY_ID);
        var expectedAddressDto = getAddressDto(FIRST_ENTITY_ID);
        expectedResult.setAddressDto(expectedAddressDto);

        // test
        var result = userService.updateAddress(userId, addressDto);

        // verify
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(addressDto, result.getAddressDto());
        assertEquals(FIRST_ENTITY_ID, result.getAddressDto().getId());
        assertEquals(expectedResult, result);
    }

    @Test
    void updateAddress_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var userId = 1000L;
        var addressDto = getAddressDto();

        var expectedMessage = String.format("Target: User not found by: id = %d", userId);

        // test
        var resultExceptionMessage = assertThrows(
                TargetNotFoundException.class, () -> userService.updateAddress(userId, addressDto)).getMessage();

        // verify
        assertEquals(expectedMessage, resultExceptionMessage);
    }

    @Test
    void deleteOneById_shouldDelete() {
        // given
        var deletedUserId = 1L;

        // test
        assertDoesNotThrow(() -> userService.deleteOneById(deletedUserId));

        // verify
        assertEquals(userRepository.findAll().size(), LAST_ENTITY_ID - 1);
    }

    @Test
    void deleteOneById_shouldThrowException_whenUserByIdNotExistInDb() {
        //given
        var deletedUserId = 1000L;
        var expectedMessage = String.format(
                "No class ua.com.zmike.userservice.model.User entity with id %d exists!", deletedUserId);

        // test
        var resultException = assertThrows(
                EmptyResultDataAccessException.class, () -> userService.deleteOneById(deletedUserId));

        // verify
        assertEquals(expectedMessage, resultException.getMostSpecificCause().getMessage());
        assertEquals(LAST_ENTITY_ID, userRepository.findAll().size());
    }


    @Test
    void getAllByBirthDateRange_shouldReturnList_whenUsersWithBirthDateInRangeExistInDb() {
        // given
        var from = LocalDate.parse("2000-01-01");
        var to = LocalDate.parse("2002-01-01");

        var expectedUser = getUserDto();
        var expectedAddressDto = getAddressDto(FIRST_ENTITY_ID);
        expectedUser.setAddressDto(expectedAddressDto);
        var expectedResult = Set.of(expectedUser);

        // test
        var result = userService.getAllByBirthDateRange(from, to);

        // verify
        assertNotNull(result);
        assertEquals(expectedResult, result);
        result.forEach(Assertions::assertNotNull);
        result.forEach(userDto -> assertNotNull(userDto.getId()));
        result.forEach(userDto -> {
            assertTrue(userDto.getBirthDate().isAfter(from));
            assertTrue(userDto.getBirthDate().isBefore(to));
        });
    }

    @Test
    void getAllByBirthDateRange_shouldReturnEmptyList_whenUsersWithBirthDateInRangeNotExistInDb() {
        // given
        var from = LocalDate.parse("2005-01-01");
        var to = LocalDate.parse("2010-01-01");

        // test
        var result = userService.getAllByBirthDateRange(from, to);

        // verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByBirthDateRange_shouldThrowException_whenDateToEqualsOrLessDateFrom() {
        // given
        var from = LocalDate.parse("2005-01-01");
        var to = LocalDate.parse("2005-01-01");
        var expectedMessage = "Incorrect value: Date 'date from' must be less then 'date to'";

        // test
        var resultException = assertThrows(
                IncorrectValueException.class, () -> userService.getAllByBirthDateRange(from, to));

        // verify
        assertEquals(expectedMessage, resultException.getMessage());
    }
}
