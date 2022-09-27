package ua.com.zmike.userservice.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.com.zmike.userservice.converter.DtoConverter;
import ua.com.zmike.userservice.dto.AddressDto;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.service.AddressService;
import ua.com.zmike.userservice.testUtil.TestDbManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddress;
import static ua.com.zmike.userservice.testUtil.TestEntityFactory.getAddressDto;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddressServiceTestIT {

    @Autowired
    private DtoConverter<AddressDto, Address> addressDtoConverter;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TestDbManager dbManager;

    @BeforeAll
    void beforeAll() {
        dbManager.initDataBase();
    }

    @BeforeEach
    void setUp() {
        dbManager.fillTables();
    }

    @AfterEach
    void tearDown() {
        dbManager.cleanTables();
    }

    @Test
    void findByExample_shouldReturnOne_whenAddressExistInDb() {
        // given
        var addressDto = getAddressDto();
        var expectedResult = Optional.of(getAddress());

        // test
        var result = addressService.findByExample(addressDto);

        // verify
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result, expectedResult);
        assertNotNull(result.get().getId());
    }

    @Test
    void findByExample_shouldReturnEmptyObject_whenAddressNotExistInDb() {
        // given
        var addressDto = getAddressDto();
        addressDto.setCountry("SomeCountry");

        // test
        var result = addressService.findByExample(addressDto);

        // verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
