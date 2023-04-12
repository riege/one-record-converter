package com.riege.onerecord.converter;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;

public class JacksonTestHelper {

    private static class MyOffsetTimeSerializer extends OffsetDateTimeSerializer {
        MyOffsetTimeSerializer() {
            super(OffsetDateTimeSerializer.INSTANCE, null,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public static String writePrettyJacksonJSON(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // mapper.disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
        // Note: setDateFormat does not apply on OffsetDateTime and other JDK 8 date/time
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        // Note: We map "types" to "@type" here:
        SerializerFactory serializerFactory = BeanSerializerFactory.instance
            .withSerializerModifier(new ONERecordBeanSerializerModifier());
        mapper.setSerializerFactory(serializerFactory);

        // Since JOPA 0.18.0 (https://github.com/kbss-cvut/jopa/releases/tag/v0.18.0)
        // The generator has
        // Implemented full support for Java 8 date/time API. It is now the preferred way of representing temporal data (#95).
        //
        // Note: Hack to write OffsetDateTime without timezone:
        mapper.registerModule(new JavaTimeModule());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(OffsetDateTime.class, new MyOffsetTimeSerializer());
        mapper.registerModule(simpleModule);


        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

}
