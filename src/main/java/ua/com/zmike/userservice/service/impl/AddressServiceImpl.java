package ua.com.zmike.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.repository.AddressRepository;
import ua.com.zmike.userservice.service.AddressService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final DtoConverter<AddressDto, Address> converter;
    private final AddressRepository addressRepository;

    @Override
    public Optional<Address> findByExample(AddressDto addressDto) {
        var address = converter.convertFromDto(addressDto);
        var example = Example.of(
                address, ExampleMatcher.matchingAll().withIgnorePaths("id", "users").withIncludeNullValues());
        return addressRepository.findOne(example);

    }
}
