package com.riege.onerecord.converter;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;

class ONERecordBeanSerializerModifier extends BeanSerializerModifier {

    /*
     * Inspired by https://www.baeldung.com/jackson-json-view-annotation
     */
    private static NameTransformer propertyTypesTransformer = new NameTransformer() {

        @Override
        public String transform(String name) {
            return name.equals("types") ? "@type" : name;
        }

        @Override
        public String reverse(String transformed) {
            return transformed.equals("@type") ? "types" : transformed;
        }
    };

    @Override
    public List<BeanPropertyWriter> changeProperties(
        SerializationConfig config, BeanDescription beanDesc,
        List<BeanPropertyWriter> beanProperties)
    {
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            if (writer.getName().equals("types")) {
                beanProperties.set(i, writer.rename(propertyTypesTransformer));
            }
        }
        return beanProperties;
    }

}
