package com.riege.onerecord.converter;

import java.io.InputStream;
import java.util.Collections;

import javax.xml.bind.JAXBException;

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

import com.riege.cargoxml.schema.xfzb3.HouseWaybillType;
import com.riege.onerecord.jsonutils.JacksonObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XFZB3toOneRecordConverterTest {

    @Test
    public void testSEL22222222() throws JAXBException, JsonProcessingException {
        Result result =
            fileProcessingTest("SEL22222222_XFZB.xml");
        assertNotNull(result.converter.getValidationWarnings());
        assertFalse(result.converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationWarnings()) {
            System.out.println(result.awb + " WARNING: " + msg.getMessage());
        }

        assertNotNull(result.converter.getValidationErrors());
        assertTrue(result.converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationErrors()) {
            System.out.println(result.awb + " ERROR: " + msg.getMessage());
        }

        for (ValidationMessage msg : result.converter.getValidationHints()) {
            System.out.println(result.awb + " HINT: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        assertEquals(Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.HOUSE.code(),
            JsonPath.read(result.json,
                "$.waybillType.id"));

        // check that all payload is also in the JSON:
        assertEquals("SEL22222222", JsonPath.read(result.json, "$.waybillNumber"));
        assertEquals("2022-04-29T00:00:00", JsonPath.read(result.json, "$.carrierDeclarationDate"));
        assertEquals("ARMIN AIRLINE", JsonPath.read(result.json, "$.carrierDeclarationSignature"));
        assertEquals("SIEGFRIED SIGNATURE", JsonPath.read(result.json, "$.consignorDeclarationSignature"));
        assertEquals("ICN", JsonPath.read(result.json, "$.departureLocation.locationCodes[0].code"));
        assertEquals("FDH", JsonPath.read(result.json, "$.arrivalLocation.locationCodes[0].code"));
        // FEHLT Assertions.assertTrue(result.json.contains("8112345678"));
        assertEquals(true, JsonPath.read(result.json, "$.shipment.pieces[0].nvdForCarriage"));
        assertEquals(true, JsonPath.read(result.json, "$.shipment.pieces[0].nvdForCustoms"));
        // Unclear where to put <IncludedHouseConsignment><TotalPrepaidChargeAmount>
        // Assertions.assertTrue(result.json.contains("420"));
        assertEquals(134.0,
            JsonPath.read(result.json, "$.shipment.pieces[0].grossWeight.numericalValue"));
        assertEquals("5432109876",
            JsonPath.read(result.json, "$.shipment.pieces[0].customsInformation[0].note"));
        assertEquals("SPARE PARTS",
            JsonPath.read(result.json, "$.shipment.pieces[0].goodsDescription"));

        // Involved parties: Shipper
        assertEquals(Vocabulary.s_c_ParticipantIdentifier + "_SHP",
            JsonPath.read(result.json, "$.shipment.involvedParties[0].partyRole.id"));
        assertEquals("SPARE PART LTD.",
            JsonPath.read(result.json, "$.shipment.involvedParties[0].partyDetails.name"));
        assertEquals(Collections.singletonList("42. GONGDAN SEONGSAN"),
            JsonPath.read(result.json,
                "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.streetAddressLines"));
        assertEquals("CHANGWON-SI. KYUN", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.cityName"));
        assertEquals("12345", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.postalCode.code"));
        assertEquals("KR", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.country.countryCode"));
        assertEquals("0553456789012", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.contactPersons[0].contactDetails[0].textualValue"));
        assertEquals("KIM QUAN KWUN", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.contactPersons[0].lastName"));

        // Involved parties: Consignee
        assertEquals(Vocabulary.s_c_ParticipantIdentifier + "_CNE",
            JsonPath.read(result.json, "$.shipment.involvedParties[1].partyRole.id"));
        assertEquals("REPAIR DIENST GMBH",
            JsonPath.read(result.json, "$.shipment.involvedParties[1].partyDetails.name"));
        assertEquals(Collections.singletonList("SPEZIAL-STR. 13"),
            JsonPath.read(result.json,
                "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.streetAddressLines"));
        assertEquals("87654", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.postalCode.code"));
        assertEquals("FRIEDRICHSHAFEN", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.cityName"));
        assertEquals("DE", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.country.countryCode"));

        // OCIs
        assertEquals("T", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].contentCode.code"));
        assertEquals("DE", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].countryCode"));
        assertEquals("CNE", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].subjectCode.code"));
        assertEquals("DE3056777000", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].note"));

        assertEquals(200,
            (Integer) JsonPath.read(result.json, "$.waybillLineItems[0].slacForRate"));
//        assertTrue(result.json.contains("PieceCount\" : 10"));

        // Some more
        assertEquals("1133557799", JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[0].ofProduct.hsCode.code"));
    }

    private Result fileProcessingTest(String filename) throws JAXBException, JsonProcessingException {
        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        HouseWaybillType xfzb = new ConverterUtil().unmarshallXFZB3(is);
        Result result = new Result();
        result.awb = xfzb.getBusinessHeaderDocument().getID().getValue();
        result.converter = new XFZB3toOneRecordConverter(xfzb);
        Object object = result.converter.getOneRecordResult();
        result.json =
            JacksonObjectMapper.buildMapperWithoutTimezone()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(object);
        assertNotNull(result.json);
        return result;
    }

}
