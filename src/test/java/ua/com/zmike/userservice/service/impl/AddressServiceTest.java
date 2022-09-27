package ua.com.zmike.userservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.repository.AddressRepository;
import ua.com.zmike.userservice.service.AddressService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddress;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressExample;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private DtoConverter<AddressDto, Address> addressDtoConverter;
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private AddressServiceImpl addressService;


    @Test
    void findByExample_shouldReturnOne_whenAddressExistInDb() {
        // given
        var addressDto = getAddressDto();
        var address = getAddress();
        var example = getAddressExample(address);
        var expectedResult = Optional.of(address);

        when(addressDtoConverter
                .convertFromDto(addressDto))
                .thenReturn(address);
        when(addressRepository
                .findOne(example))
                .thenReturn(expectedResult);

        // test
        var result = addressService.findByExample(addressDto);

        // verify
        assertThat(result).isEqualTo(expectedResult);
        verify(addressDtoConverter, only()).convertFromDto(addressDto);
        verify(addressRepository, only()).findOne(example);
    }

    @Test
    void findByExample_shouldReturnEmptyObject_whenAddressNotExistInDb() {
        // given
        var addressDto = getAddressDto();
        var address = getAddress();
        var example = getAddressExample(address);

        when(addressDtoConverter
                .convertFromDto(addressDto))
                .thenReturn(address);
        when(addressRepository
                .findOne(example))
                .thenReturn(Optional.empty());

        // test
        var result = addressService.findByExample(addressDto);

        // verify
        assertThat(result).isEmpty();
        verify(addressDtoConverter, only()).convertFromDto(addressDto);
        verify(addressRepository, only()).findOne(example);
    }
}
