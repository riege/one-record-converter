package com.riege.onerecord.converter;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.riege.cargoxml.schema.xfwb3.WaybillType;
import com.riege.onerecord.jsonutils.JacksonObjectMapper;

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

        for (ValidationMessage msg : result.converter.getValidationHints()) {
            System.out.println(result.awb + " HINT: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        Assertions.assertTrue(result.json.contains("customsInformation\" : \"USCI1234567812345678X7\""));
        Assertions.assertTrue(result.json.contains("goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
        Assertions.assertEquals(WaybillTypeCode.MASTER.code(), result.converter.getOneRecordResult().getWaybillType());

        // check that all payload is also in the JSON:
        Assertions.assertTrue(result.json.contains("waybillPrefix\" : \"888\""));
        Assertions.assertTrue(result.json.contains("waybillNumber\" : \"11111111\""));
        Assertions.assertTrue(result.json.contains("FRA8117142550"));
        Assertions.assertTrue(result.json.contains("2021-03-10T00:00:00"));
        Assertions.assertTrue(result.json.contains("MARK USER"));
        Assertions.assertTrue(result.json.contains("ORK"));
        // FEHLT Assertions.assertTrue(result.json.contains("8112345678"));
        Assertions.assertTrue(result.json.contains("nvdForCarriage\" : true"));
        Assertions.assertTrue(result.json.contains("nvdForCustoms\" : true"));
        Assertions.assertTrue(result.json.contains("nvdIndicator\" : true"));
        Assertions.assertTrue(result.json.contains("1042.0"));
        Assertions.assertTrue(result.json.contains("2.602"));

        Assertions.assertTrue(result.json.contains("FORWARDER COMPANY IRELAND LTD"));
        Assertions.assertTrue(result.json.contains("SHP_ACCOUNT_NO_1234"));
        Assertions.assertTrue(result.json.contains("ABCD"));
        Assertions.assertTrue(result.json.contains("HARBOUR POINT BUSINESS PARK"));
        Assertions.assertTrue(result.json.contains("LITTLE CITY"));
        Assertions.assertTrue(result.json.contains("IE"));
        Assertions.assertTrue(result.json.contains("HILDA HILARIOUS"));
        Assertions.assertTrue(result.json.contains("353123456789"));

        Assertions.assertTrue(result.json.contains("FORWARDER COMPANY SHANGHAI LTD"));
        Assertions.assertTrue(result.json.contains("ROOM 123 SPECIAL BUILDING. NR 987"));
        Assertions.assertTrue(result.json.contains("SUHANG RD. PUDONG AIRPORT"));
        Assertions.assertTrue(result.json.contains("AIRPORT ROAD"));
        Assertions.assertTrue(result.json.contains("SHANGHAI"));
        Assertions.assertTrue(result.json.contains("CN"));
        Assertions.assertTrue(result.json.contains("People's Republic of China"));
        Assertions.assertTrue(result.json.contains("862123454321"));
        // Note: 862123454321 "overwrites" 861234567890 even though both are in the XFWB
        Assertions.assertFalse(result.json.contains("861234567890"));

        Assertions.assertTrue(result.json.contains("PAUL PERSON"));
        Assertions.assertTrue(result.json.contains("862123454321"));

        Assertions.assertTrue(result.json.contains("FORWARDER COMPANY IRELAND LIMITED"));
        Assertions.assertTrue(result.json.contains("87654321"));
        Assertions.assertTrue(result.json.contains("3456789"));
        Assertions.assertTrue(result.json.contains("ABCD"));
        Assertions.assertTrue(result.json.contains("Harbour Point Business Part"));
        Assertions.assertTrue(result.json.contains("Little City"));
        Assertions.assertTrue(result.json.contains("0123"));
        Assertions.assertTrue(result.json.contains("353987654321"));
        Assertions.assertTrue(result.json.contains("ORK"));
        Assertions.assertTrue(result.json.contains("PVG"));

        Assertions.assertTrue(result.json.contains("XX8012"));
        // The getDepartureEvent().getScheduledOccurrenceDateTime()
        // map to 1R MovementTimes "SD" in Ontology v1.2
        Assertions.assertTrue(result.json.contains("movementMilestone\" : \"SD\""));
        Assertions.assertTrue(result.json.contains("movementTimestamp\" : \"2021-03-10T00:00:00\""));

        Assertions.assertTrue(result.json.contains("XX345"));
        Assertions.assertTrue(result.json.contains("movementTimestamp\" : \"2021-03-12T00:00:00\""));
        Assertions.assertTrue(result.json.contains("\"XX\""));
        Assertions.assertTrue(result.json.contains("DUB"));
        Assertions.assertTrue(result.json.contains("2021-03-10T00:00:00"));
        Assertions.assertTrue(result.json.contains("AUH"));

        Assertions.assertTrue(result.json.contains("NSC"));
        Assertions.assertTrue(result.json.contains("4 PALLETS"));
        Assertions.assertTrue(result.json.contains("TEMPERATURE CONTROL 15 TO 25 DEGREES CELSIUS"));
        Assertions.assertTrue(result.json.contains("HANDLE WITH CARE"));

        // OCIs
        Assertions.assertTrue(result.json.contains("customsInfoContentCode\" : \"T\""));
        Assertions.assertTrue(result.json.contains("customsInfoCountryCode\" : \"IE\""));
        Assertions.assertTrue(result.json.contains("customsInfoSubjectCode\" : \"SHP\""));
        Assertions.assertTrue(result.json.contains("customsInformation\" : \"IE1234567N\""));

        Assertions.assertTrue(result.json.contains("USCI1234567812345678X7"));

        // Security
        Assertions.assertTrue(result.json.contains("01234-01"));
        Assertions.assertTrue(result.json.contains("RA"));
        Assertions.assertTrue(result.json.contains("ED"));
        // Note: 1299 is converted into expiryDate
        Assertions.assertFalse(result.json.contains("1299"));
        Assertions.assertTrue(result.json.contains("expiryDate\" : \"2099-12-01T00:00:00\""));
        Assertions.assertTrue(result.json.contains("MR. SECURITY OFFICER"));
        // Note: 13APR211147 is converted into issuedOn" : "2021-04-13T11:47:00
        Assertions.assertFalse(result.json.contains("13APR211147"));
        Assertions.assertTrue(result.json.contains("issuedOn\" : \"2021-04-13T11:47:00\""));

        Assertions.assertTrue(result.json.contains("customsOriginCode\" : \"X\","));

        Assertions.assertTrue(result.json.contains("originCurrency\" : \"EUR\""));

        Assertions.assertTrue(result.json.contains("chargePaymentType\" : \"P\""));
        Assertions.assertTrue(result.json.contains("entitlement\" : \"C\""));
        Assertions.assertTrue(result.json.contains("otherChargeCode\" : \"MC\""));
        Assertions.assertTrue(result.json.contains("subTotal\" : 1234.56"));

        Assertions.assertTrue(result.json.contains("000111"));
        Assertions.assertTrue(result.json.contains("SLAC\" : 333"));
        Assertions.assertTrue(result.json.contains("PieceCount\" : 3"));

        Assertions.assertTrue(result.json.contains("\"CONSOLIDATION"));
        Assertions.assertTrue(result.json.contains("AS PER ATTACHED"));
        Assertions.assertTrue(result.json.contains("MANIFEST"));
        Assertions.assertTrue(result.json.contains("SECURE CARGO"));
        Assertions.assertTrue(result.json.contains("NOT RESTRICTED"));
        Assertions.assertTrue(result.json.contains("AIRLINE PHARMA"));
        Assertions.assertTrue(result.json.contains("SERVICE\""));

        // Dimensions
        Assertions.assertTrue(result.json.contains(" : 80"));
        Assertions.assertTrue(result.json.contains(" : 120"));
        Assertions.assertTrue(result.json.contains(" : 96"));
        Assertions.assertTrue(result.json.contains(" : 79"));

        // Some more
        Assertions.assertTrue(result.json.contains("4626.48"));
        Assertions.assertTrue(result.json.contains("11.22"));
        Assertions.assertTrue(result.json.contains("222.33"));
        Assertions.assertTrue(result.json.contains("4860.03"));
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
        Assertions.assertTrue(result.json.contains("goodsDescription\" : \"CONSOLIDATION\\nAS PER ATTACHED\\nMANIFEST\\nSECURE CARGO\\nNOT RESTRICTED\\nAIRLINE PHARMA\\nSERVICE\""));
        Assertions.assertEquals(WaybillTypeCode.DIRECT.code(), result.converter.getOneRecordResult().getWaybillType());
    }

    private Result fileProcessingTest(String filename) throws JAXBException, JsonProcessingException {
        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        WaybillType xfwb = new ConverterUtil().unmarshallXFWB3(is);
        Result result = new Result();
        result.awb = xfwb.getBusinessHeaderDocument().getID().getValue();
        result.converter = new XFWB3toOneRecordConverter(xfwb);
        result.json =
            JacksonObjectMapper.buildMapperWithoutTimezone()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result.converter.getOneRecordResult());
        Assertions.assertNotNull(result.json);
        return result;
    }

    class Result {
        XFWB3toOneRecordConverter converter;
        String awb;
        String json;
    }

}
