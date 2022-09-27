package ua.com.zmike.userservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.zmike.userservice.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
