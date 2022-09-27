package ua.com.zmike.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.zmike.userservice.model.Address;
import ua.com.zmike.userservice.model.User;
import ua.com.zmike.userservice.repository.UserRepository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RunWithAppService {

    private static final String INIT_DB_SCRIPT = "init-db.sql";

    private final UserRepository userRepository;

    @Bean
    public ApplicationRunner run(DataSource dataSource, RunWithAppService service) {
        return args -> {
            executeSqlScript(dataSource.getConnection(), new DefaultResourceLoader().getResource(INIT_DB_SCRIPT));
            service.addUsers(20);
        };
    }

    @Transactional
    public void addUsers(int i) {
        IntStream.range(0, i).forEach(j -> {
            User user = new User();
            user.setEmail(String.format("User%d@gmail.com", j));
            user.setFirstName("User" + j);
            user.setLastName("User" + j);
            user.setPhoneNumber(getRandomPhoneNumber());
            user.setBirthDate(getRandomBirthDate());
            user.setAddress(getAddress(j+1));
            userRepository.save(user);
        });
    }

    private Address getAddress(int i) {
        var address = new Address();
        address.setCountry("Country" + i);
        address.setCity("City" + i);
        address.setStreet("Street" + i);
        address.setHouseNumber(String.valueOf(i));
        address.setApartment(i);
        address.setZipcode(i);
        return address;
    }

    private LocalDate getRandomBirthDate() {
        var minAge = 19;
        var maxAge = 25;
        return LocalDate.now().minusYears(new SplittableRandom().nextInt(minAge, maxAge));
    }

    private String getRandomPhoneNumber() {
        return new SplittableRandom().ints(10, 0, 9)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
