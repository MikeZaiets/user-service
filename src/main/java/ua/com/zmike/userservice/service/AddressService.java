package ua.com.zmike.userservice.service;

import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.model.Address;

import java.util.Optional;

public interface AddressService {

    Optional<Address> findByExample(AddressDto addressDto);
}
