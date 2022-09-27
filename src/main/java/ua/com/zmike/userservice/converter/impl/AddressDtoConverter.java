package ua.com.zmike.userservice.converter.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.model.Address;

@Component
public class AddressDtoConverter implements DtoConverter<AddressDto, Address> {

    public AddressDto convertToDto(Address address) {
        if (ObjectUtils.isEmpty(address)) {
            return null;
        }
        return AddressDto.builder()
                .id(address.getId())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .houseNumber(address.getHouseNumber())
                .apartment(address.getApartment())
                .zipcode(address.getZipcode())
                .build();
    }

    public Address convertFromDto(AddressDto addressDto) {
        if (ObjectUtils.isEmpty(addressDto)) {
            return null;
        }
        var address = new Address();
        address.setId(addressDto.getId());
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHouseNumber(addressDto.getHouseNumber());
        address.setApartment(addressDto.getApartment());
        address.setZipcode(addressDto.getZipcode());
        return address;
    }
}
