package com.riege.onerecord.converter;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.iata.cargo.codelists.WaybillTypeCode;
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
        Result result =
            fileProcessingTest("888-11111111_XFWB.xml");
        Assertions.assertNotNull(result.converter.getValidationWarnings());
        Assertions.assertTrue(result.converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationWarnings()) {
            System.out.println(result.awb + " WARNING: " + msg.getMessage());
        }

        Assertions.assertNotNull(result.converter.getValidationErrors());
        Assertions.assertTrue(result.converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationErrors()) {
            System.out.println(result.awb + " ERROR: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        Assertions.assertTrue(result.json.contains("customsInformation\" : \"USCI1234567812345678X7\""));
        Assertions.assertTrue(result.json.contains("Piece#goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
        Assertions.assertEquals(WaybillTypeCode.MASTER.code(), result.converter.getOneRecordResult().getWaybillType());
    }

    @Test
    public void test88811111111noModeCode() throws JAXBException, JsonProcessingException {
        Result result =
            fileProcessingTest("888-11111111_XFWB_noModeCode.xml");

        Assertions.assertNotNull(result.converter.getValidationWarnings());
        Assertions.assertTrue(result.converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationWarnings()) {
            System.out.println(result.awb + " WARNING: " + msg.getMessage());
        }

        Assertions.assertNotNull(result.converter.getValidationErrors());
        Assertions.assertTrue(result.converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationErrors()) {
            System.out.println(result.awb + " ERROR: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        Assertions.assertTrue(result.json.contains("customsInformation\" : \"USCI1234567812345678X7\""));
        Assertions.assertTrue(result.json.contains("Piece#goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
        Assertions.assertEquals(WaybillTypeCode.DIRECT.code(), result.converter.getOneRecordResult().getWaybillType());
    }

    private Result fileProcessingTest(String filename) throws JAXBException, JsonProcessingException {
        Result result = new Result();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        WaybillType xfwb = new ConverterUtil().unmarshalXFWB3(is);
        result.awb = xfwb.getBusinessHeaderDocument().getID().getValue();
        result.converter = new XFWB3toOneRecordConverter(xfwb);
        result.json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.converter.getOneRecordResult());
        Assertions.assertNotNull(result.json);
        return result;
    }

    class Result {
        XFWB3toOneRecordConverter converter;
        String awb;
        String json;
    }

}
