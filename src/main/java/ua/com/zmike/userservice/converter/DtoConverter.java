package ua.com.zmike.userservice.converter;

public interface DtoConverter<T, S> {

    T convertToDto(S s);

    S convertFromDto(T t);
}
