package ua.com.zmike.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerContract {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsersByBirthDateRange(@RequestParam(value = "from") LocalDate from,
                                                           @RequestParam(value = "to") LocalDate to) {
        log.info("Get all Users with birth date between {} and {}", from, to);
        return userService.getAllByBirthDateRange(from, to);
    }

    @Override
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        log.info("Get User by id: {}", id);
        return userService.getOneById(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUsers(@RequestBody UserDto user) {
        log.info("Add User with params: {}", user);
        return userService.addOne(user);
    }

    @Override
    @PutMapping("/{id}")
    public UserDto updateUsers(@PathVariable("id") Long id,
                               @RequestBody UserDto user) {
        log.info("Update User by id: {} for params: {}", id, user);
        return userService.updateOne(id, user);
    }

    @Override
    @PatchMapping("/{id}")
    public UserDto updateUserAddress(@PathVariable Long id,
                                     @RequestBody AddressDto address) {
        log.info("Update User by id: {}. Set new Address: {} ", id, address);
        return userService.updateAddress(id, address);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteUsers(@PathVariable("id") Long id) {
        log.info("Delete User by id: {}", id);
        userService.deleteOneById(id);
    }
}
