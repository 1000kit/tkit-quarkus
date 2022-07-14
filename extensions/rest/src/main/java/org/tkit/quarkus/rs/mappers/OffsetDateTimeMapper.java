package org.tkit.quarkus.rs.mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OffsetDateTimeMapper {

    public OffsetDateTime map(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return OffsetDateTime.of(dateTime, ZoneOffset.systemDefault().getRules().getOffset(dateTime));
    }

    public LocalDateTime map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return LocalDateTime.ofInstant(offsetDateTime.toInstant(), ZoneOffset.systemDefault());
    }

}
