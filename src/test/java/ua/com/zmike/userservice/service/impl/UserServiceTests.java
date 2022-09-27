package ua.com.zmike.userservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.exception.TargetNotFoundException;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;
import ua.com.zmike.userservice.repository.UserRepository;
import ua.com.zmike.userservice.service.AddressService;
import ua.com.zmike.userservice.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddress;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getUser;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getUserDto;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTests {

    private static final Long FIRST_ENTITY_ID = 1L;
    private static final Long NEW_ENTITY_ID = 6L;

    @Mock
    private DtoConverter<UserDto, User> userDtoConverter;
    @Mock
    private DtoConverter<AddressDto, Address> addressDtoConverter;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressService addressService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDtoConverter, addressDtoConverter, userRepository, addressService);
    }

    @Test
    void addOne_shouldAddUserAndNewAddress_whenAddressNotExistInDb() {
        // given
        var incomingUserDto = getUserDto();
        var user = getUser();
        var addressDto = getAddressDto();
        var newAddress = getAddress(NEW_ENTITY_ID);
        var savedUser = getUser(NEW_ENTITY_ID);
        savedUser.setAddress(newAddress);

        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setAddressDto(getAddressDto(NEW_ENTITY_ID));

        when(userDtoConverter
                .convertFromDto(incomingUserDto))
                .thenReturn(user);
        when(addressService
                .findByExample(addressDto))
                .thenReturn(Optional.empty());
        when(addressDtoConverter
                .convertFromDto(addressDto))
                .thenReturn(newAddress);
        user.setAddress(newAddress);
        when(userRepository
                .save(user))
                .thenReturn(savedUser);
        when(userDtoConverter
                .convertToDto(savedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.addOne(incomingUserDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userDtoConverter, times(1)).convertFromDto(incomingUserDto);
        verify(addressService, only()).findByExample(addressDto);
        verify(addressDtoConverter, only()).convertFromDto(addressDto);
        verify(userRepository, only()).save(user);
        verify(userDtoConverter, times(1)).convertToDto(savedUser);
        verifyNoMoreInteractions(userDtoConverter);
    }

    @Test
    void addOne_shouldAddUserWithAddressFromDb_whenAddressExistInDb() {
        // given
        var incomingUserDto = getUserDto();
        var user = getUser();
        var addressDto = getAddressDto();
        var addressFromDb = Optional.of(getAddress(FIRST_ENTITY_ID));
        var savedUser = getUser(NEW_ENTITY_ID);
        savedUser.setAddress(addressFromDb.get());

        var expectedResult = getUserDto(NEW_ENTITY_ID);
        expectedResult.setAddressDto(getAddressDto(FIRST_ENTITY_ID));

        when(userDtoConverter
                .convertFromDto(getUserDto()))
                .thenReturn(user);
        when(addressService
                .findByExample(addressDto))
                .thenReturn(addressFromDb);
        user.setAddress(addressFromDb.get());
        when(userRepository
                .save(user))
                .thenReturn(savedUser);
        when(userDtoConverter
                .convertToDto(savedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.addOne(incomingUserDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userDtoConverter, times(1)).convertFromDto(incomingUserDto);
        verify(addressService, only()).findByExample(addressDto);
        verify(userRepository, only()).save(user);
        verify(userDtoConverter, times(1)).convertToDto(savedUser);
        verifyNoMoreInteractions(userDtoConverter);
        verifyNoInteractions(addressDtoConverter);
    }

    @Test
    void getOneById_shouldReturn() {
        // given
        var userId = 1L;

        var optionalUserFromDb = Optional.of(getUser(userId));
        var userFromDb = optionalUserFromDb.get();
        var expectedResult = getUserDto(userId);

        when(userRepository
                .findById(userId))
                .thenReturn(optionalUserFromDb);
        when(userDtoConverter
                .convertToDto(userFromDb))
                .thenReturn(expectedResult);

        // test
        var result = userService.getOneById(userId);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, only()).findById(userId);
        verify(userDtoConverter, only()).convertToDto(optionalUserFromDb.get());
    }

    @Test
    void getOneById_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var userId = 1000L;
        var expectedException = new TargetNotFoundException("User", "id", userId);

        when(userRepository
                .findById(userId))
                .thenReturn(Optional.empty());

        // test
        var resultException = assertThrows(
                TargetNotFoundException.class, () -> userService.getOneById(userId));


        // verify
        assertEquals(expectedException.getMessage(), resultException.getMessage());
        verifyNoInteractions(userDtoConverter);
    }

    @Test
    void updateOne_shouldCreateNewAddressAndUpdateUserById_whenUserDataValidAndAddressNotExistInDb() {
        // given
        var userId = 1L;
        var incomingUserDto = getUserDto();

        var userFromDB = Optional.of(getUser(userId));
        var user = userFromDB.get();
        var addressDto = getAddressDto();
        var addressFromDb = Optional.of(getAddress(FIRST_ENTITY_ID));
        var updatedUser = getUser(userId);

        var expectedResult = getUserDto(userId);
        expectedResult.setAddressDto(getAddressDto(FIRST_ENTITY_ID));

        when(userRepository
                .findById(userId))
                .thenReturn(userFromDB);
        when(addressService
                .findByExample(addressDto))
                .thenReturn(addressFromDb);
        user.setAddress(addressFromDb.get());
        when(userRepository
                .save(user))
                .thenReturn(updatedUser);
        when(userDtoConverter
                .convertToDto(updatedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.updateOne(userId, incomingUserDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, times(1)).findById(userId);
        verify(addressService, only()).findByExample(addressDto);
        verify(userRepository, times(1)).save(user);
        verify(userDtoConverter, times(1)).convertToDto(updatedUser);
        verifyNoMoreInteractions(userDtoConverter, userRepository);
        verifyNoInteractions(addressDtoConverter);
    }

    @Test
    void updateOne_shouldSetAddressFromDbAndUpdateUserData_whenUserDataValidAndAddressExistInDb() {
        // given
        var userId = 1L;
        var incomingUserDto = getUserDto();

        var userFromDB = Optional.of(getUser(userId));
        var user = userFromDB.get();
        var addressDto = getAddressDto();
        var newAddress = getAddress(NEW_ENTITY_ID);
        var updatedUser = getUser(userId);

        var expectedResult = getUserDto(userId);
        expectedResult.setAddressDto(getAddressDto(NEW_ENTITY_ID));

        when(userRepository
                .findById(userId))
                .thenReturn(userFromDB);
        when(addressService
                .findByExample(addressDto))
                .thenReturn(Optional.empty());
        when(addressDtoConverter
                .convertFromDto(addressDto))
                .thenReturn(newAddress);
        user.setAddress(newAddress);
        when(userRepository
                .save(user))
                .thenReturn(updatedUser);
        when(userDtoConverter
                .convertToDto(updatedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.updateOne(userId, incomingUserDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, times(1)).findById(userId);
        verify(addressService, only()).findByExample(addressDto);
        verify(addressDtoConverter, only()).convertFromDto(addressDto);
        verify(userRepository, times(1)).save(user);
        verify(userDtoConverter, times(1)).convertToDto(updatedUser);
        verifyNoMoreInteractions(userDtoConverter, userRepository);
    }

    @Test
    void updateOne_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var userId = 1000L;
        var expectedException = new TargetNotFoundException("User", "id", userId);

        when(userRepository
                .findById(userId))
                .thenReturn(Optional.empty());

        // test
        var resultException = assertThrows(
                TargetNotFoundException.class, () -> userService.getOneById(userId));

        // verify
        assertEquals(expectedException.getMessage(), resultException.getMessage());
        verify(userRepository, only()).findById(userId);
        verifyNoInteractions(addressService, userDtoConverter, addressDtoConverter);
    }

    @Test
    void updateAddress_shouldSetAddressFromDbToUser_whenAddressExistInDb() {
        // given
        var userId = 1L;
        var incomingAddressDto = getAddressDto();

        var optionalUserFromDb = Optional.of(getUser(userId));
        var addressFromDb = Optional.of(getAddress(FIRST_ENTITY_ID));

        var userFromDb = optionalUserFromDb.get();
        var updatedUser = getUser(userId);

        var expectedAddress = getAddressDto(FIRST_ENTITY_ID);
        var expectedResult = getUserDto(userId);
        expectedResult.setAddressDto(expectedAddress);

        when(userRepository
                .findById(userId))
                .thenReturn(optionalUserFromDb);
        when(addressService
                .findByExample(incomingAddressDto))
                .thenReturn(addressFromDb);
        userFromDb.setAddress(addressFromDb.get());
        when(userRepository
                .save(userFromDb))
                .thenReturn(updatedUser);
        when(userDtoConverter
                .convertToDto(updatedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.updateAddress(userId, incomingAddressDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, times(1)).findById(userId);
        verify(addressService, only()).findByExample(incomingAddressDto);
        verify(userRepository, times(1)).save(userFromDb);
        verify(userDtoConverter, only()).convertToDto(updatedUser);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(addressDtoConverter);
    }

    @Test
    void updateAddress_shouldCreateNewAddressAndSetToUser_whenAddressNotExistInDb() {
        // given
        var userId = 1L;
        var incomingAddressDto = getAddressDto();

        var optionalUserFromDb = Optional.of(getUser(userId));
        var newAddress = getAddress(NEW_ENTITY_ID);

        var userFromDb = optionalUserFromDb.get();
        var updatedUser = getUser(userId);

        var expectedAddress = getAddressDto(NEW_ENTITY_ID);
        var expectedResult = getUserDto(userId);
        expectedResult.setAddressDto(expectedAddress);

        when(userRepository
                .findById(userId))
                .thenReturn(optionalUserFromDb);
        when(addressService
                .findByExample(incomingAddressDto))
                .thenReturn(Optional.empty());
        when(addressDtoConverter
                .convertFromDto(incomingAddressDto))
                .thenReturn(newAddress);
        userFromDb.setAddress(newAddress);
        when(userRepository
                .save(userFromDb))
                .thenReturn(updatedUser);
        when(userDtoConverter
                .convertToDto(updatedUser))
                .thenReturn(expectedResult);

        // test
        var result = userService.updateAddress(userId, incomingAddressDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, times(1)).findById(userId);
        verify(addressService, only()).findByExample(incomingAddressDto);
        verify(addressDtoConverter, only()).convertFromDto(incomingAddressDto);
        verify(userRepository, times(1)).save(userFromDb);
        verify(userDtoConverter, only()).convertToDto(updatedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateAddress_shouldThrowException_whenUserByIdNotExistInDb() {
        // given
        var userId = 1000L;
        var incomingAddressDto = getAddressDto();

        var expectedException = new TargetNotFoundException("User", "id", userId);

        when(userRepository
                .findById(userId))
                .thenReturn(Optional.empty());

        // test
        var resultException = assertThrows(
                TargetNotFoundException.class, () -> userService.updateAddress(userId, incomingAddressDto));

        // verify
        assertEquals(expectedException.getMessage(), resultException.getMessage());
        verify(userRepository, only()).findById(userId);
        verifyNoInteractions(addressService, userDtoConverter, addressDtoConverter);
    }

    @Test
    void deleteOneById_shouldDelete() {
        // given
        var deletedUserId = 1L;

        doNothing()
                .when(userRepository)
                .deleteById(deletedUserId);

        // test
        userService.deleteOneById(deletedUserId);

        // verify
        verify(userRepository, only()).deleteById(deletedUserId);
    }

    @Test
    void getAllByBirthDateRange_shouldReturnList_whenUsersWithBirthDateInRangeExistInDb() {
        // given
        var from = LocalDate.parse("2000-01-01");
        var to = LocalDate.parse("2005-01-01");

        var firstFoundUserByRange = getUser();
        var secondFoundUserByRange = getUser();
        secondFoundUserByRange.setEmail("some@gmail.com");
        var foundUsersByRange = Set.of(firstFoundUserByRange, secondFoundUserByRange);

        var firstFoundUserDto = getUserDto();
        var secondFoundUserDto = getUserDto();
        secondFoundUserDto.setEmail("some@gmail.com");
        var expectedResult = Set.of(firstFoundUserDto, secondFoundUserDto);

        when(userRepository
                .findAllByBirthDateBetween(from, to))
                .thenReturn(foundUsersByRange);
        when(userDtoConverter
                .convertToDto(firstFoundUserByRange))
                .thenReturn(firstFoundUserDto);
        when(userDtoConverter
                .convertToDto(secondFoundUserByRange))
                .thenReturn(secondFoundUserDto);

        // test
        var result = userService.getAllByBirthDateRange(from, to);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(userRepository, only()).findAllByBirthDateBetween(from, to);
        verify(userDtoConverter, times(2)).convertToDto(any(User.class));
        verifyNoMoreInteractions(userDtoConverter);
    }

    @Test
    void getAllByBirthDateRange_shouldReturnEmptyList_whenUsersWithBirthDateInRangeNotExistInDb() {
        // given
        var from = LocalDate.parse("2005-01-01");
        var to = LocalDate.parse("2010-01-01");

        when(userRepository
                .findAllByBirthDateBetween(from, to))
                .thenReturn(Collections.emptySet());

        // test
        var result = userService.getAllByBirthDateRange(from, to);

        // verify
        assertThat(result).isEqualTo(Collections.emptySet());
        verify(userRepository, only()).findAllByBirthDateBetween(from, to);
        verifyNoInteractions(userDtoConverter);
    }
}
