package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";


    @Autowired
    public UserClient(@Value("${shareit-server.url:default}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }



//    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", state.name(),
//                "from", from,
//                "size", size
//        );
//        return get("?state={state}&from={from}&size={size}", userId, parameters);
//    }
//
//
    public ResponseEntity<Object> addUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> patchUser(Long id, UserDto userDto) {
        return patch("/" + id, id, userDto);
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        return delete("/" + id);
    }

    public ResponseEntity<Object> getUser(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }
//
//    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
//        return get("/" + bookingId, userId);
//    }
}
