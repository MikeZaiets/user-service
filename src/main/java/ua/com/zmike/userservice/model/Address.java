package ua.com.zmike.userservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(name = "apt_number")
    private Integer apartment;

    @Column(name = "zipcode", nullable = false)
    private Integer zipcode;

    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        if (!Objects.equals(country, address.country)) return false;
        if (!Objects.equals(city, address.city)) return false;
        if (!Objects.equals(street, address.street)) return false;
        if (!Objects.equals(houseNumber, address.houseNumber)) return false;
        if (!Objects.equals(apartment, address.apartment)) return false;
        return Objects.equals(zipcode, address.zipcode);
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (houseNumber != null ? houseNumber.hashCode() : 0);
        result = 31 * result + (apartment != null ? apartment.hashCode() : 0);
        result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("country='" + country + "'")
                .add("city='" + city + "'")
                .add("street='" + street + "'")
                .add("houseNumber='" + houseNumber + "'")
                .add("apartment=" + apartment)
                .add("zipcode=" + zipcode)
                .add("users=" + users)
                .toString();
    }
}
