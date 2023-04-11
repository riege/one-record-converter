package com.riege.onerecord.converter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonTestHelper {

    public static String writePrettyJacksonJSON(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
//            @Override
//            public void serialize(OffsetDateTime value, JsonGenerator gen,
//                SerializerProvider serializers) throws IOException
//            {
//
//            }
//        });
//        mapper.registerModule(module);

        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

}
