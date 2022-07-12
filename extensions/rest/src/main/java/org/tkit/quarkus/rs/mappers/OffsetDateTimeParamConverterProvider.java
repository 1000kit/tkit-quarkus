package org.tkit.quarkus.rs.mappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.tkit.quarkus.log.cdi.LogService;

@Provider
@Singleton
@LogService(log = false)
public class OffsetDateTimeParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.isAssignableFrom(OffsetDateTime.class)) {
            return (ParamConverter<T>) new OffsetDateTimeParamConverter();
        }
        return null;
    }

    public static class OffsetDateTimeParamConverter implements ParamConverter<OffsetDateTime> {

        @Override
        public OffsetDateTime fromString(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return OffsetDateTime.parse(value);
        }

        @Override
        public String toString(OffsetDateTime value) {
            if (value == null) {
                return null;
            }
            return value.toString();
        }
    }
}
