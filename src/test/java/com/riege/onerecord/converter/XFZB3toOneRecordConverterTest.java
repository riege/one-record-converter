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
import com.riege.cargoxml.schema.xfzb3.HouseWaybillType;

public class XFZB3toOneRecordConverterTest {

    @Test
    public void testSEL22222222() throws JAXBException, JsonProcessingException {
        XFZB3toOneRecordConverterTest.Result result =
            fileProcessingTest("SEL22222222_XFZB.xml");
        Assertions.assertNotNull(result.converter.getValidationWarnings());
        Assertions.assertFalse(result.converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationWarnings()) {
            System.out.println(result.awb + " WARNING: " + msg.getMessage());
        }

        Assertions.assertNotNull(result.converter.getValidationErrors());
        Assertions.assertTrue(result.converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationErrors()) {
            System.out.println(result.awb + " ERROR: " + msg.getMessage());
        }

        for (ValidationMessage msg : result.converter.getValidationHints()) {
            System.out.println(result.awb + " HINT: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        Assertions.assertEquals(
            WaybillTypeCode.HOUSE.code(), result.converter.getOneRecordResult().getWaybillType());

        // check that all payload is also in the JSON:
        Assertions.assertTrue(result.json.contains("waybillType\" : \"House\""));
        Assertions.assertTrue(result.json.contains("waybillNumber\" : \"SEL22222222\""));
        Assertions.assertTrue(result.json.contains("2022-04-29T00:00:00"));
        Assertions.assertTrue(result.json.contains("ARMIN AIRLINE"));
        Assertions.assertTrue(result.json.contains("SIEGFRIED SIGNATURE"));
        Assertions.assertTrue(result.json.contains("Location#code\" : \"ICN\""));
        Assertions.assertTrue(result.json.contains("Location#code\" : \"FDH\""));
        Assertions.assertFalse(result.json.contains("Location#code\" : \"FRA\""));
        // FEHLT Assertions.assertTrue(result.json.contains("8112345678"));
        Assertions.assertTrue(result.json.contains("nvdForCarriage\" : true"));
        Assertions.assertTrue(result.json.contains("nvdForCustoms\" : true"));
        Assertions.assertTrue(result.json.contains("nvdIndicator\" : true"));
        // Unclear where to put <IncludedHouseConsignment><TotalPrepaidChargeAmount>
        // Assertions.assertTrue(result.json.contains("420"));
        Assertions.assertTrue(result.json.contains("134.0"));

        Assertions.assertTrue(result.json.contains("customsInformation\" : \"5432109876\""));
        Assertions.assertTrue(result.json.contains("Piece#goodsDescription\" : \"SPARE PARTS\""));

        Assertions.assertTrue(result.json.contains("SPARE PART LTD"));
        Assertions.assertTrue(result.json.contains("42. GONGDAN SEONGSAN"));
        Assertions.assertTrue(result.json.contains("CHANGWON-SI. KYUN"));
        Assertions.assertTrue(result.json.contains("12345"));
        Assertions.assertTrue(result.json.contains("\"KR\""));
        Assertions.assertTrue(result.json.contains("0553456789012"));
        Assertions.assertFalse(result.json.contains("055281234567890"));
        Assertions.assertTrue(result.json.contains("KIM QUAN KWUN"));

        Assertions.assertTrue(result.json.contains("REPAIR DIENST GMBH"));
        Assertions.assertTrue(result.json.contains("SPEZIAL-STR. 13"));
        Assertions.assertTrue(result.json.contains("87654"));
        Assertions.assertTrue(result.json.contains("FRIEDRICHSHAFEN"));
        Assertions.assertTrue(result.json.contains("DE"));

        // OCIs
        Assertions.assertTrue(result.json.contains("customsInfoContentCode\" : \"T\""));
        Assertions.assertTrue(result.json.contains("customsInfoCountryCode\" : \"DE\""));
        Assertions.assertTrue(result.json.contains("customsInfoSubjectCode\" : \"CNE\""));
        Assertions.assertTrue(result.json.contains("customsInformation\" : \"DE3056777000\""));

        Assertions.assertTrue(result.json.contains("SLAC\" : 200"));
        Assertions.assertTrue(result.json.contains("PieceCount\" : 10"));

        // Some more
        Assertions.assertTrue(result.json.contains("#hsCode\" : \"1133557799\""));
    }

    private XFZB3toOneRecordConverterTest.Result fileProcessingTest(String filename) throws JAXBException, JsonProcessingException {
        XFZB3toOneRecordConverterTest.Result result = new Result();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        HouseWaybillType xfzb = new ConverterUtil().unmarshallXFZB3(is);
        result.awb = xfzb.getBusinessHeaderDocument().getID().getValue();
        result.converter = new XFZB3toOneRecordConverter(xfzb);
        result.json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.converter.getOneRecordResult());
        Assertions.assertNotNull(result.json);
        return result;
    }

    class Result {
        XFZB3toOneRecordConverter converter;
        String awb;
        String json;
    }

}
