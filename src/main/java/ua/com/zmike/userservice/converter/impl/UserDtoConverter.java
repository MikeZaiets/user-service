package ua.com.zmike.userservice.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;


@Component
@RequiredArgsConstructor
public class UserDtoConverter implements DtoConverter<UserDto, User> {

    private final DtoConverter<AddressDto, Address> addressDtoConverter;

    public UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber())
                .addressDto(addressDtoConverter.convertToDto(user.getAddress()))
                .build();
    }

    public User convertFromDto(UserDto userDto) {
        var user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthDate(userDto.getBirthDate());
        user.setPhoneNumber(userDto.getPhoneNumber());
        return user;
    }
}
