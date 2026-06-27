package vn.ezisolutions.cloud.hackathon.core.jpa;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonConverters {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private JsonConverters() {
    }

    @Converter
    public static class MapStringStringConverter implements AttributeConverter<Map<String, String>, String> {
        @Override
        public String convertToDatabaseColumn(Map<String, String> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot serialize string map", exception);
            }
        }

        @Override
        public Map<String, String> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return new LinkedHashMap<>();
            }
            try {
                return MAPPER.readValue(dbData, new TypeReference<>() {
                });
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot deserialize string map", exception);
            }
        }
    }

    @Converter
    public static class ObjectMapConverter implements AttributeConverter<Map<String, Object>, String> {
        @Override
        public String convertToDatabaseColumn(Map<String, Object> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot serialize object map", exception);
            }
        }

        @Override
        public Map<String, Object> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return new LinkedHashMap<>();
            }
            try {
                return MAPPER.readValue(dbData, new TypeReference<>() {
                });
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot deserialize object map", exception);
            }
        }
    }

    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot serialize string list", exception);
            }
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return List.of();
            }
            try {
                return MAPPER.readValue(dbData, new TypeReference<>() {
                });
            } catch (Exception exception) {
                throw new IllegalArgumentException("Cannot deserialize string list", exception);
            }
        }
    }
}
