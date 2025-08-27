package com.riege.onerecord.converter;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.iata.onerecord.cargo.model.Item;
import org.iata.onerecord.cargo.model.Piece;
import org.iata.onerecord.cargo.model.Waybill;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;

import com.riege.cargoxml.schema.xfwb3.QuantityType;
import com.riege.cargoxml.schema.xfwb3.WaybillType;
import com.riege.onerecord.jsonutils.JacksonObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static com.riege.onerecord.converter.XFWB3ParserHelper.*;

public class XFWB3toOneRecordConverterTest {

    @Test
    public void test88811111111() throws JAXBException, JsonProcessingException {
        Result result =
            fileProcessingTest("888-11111111_XFWB.xml");
        assertNotNull(result.converter.getValidationWarnings());
        assertTrue(result.converter.getValidationWarnings().isEmpty());
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

        assertEquals(
            Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.MASTER.code(),
            JsonPath.read(result.json,
                "$.waybillType.id"));

        // check that all payload is also in the JSON:
        assertEquals("888", JsonPath.read(result.json, "$.waybillPrefix"));
        assertEquals("11111111", JsonPath.read(result.json, "$.waybillNumber"));
        assertEquals("FRA8117142550", JsonPath.read(result.json, "$.shippingRefNo"));
        assertEquals("2021-03-10T00:00:00", JsonPath.read(result.json, "$.carrierDeclarationDate"));
        assertEquals("MARK USER", JsonPath.read(result.json, "$.carrierDeclarationSignature"));
        assertEquals("ORK",
            JsonPath.read(result.json, "$.carrierDeclarationPlace.locationCodes[0].code"));
        // FEHLT Assertions.assertTrue(result.json.contains("8112345678"));
        assertEquals(true, JsonPath.read(result.json, "$.shipment.pieces[0].nvdForCarriage"));
        assertEquals(true, JsonPath.read(result.json, "$.shipment.pieces[0].nvdForCustoms"));
        // If no data, it is considered no value declared -> Assertions.assertTrue(result.json.contains("nvdIndicator\" : true"));
        assertEquals(1042.0,
            JsonPath.read(result.json, "$.shipment.pieces[0].grossWeight.numericalValue"));
        assertEquals(2.602,
            JsonPath.read(result.json, "$.shipment.pieces[0].dimensions.volume.numericalValue"));

        // Involved parties: Shipper
        assertEquals(Vocabulary.s_c_ParticipantIdentifier + "_SHP",
            JsonPath.read(result.json, "$.shipment.involvedParties[0].partyRole.id"));
        assertEquals("FORWARDER COMPANY IRELAND LTD",
            JsonPath.read(result.json, "$.shipment.involvedParties[0].partyDetails.name"));
        assertEquals("SHP_ACCOUNT_NO_1234", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].otherIdentifiers[0].textualValue"));
        assertEquals("ABCD", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.postalCode.code"));
        assertEquals(Collections.singletonList("HARBOUR POINT BUSINESS PARK"),
            JsonPath.read(result.json,
                "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.streetAddressLines"));
        assertEquals("LITTLE CITY", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.cityName"));
        assertEquals("IE", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.basedAtLocation.address.country.countryCode"));
        assertEquals("HILDA HILARIOUS", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.contactPersons[0].lastName"));
        assertEquals("353123456789", JsonPath.read(result.json,
            "$.shipment.involvedParties[0].partyDetails.contactPersons[0].contactDetails[0].textualValue"));

        // Involved parties: Consignee
        assertEquals(Vocabulary.s_c_ParticipantIdentifier + "_CNE",
            JsonPath.read(result.json, "$.shipment.involvedParties[1].partyRole.id"));
        assertEquals("FORWARDER COMPANY SHANGHAI LTD",
            JsonPath.read(result.json, "$.shipment.involvedParties[1].partyDetails.name"));
        assertEquals(Arrays.asList("SUHANG RD. PUDONG AIRPORT", "AIRPORT ROAD"),
            JsonPath.read(result.json,
                "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.streetAddressLines"));
        assertEquals("SHANGHAI", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.cityName"));
        assertEquals("CN", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.country.countryCode"));
        assertEquals("People's Republic of China", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.basedAtLocation.address.country.countryName"));
        // Note: 862123454321 "overwrites" 861234567890 even though both are in the XFWB
        assertFalse(result.json.contains("861234567890"));
        assertEquals("PAUL PERSON", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.contactPersons[0].lastName"));
        assertEquals("862123454321", JsonPath.read(result.json,
            "$.shipment.involvedParties[1].partyDetails.contactPersons[0].contactDetails[0].textualValue"));

        // Involved parties: Forwarder
        assertEquals(Vocabulary.s_c_ParticipantIdentifier + "_FFW",
            JsonPath.read(result.json, "$.shipment.involvedParties[2].partyRole.id"));
        assertEquals("FORWARDER COMPANY IRELAND LIMITED",
            JsonPath.read(result.json, "$.shipment.involvedParties[2].partyDetails.name"));
        assertEquals("87654321", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].otherIdentifiers[0].textualValue"));
        assertEquals("3456789", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].partyDetails.iataCargoAgentCode"));
        assertEquals("0123", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].partyDetails.iataCargoAgentLocationIdentifier"));
        assertEquals("ABCD", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].partyDetails.basedAtLocation.address.postalCode.code"));
        assertEquals(Collections.singletonList("Harbour Point Business Part"),
            JsonPath.read(result.json,
                "$.shipment.involvedParties[2].partyDetails.basedAtLocation.address.streetAddressLines"));
        assertEquals("Little City", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].partyDetails.basedAtLocation.address.cityName"));
        assertEquals("353987654321", JsonPath.read(result.json,
            "$.shipment.involvedParties[2].partyDetails.contactPersons[0].contactDetails[0].textualValue"));

        // DEP/ARR
        assertEquals("ORK", JsonPath.read(result.json, "$.departureLocation.locationCodes[0].code"));
        assertEquals("PVG", JsonPath.read(result.json, "$.arrivalLocation.locationCodes[0].code"));

        // this is supposed to be set on TransportMovement#operatingParties -> Party.partyDetails -> Carrier#airlineCode
        // this seems to be missing in 3.0.0 but got added in 3.1.1 -> Assertions.assertTrue(result.json.contains("\"XX\""));
        // Flights -> are now in referredBookingOption.activitySequences
        assertEquals("ORK", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[0].activity.departureLocation.locationCodes[0].code"));
        assertEquals("DUB", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[0].activity.arrivalLocation.locationCodes[0].code"));
        assertEquals("XX8012", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[0].activity.transportIdentifier"));
        assertEquals("SD", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[0].activity.movementTimes[0].movementMilestone.code"));
        assertEquals("2021-03-10T00:00:00", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[0].activity.movementTimes[0].movementTimestamp"));
        assertEquals("DUB", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[1].activity.departureLocation.locationCodes[0].code"));
        assertEquals("AUH", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[1].activity.arrivalLocation.locationCodes[0].code"));
        assertEquals("SD", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[1].activity.movementTimes[0].movementMilestone.code"));
        assertEquals("XX345", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[1].activity.transportIdentifier"));
        assertEquals("2021-03-12T00:00:00", JsonPath.read(result.json,
            "$.referredBookingOption.activitySequences[1].activity.movementTimes[0].movementTimestamp"));

        assertEquals(
            Arrays.asList("4 PALLETS", "TEMPERATURE CONTROL 15 TO 25 DEGREES CELSIUS",
                "HANDLE WITH CARE"),
            JsonPath.read(result.json, "$.shipment.pieces[0].textualHandlingInstructions"));

        // OCIs
        assertEquals("T", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[0].contentCode.code"));
        assertTrue(result.json.contains("countryCode\" : \"IE\""));
        assertEquals("SHP", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[0].subjectCode.code"));
        assertEquals("IE1234567N", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[0].note"));
        assertEquals("USCI1234567812345678X7", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].note"));

        // Security
        assertEquals("01234-01", JsonPath.read(result.json,
            "$.shipment.pieces[0].securityDeclaration.regulatedEntityIssuer.owningOrganization.otherIdentifiers[0].textualValue"));
        assertEquals(Vocabulary.s_c_RegulatedEntityCategoryCode + "_RA", JsonPath.read(result.json,
            "$.shipment.pieces[0].securityDeclaration.regulatedEntityIssuer.regulatedEntityCategory.id"));
        // Note: 1299 is converted into expiryDate
        assertFalse(result.json.contains("1299"));
        assertEquals("2099-12-01T00:00:00", JsonPath.read(result.json,
            "$.shipment.pieces[0].securityDeclaration.regulatedEntityIssuer.regulatedEntityExpiryDate"));
        assertEquals("MR. SECURITY OFFICER", JsonPath.read(result.json,
            "$.shipment.pieces[0].securityDeclaration.issuedBy.lastName"));
        // Note: 13APR211147 is converted into issuedOn" : "2021-04-13T11:47:00
        assertFalse(result.json.contains("13APR211147"));
        assertEquals("2021-04-13T11:47:00", JsonPath.read(result.json,
            "$.shipment.pieces[0].securityDeclaration.issuedOn"));

        assertEquals("X", JsonPath.read(result.json, "$.customsOriginCode.code"));
        assertEquals("https://onerecord.iata.org/ns/coreCodeLists#CurrencyCode_EUR",
            JsonPath.read(result.json, "$.originCurrency.id"));
        assertEquals("https://onerecord.iata.org/ns/coreCodeLists#PrepaidCollectIndicator_P",
            JsonPath.read(result.json,
                "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[1].chargePaymentType.id"));
        assertEquals("https://onerecord.iata.org/ns/coreCodeLists#EntitlementCode_C",
            JsonPath.read(result.json,
                "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[0].entitlement.id"));
        assertEquals("https://onerecord.iata.org/ns/coreCodeLists#OtherChargeCode_MC",
            JsonPath.read(result.json,
                "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[0].otherChargeCode.id"));
        assertEquals(1234.56, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[0].subTotal"));
        assertEquals("000111", JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[0].ofProduct.hsCode.code"));
        assertEquals(333,
            (Integer) JsonPath.read(result.json, "$.waybillLineItems[0].slacForRate"));
        // not available anymore on shipment -> Assertions.assertTrue(result.json.contains("PieceCount\" : 3"));
        assertEquals(
            "CONSOLIDATION\nAS PER ATTACHED\nMANIFEST\nSECURE CARGO\nNOT RESTRICTED\nAIRLINE PHARMA\nSERVICE",
            JsonPath.read(result.json, "$.shipment.pieces[0].goodsDescription"));

        // Dimensions
        assertEquals(96.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[1].dimensions.height.numericalValue"));
        assertEquals(120.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[1].dimensions.length.numericalValue"));
        assertEquals(80.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[1].dimensions.width.numericalValue"));
        assertEquals(2.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[1].itemQuantity.numericalValue"));
        assertEquals(79.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[2].dimensions.height.numericalValue"));
        assertEquals(1.0, JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[2].itemQuantity.numericalValue"));
        assertEquals(Vocabulary.s_c_MeasurementUnitCode + "_CMT", JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[2].dimensions.height.unit.id"));

        // Some more
        assertEquals(4626.48, JsonPath.read(result.json,
            "$.waybillLineItems[0].total.numericalValue"));
        assertEquals(1234.56, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[0].subTotal"));
        assertEquals(4626.48, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[1].subTotal"));
        assertEquals(11.22, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[2].subTotal"));
        assertEquals(222.33, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.ratings[3].subTotal"));
        assertEquals(4860.03, JsonPath.read(result.json,
            "$.referredBookingOption.bookingRequest.forBookingOption.price.grandTotal"));
    }

    @Test
    public void test88811111111noModeCode() throws JAXBException, JsonProcessingException {
        Result result =
            fileProcessingTest("888-11111111_XFWB_noModeCode.xml");

        assertNotNull(result.converter.getValidationWarnings());
        assertTrue(result.converter.getValidationWarnings().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationWarnings()) {
            System.out.println(result.awb + " WARNING: " + msg.getMessage());
        }

        assertNotNull(result.converter.getValidationErrors());
        assertTrue(result.converter.getValidationErrors().isEmpty());
        for (ValidationMessage msg : result.converter.getValidationErrors()) {
            System.out.println(result.awb + " ERROR: " + msg.getMessage());
        }

        System.out.println(result.awb + " JSON=\n" + result.json);
        assertEquals("USCI1234567812345678X7", JsonPath.read(result.json,
            "$.shipment.pieces[0].customsInformation[1].note"));
        assertEquals(
            "CONSOLIDATION\nAS PER ATTACHED\nMANIFEST\nSECURE CARGO\nNOT RESTRICTED\nAIRLINE PHARMA\nSERVICE",
            JsonPath.read(result.json, "$.shipment.pieces[0].goodsDescription"));
        assertEquals(
            Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.DIRECT.code(),
            JsonPath.read(result.json,
                "$.waybillType.id"));
    }

    @Test
    public void testPieceQuantityItemCountMismatch() throws JAXBException, JsonProcessingException {
        Result result =
            fileProcessingTest("888-11111111_XFWB_pieceQuantityItemCountMismatch.xml");
        Waybill oneRecordResult = result.converter.getOneRecordResult();
        Set<Piece> pieces = oneRecordResult.getShipment().getPieces();
        Optional<Piece> first = pieces.stream().findFirst();
        Piece mainPiece = null;
        if (first.isPresent()) {
            mainPiece = first.get();
        }
        assertNotNull(mainPiece);
        List<Item> items = mainPiece.getContainedItems().stream()
            .filter(i -> i.getItemQuantity() != null)
            .collect(Collectors.toList());

        WaybillType cxmlWaybill = getCXMLWaybill(
            "888-11111111_XFWB_pieceQuantityItemCountMismatch.xml");
        QuantityType totalPieceQuantity =
            cxmlWaybill.getMasterConsignment().getTotalPieceQuantity();
        int totalPieceQuantityXML = integerValue(totalPieceQuantity);
        int itemQuantity = 0;
        for (Item item : items) {
            itemQuantity += item.getItemQuantity().getNumericalValue();
        }
        // extra Item with itemQuantity = 2 should have been created since totalPieceQuantity in xml was 5
        assertEquals(itemQuantity, totalPieceQuantityXML);
    }

    @Test
    public void test88811111111_XFWB_multipleULD_multipleHTS()
        throws JAXBException, JsonProcessingException
    {
        Result result = fileProcessingTest("888-11111111_XFWB_multipleULD_multipleHTS.xml");
        // Items
        // 2 items for HTS + 2 from LogisticsPackage with dims
        assertEquals(4, (Integer) JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems.length()"));

        assertEquals("000111", JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[0].ofProduct.hsCode.code"));

        assertEquals("000222", JsonPath.read(result.json,
            "$.shipment.pieces[0].containedItems[1].ofProduct.hsCode.code"));

        // WaybillLineItems
        assertEquals(2, (Integer) JsonPath.read(result.json,
            "$.waybillLineItems.length()"));

        // 1st waybillLineItem with info from <ApplicableFreightRateServiceCharge> + slac + first ULD
        assertEquals(1042.0, JsonPath.read(result.json,
            "$.waybillLineItems[0].chargeableWeightForRate.numericalValue"));

        assertEquals(4.44, JsonPath.read(result.json,
            "$.waybillLineItems[0].rateCharge.numericalValue"));

        assertEquals(4626.48, JsonPath.read(result.json,
            "$.waybillLineItems[0].total.numericalValue"));

        assertEquals(333, (Integer) JsonPath.read(result.json,
            "$.waybillLineItems[0].slacForRate"));

        assertEquals("PMC", JsonPath.read(result.json,
            "$.waybillLineItems[0].uldType.code"));

        assertEquals("1337", JsonPath.read(result.json,
            "$.waybillLineItems[0].uldSerialNumber"));

        // 2nd waybillLineItem with info for 2nd ULD
        assertEquals("AKE", JsonPath.read(result.json,
            "$.waybillLineItems[1].uldType.code"));

        assertEquals("4711", JsonPath.read(result.json,
            "$.waybillLineItems[1].uldSerialNumber"));
    }

    private Result fileProcessingTest(String filename) throws JAXBException, JsonProcessingException {
        WaybillType xfwb = getCXMLWaybill(filename);
        Result result = new Result();
        result.awb = xfwb.getBusinessHeaderDocument().getID().getValue();
        result.converter = new XFWB3toOneRecordConverter(xfwb);
        result.json =
            JacksonObjectMapper.buildMapperWithoutTimezone()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result.converter.getOneRecordResult());
        assertNotNull(result.json);
        return result;
    }

    private WaybillType getCXMLWaybill(String filename) throws JAXBException {
        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        return new ConverterUtil().unmarshallXFWB3(is);
    }

    static class Result {
        XFWB3toOneRecordConverter converter;
        String awb;
        String json;
    }

}
