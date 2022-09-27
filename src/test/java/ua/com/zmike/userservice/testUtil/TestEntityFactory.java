package ua.com.zmike.userservice.testUtil;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.ObjectUtils;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.dto.UserDto;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;

import java.time.LocalDate;
import java.util.Collections;

@UtilityClass
public class TestEntityFactory {

    public static UserDto getUserDto(Long... id) {
        return UserDto.builder()
                .id(ObjectUtils.isEmpty(id) ? null : id[0])
                .email("User1@gmail.com")
                .firstName("User1")
                .lastName("User1")
                .phoneNumber("1234567890")
                .birthDate(LocalDate.parse("2000-02-02"))
                .addressDto(getAddressDto())
                .build();
    }

    public static AddressDto getAddressDto(Long... id) {
        return AddressDto.builder()
                .id(ObjectUtils.isEmpty(id) ? null : id[0])
                .country("Country1")
                .city("City1")
                .street("Street1")
                .houseNumber("1")
                .apartment(1)
                .zipcode(1)
                .build();
    }

    public static User getUser(Long... id) {
        var userDto = getUserDto(id);
        var user = new User();
        user.setId(user.getId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthDate(userDto.getBirthDate());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(getAddress());
        return user;
    }

    public static Address getAddress(Long... id) {
        var addressDto = getAddressDto(id);
        var address = new Address();
        address.setId(addressDto.getId());
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHouseNumber(addressDto.getHouseNumber());
        address.setApartment(addressDto.getApartment());
        address.setZipcode(addressDto.getZipcode());
        address.setUsers(Collections.emptySet());
        return address;
    }

    public static Example<Address> getAddressExample(Address address) {
        return Example.of(address, ExampleMatcher.matchingAll().withIgnorePaths("id", "users").withIncludeNullValues());
    }
}
