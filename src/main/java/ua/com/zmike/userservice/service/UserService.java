package ua.com.zmike.userservice.service;

import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;

import java.time.LocalDate;
import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllByBirthDateRange(LocalDate from, LocalDate to);

    UserDto addOne(UserDto user);

    UserDto getOneById(Long id);

    UserDto updateOne(Long id, UserDto user);

    UserDto updateAddress(Long userId, AddressDto address);

    void deleteOneById(Long id);

}
