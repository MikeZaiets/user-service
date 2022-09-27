package ua.com.zmike.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.exception.TargetNotFoundException;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;
import ua.com.zmike.userservice.repository.UserRepository;
import ua.com.zmike.userservice.service.AddressService;
import ua.com.zmike.userservice.service.UserService;
import ua.com.zmike.userservice.util.ValidationUtil;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final DtoConverter<UserDto, User> userDtoConverter;
    private final DtoConverter<AddressDto, Address> addressDtoConverter;

    private final UserRepository userRepository;
    private final AddressService addressService;

    @Transactional
    @Override
    public UserDto addOne(UserDto userDto) {
        var user = userDtoConverter.convertFromDto(userDto);
        applyAddress(user, userDto.getAddressDto());
        return saveAndConvertToDto(user);
    }

    @Override
    public UserDto getOneById(Long id) {
        return userDtoConverter.convertToDto(getExistingUserById(id));
    }

    @Transactional
    @Override
    public UserDto updateOne(Long id, UserDto userDto) {
        var user = getExistingUserById(id);
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setBirthDate(userDto.getBirthDate());
        applyAddress(user, userDto.getAddressDto());
        return saveAndConvertToDto(user);
    }

    @Transactional
    @Override
    public UserDto updateAddress(Long userId, AddressDto addressDto) {
        var user = getExistingUserById(userId);
        applyAddress(user, addressDto);
        return saveAndConvertToDto(user);
    }

    @Transactional
    @Override
    public void deleteOneById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Set<UserDto> getAllByBirthDateRange(LocalDate from, LocalDate to) {
        ValidationUtil.validateDateRange(from, to);
        return userRepository.findAllByBirthDateBetween(from, to)
                .stream()
                .map(userDtoConverter::convertToDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private UserDto saveAndConvertToDto(User user) {
        var savedUser = userRepository.save(user);
        return userDtoConverter.convertToDto(savedUser);
    }

    private void applyAddress(User user, AddressDto addressDto) {
        var newAddress = addressService.findByExample(addressDto);
        if (newAddress.isPresent()) {
            user.setAddress(newAddress.get());
        } else {
            user.setAddress(addressDtoConverter.convertFromDto(addressDto));
        }
    }

    private User getExistingUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new TargetNotFoundException("User", "id", id));
    }
}
