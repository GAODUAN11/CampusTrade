package com.campustrade.productservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> LIST_TYPE = new TypeReference<List<String>>() {
    };

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        List<String> safeList = attribute == null ? List.of() : attribute;
        try {
            return OBJECT_MAPPER.writeValueAsString(safeList);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to serialize string list.", ex);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(OBJECT_MAPPER.readValue(dbData, LIST_TYPE));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize string list.", ex);
        }
    }
}
