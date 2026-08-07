package com.saranaresturantsystem.config.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Jackson-friendly wrapper around {@link PageImpl} that can be properly
 * serialized/deserialized by Redis cache.
 * <p>
 * Spring's {@code PageImpl} doesn't have a default or JSON-friendly constructor,
 * so Jackson cannot reconstruct it from cached JSON — it falls back to {@code LinkedHashMap}.
 * This class provides the necessary {@link JsonCreator} constructor.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("numberOfElements") int numberOfElements,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("first") boolean first,
            @JsonProperty("last") boolean last,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("empty") boolean empty
    ) {
        super(content != null ? content : new ArrayList<>(),
                PageRequest.of(number, size > 0 ? size : 1),
                totalElements);
    }

    public RestPage(org.springframework.data.domain.Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
