package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.Map;

@Generated
@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createRequest(long userId, CreateItemRequestDto createItemRequestDto) {
        return post("", userId, createItemRequestDto);
    }

    public ResponseEntity<Object> findAllRequest(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findRequestById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAllRequestWithPagination(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
