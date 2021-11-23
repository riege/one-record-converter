package com.riege.onerecord.converter;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.riege.cargoxml.schema.xfwb3.WaybillType;

public class XFWB3toOneRecordConverterTest {

    @Test
    public void test88811111111() throws JAXBException, JsonProcessingException {
        String filename = "888-11111111_XFWB.xml";

        InputStream is;
        WaybillType xfwb;
        XFWB3toOneRecordConverter converter;
        String awb;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        is = ClassLoader.getSystemResourceAsStream(filename);
        xfwb = new ConverterUtil().unmarshalXFWB3(is);
        awb = xfwb.getBusinessHeaderDocument().getID().getValue();
        converter = new XFWB3toOneRecordConverter(xfwb);

        Assertions.assertNotNull(converter.getValidationHints());
        Assertions.assertFalse(converter.getValidationHints().isEmpty());
        for (ValidationMessage msg : converter.getValidationHints()) {
            System.out.println(awb + " HINT: " + msg.getMessage());
        }

        Assertions.assertNotNull(converter.getValidationWarnings());
        Assertions.assertFalse(converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : converter.getValidationWarnings()) {
            System.out.println(awb + " WARNING: " + msg.getMessage());
        }

        Assertions.assertNotNull(converter.getValidationErrors());
        Assertions.assertTrue(converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : converter.getValidationErrors()) {
            System.out.println(awb + " ERROR: " + msg.getMessage());
        }

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(converter.getOneRecordResult());
        System.out.println(awb + " JSON=\n" + json);
        Assertions.assertTrue(json.contains("customsInfoNote\" : \"USCI1234567812345678X7\""));
        Assertions.assertTrue(json.contains("Piece#goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
    }

    @Test
    public void test88811111111noModeCode() throws JAXBException, JsonProcessingException {
        String filename = "888-11111111_XFWB_noModeCode.xml";

        InputStream is;
        WaybillType xfwb;
        XFWB3toOneRecordConverter converter;
        String awb;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        is = ClassLoader.getSystemResourceAsStream(filename);
        xfwb = new ConverterUtil().unmarshalXFWB3(is);
        awb = xfwb.getBusinessHeaderDocument().getID().getValue();
        converter = new XFWB3toOneRecordConverter(xfwb);

        Assertions.assertNotNull(converter.getValidationHints());
        Assertions.assertFalse(converter.getValidationHints().isEmpty());
        for (ValidationMessage msg : converter.getValidationHints()) {
            System.out.println(awb + " HINT: " + msg.getMessage());
        }

        Assertions.assertNotNull(converter.getValidationWarnings());
        Assertions.assertFalse(converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : converter.getValidationWarnings()) {
            System.out.println(awb + " WARNING: " + msg.getMessage());
        }

        Assertions.assertNotNull(converter.getValidationErrors());
        Assertions.assertTrue(converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : converter.getValidationErrors()) {
            System.out.println(awb + " ERROR: " + msg.getMessage());
        }

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(converter.getOneRecordResult());
        System.out.println(awb + " JSON=\n" + json);
        Assertions.assertTrue(json.contains("customsInfoNote\" : \"USCI1234567812345678X7\""));
        Assertions.assertTrue(json.contains("Piece#goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
    }

}
