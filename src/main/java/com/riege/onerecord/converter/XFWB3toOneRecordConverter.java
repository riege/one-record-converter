package com.riege.onerecord.converter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.iata.cargo.codelists.BillingChargeCode;
import org.iata.cargo.codelists.ContactTypeCode;
import org.iata.cargo.codelists.MovementIndicatorCode;
import org.iata.cargo.codelists.PartyRoleCode;
import org.iata.cargo.codelists.WaybillTypeCode;
import org.iata.cargo.model.Address;
import org.iata.cargo.model.BookingOption;
import org.iata.cargo.model.Carrier;
import org.iata.cargo.model.Company;
import org.iata.cargo.model.Contact;
import org.iata.cargo.model.Country;
import org.iata.cargo.model.CustomsInfo;
import org.iata.cargo.model.Dimensions;
import org.iata.cargo.model.Event;
import org.iata.cargo.model.Insurance;
import org.iata.cargo.model.Item;
import org.iata.cargo.model.MovementTimes;
import org.iata.cargo.model.OtherIdentifier;
import org.iata.cargo.model.Party;
import org.iata.cargo.model.Person;
import org.iata.cargo.model.Piece;
import org.iata.cargo.model.Product;
import org.iata.cargo.model.Ranges;
import org.iata.cargo.model.Ratings;
import org.iata.cargo.model.RegulatedEntity;
import org.iata.cargo.model.SecurityDeclaration;
import org.iata.cargo.model.ServiceRequest;
import org.iata.cargo.model.Shipment;
import org.iata.cargo.model.SpecialHandling;
import org.iata.cargo.model.TransportMovement;
import org.iata.cargo.model.TransportSegment;
import org.iata.cargo.model.ULD;
import org.iata.cargo.model.Value;
import org.iata.cargo.model.VolumetricWeight;
import org.iata.cargo.model.Waybill;

import com.riege.cargoxml.schema.xfwb3.AccountingNoteType;
import com.riege.cargoxml.schema.xfwb3.AssociatedPartyType;
import com.riege.cargoxml.schema.xfwb3.AuthenticationLocationType;
import com.riege.cargoxml.schema.xfwb3.BusinessHeaderDocumentType;
import com.riege.cargoxml.schema.xfwb3.CarrierAuthenticationType;
import com.riege.cargoxml.schema.xfwb3.CodeType;
import com.riege.cargoxml.schema.xfwb3.ConsigneePartyType;
import com.riege.cargoxml.schema.xfwb3.ConsignorAuthenticationType;
import com.riege.cargoxml.schema.xfwb3.ConsignorPartyType;
import com.riege.cargoxml.schema.xfwb3.CustomsNoteType;
import com.riege.cargoxml.schema.xfwb3.DestinationCurrencyExchangeType;
import com.riege.cargoxml.schema.xfwb3.FreightForwarderAddressType;
import com.riege.cargoxml.schema.xfwb3.FreightForwarderPartyType;
import com.riege.cargoxml.schema.xfwb3.FreightRateServiceChargeType;
import com.riege.cargoxml.schema.xfwb3.IDType;
import com.riege.cargoxml.schema.xfwb3.LogisticsAllowanceChargeType;
import com.riege.cargoxml.schema.xfwb3.LogisticsPackageType;
import com.riege.cargoxml.schema.xfwb3.LogisticsTransportMovementType;
import com.riege.cargoxml.schema.xfwb3.MasterConsignmentItemType;
import com.riege.cargoxml.schema.xfwb3.MasterConsignmentType;
import com.riege.cargoxml.schema.xfwb3.MessageHeaderDocumentType;
import com.riege.cargoxml.schema.xfwb3.OSIInstructionsType;
import com.riege.cargoxml.schema.xfwb3.PrepaidCollectMonetarySummationType;
import com.riege.cargoxml.schema.xfwb3.RatingType;
import com.riege.cargoxml.schema.xfwb3.SPHInstructionsType;
import com.riege.cargoxml.schema.xfwb3.SSRInstructionsType;
import com.riege.cargoxml.schema.xfwb3.SpatialDimensionType;
import com.riege.cargoxml.schema.xfwb3.StructuredAddressType;
import com.riege.cargoxml.schema.xfwb3.TextType;
import com.riege.cargoxml.schema.xfwb3.TotalRatingType;
import com.riege.cargoxml.schema.xfwb3.TradeContactType;
import com.riege.cargoxml.schema.xfwb3.UnitLoadTransportEquipmentType;
import com.riege.cargoxml.schema.xfwb3.WaybillType;

import static com.riege.onerecord.converter.ConverterUtil.hasElements;
import static com.riege.onerecord.converter.ConverterUtil.isNullOrEmpty;
import static com.riege.onerecord.converter.OneRecordTypeConstants.buildSet;
import static com.riege.onerecord.converter.XFWB3ParserHelper.bigDecimal;
import static com.riege.onerecord.converter.XFWB3ParserHelper.integerValue;
import static com.riege.onerecord.converter.XFWB3ParserHelper.unitCode;
import static com.riege.onerecord.converter.XFWB3ParserHelper.value;

/*
 * JSON Examples for ONE Record can be found at
 *
 * (a) https://github.com/IATA-Cargo/ONE-Record/tree/master/working_draft/API/json-ld/Cargo%20related%20models
 * (b) the following link which leads to (a):
 *      https://onerecord.iata.org/codeexchange then scrolling down
 *      to 'Useful Resources' / 'JSON-LD representation' and
 *      the 'Take a look at the JSON-LD examples' link.
 */
public final class XFWB3toOneRecordConverter {

    public final static String VG_GENERAL = "General";
    public final static String VG_UNCERTAINTY = "Mapping-Uncertainty";
    public final static String VG_UNIMPLEMENTED = "Not-Implemented-Yet";
    public final static String VG_XMLDATAWARNING = "XML-Data-Warning";
    public final static String VG_XMLDATAERROR = "XML-Data-Error";
    public final static String VG_INFORMATION = "Info";

    private final WaybillType xfwb;
    private final ValidationResult validationResult;
    private final Waybill waybill;

    /**
     * Converter for CargoXML XFWB into a OneRecord (master or direct) Waybill.
     *
     * @param xfwb CargoXML XFWB
     */
    public XFWB3toOneRecordConverter(WaybillType xfwb) {
        this.xfwb = xfwb;

        validationResult = new ValidationResult();
        waybill = OneRecordTypeConstants.createWaybill();

        convertData();
    }

    /**
     * @return converted OneRecord (master or direct) Waybill
     */
    public Waybill getOneRecordResult() {
        return waybill;
    }

    /**
     * @return complete conversion errors, warnings and hints et al
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * @return conversion hints
     */
    public List<ValidationMessage> getValidationHints() {
        return validationResult.getHints();
    }

    /**
     * @return conversion warnings
     */
    public List<ValidationMessage> getValidationWarnings() {
        return validationResult.getWarnings();
    }

    /**
     * @return conversion warnings
     */
    public List<ValidationMessage> getValidationErrors() {
        return validationResult.getErrors();
    }

    // *************************************************************************

    // OneRecord helper instances
    private BookingOption mainBooking;
    private Carrier mainAirline;
    private TransportSegment mainTransportSegment;
    private Shipment mainShipment;
    private Piece mainPiece;

    // CargoXML shortcuts
    private MessageHeaderDocumentType xmlMH;
    private MasterConsignmentType xmlMC;
    private BusinessHeaderDocumentType xmlBH;
    private String awbCurrency;

    private void convertData() {
        /*
         * initialize the basic main 1R data structure:
         * BookingOption
         * with shipper, consignee, freightForwarder...
         * with TransportSegment as "transportMovement" which
         * representing the AWB-transport such as shipment-dep/des
         * with transportMovement which has a Piece as "transportedPieces" which
         * represents a piece/package related details on AWB level
         * BookingOption also has a Shipment as "shipmentDetails" which
         * represents some details only available for a shipment such
         * as Insurance..
         */
        // mainBooking = OneRecordTypeConstants.createBooking();
        // waybill.setBookingRef(mainBooking);
        mainBooking = OneRecordTypeConstants.createBookingOption();
        waybill.setBooking(mainBooking);

        // NOTE: v1.1 is the last version which uses TransportSegment
        //       will be replaced by TransportMovement in future versions
        mainTransportSegment = OneRecordTypeConstants.createTransportSegment();
        mainBooking.setTransportMovement(buildSet(mainTransportSegment));

        mainShipment = OneRecordTypeConstants.createShipment();
        mainBooking.setShipmentDetails(mainShipment);

        mainPiece = OneRecordTypeConstants.createPiece();
        mainTransportSegment.setTransportedPieces(buildSet(mainPiece));

        // Add the main carrier, as per AWB prefix
        mainAirline = OneRecordTypeConstants.createCarrier();
        mainBooking.setCarrier(mainAirline);

        /*
         * initialize some shortcuts to main CargoXML elements
         */
        xmlMH = xfwb.getMessageHeaderDocument();
        xmlMC = xfwb.getMasterConsignment();
        xmlBH = xfwb.getBusinessHeaderDocument();

        // General Hints()
        addHint(VG_GENERAL, "This converter intentionally does neither set IDs nor makes use of persisted data for linked-data purposes.");
        addHint(VG_GENERAL, "This converter is based on XFWB3 schema from IATA CargoXML Toolkit 8th Edition.");
        addHint(VG_GENERAL, "This converter is based on ONE RECORD datamodel Ontology 1.1, see https://github.com/IATA-Cargo/ONE-Record/tree/master/June-2021-standard-forCOTBendorsement/Data-Model");
        addHint(VG_GENERAL, "Codes and units are applied 1:1 from CargoXML where applicable.");
        addHint(VG_GENERAL, "Line breaks are respected for some fields if provided in XML, e.g. for multi-line goods description or address name/street");
        addHint(VG_GENERAL, "Line breaks are intentionally added in 'goodsDescription' and 'accountingInformation' to preserve and indicate descriptions from more than one field if applicaple from original XML");
        addHint(VG_GENERAL, "XFWB3 to 1R converter is not mapping all possible data yet and has focus on the use-case 'XFWB message from forwarder to airline'");

        if (xmlMC.getApplicableOriginCurrencyExchange() != null && xmlMC.getApplicableOriginCurrencyExchange().getSourceCurrencyCode() != null) {
            awbCurrency = value(xmlMC.getApplicableOriginCurrencyExchange().getSourceCurrencyCode());
            waybill.setOriginCurrency(awbCurrency);
        }

        /*
         * Conversion is split by topic of the individual CIMP segments,
         * just to allow easier navigation though source code.
         * CargoXML elements which have no CIMP mapping should get mapped
         * in the CIMP segment which matches best topic-wise.
         */
        convertCIMPSegment02();
        convertCIMPSegment03to04Flights();
        convertCIMPSegment05();
        convertCIMPSegment06();
        convertCIMPSegment07();
        convertCIMPSegment08();
        convertCIMPSegment09and26and28();
        convertCIMPSegment10();
        convertCIMPSegment11();
        convertCIMPSegment12();
        convertCIMPSegment13();
        convertCIMPSegment14to15ChargeSummary();
        convertCIMPSegment16to17Signatory();
        convertCIMPSegment18();
        convertCIMPSegment19();
        convertCIMPSegment20();
        convertCIMPSegment21();
        convertCIMPSegment22();
        convertCIMPSegment23();
        convertCIMPSegment24();
        convertCIMPSegment25();
        convertCIMPSegment27();
        convertCIMPSegment29();
    }

    private void addHint(String group, String text) {
        validationResult.addHint(group, text);
    }

    private void addWarning(String group, String text) {
        validationResult.addWarning(group, text);
    }

    private void addError(String group, String text) {
        validationResult.addError(group, text);
    }

    // *************************************************************************
    // CIMP FWB Segment 2: AWB Consignment Details (M)
    // *************************************************************************
    private void convertCIMPSegment02() {
        // Determine type of AWB ("Master" or "Direct")
        String typeCode = value(xmlMH.getTypeCode());
        if (typeCode == null) {
            addError(VG_XMLDATAERROR, "Missing TypeCode in MessageHeaderDocumentType");
        } else if ("740".equals(typeCode.trim())) {
            waybill.setWaybillType(WaybillTypeCode.DIRECT);
        } else if ("741".equals(typeCode.trim())) {
            waybill.setWaybillType(WaybillTypeCode.MASTER);
        } else {
            addError(VG_XMLDATAERROR, "Unsupported XFWB type code '" + typeCode + "' MessageHeaderDocumentType/TypeCode");
        }

        String xmlAwbNumber = value(xmlBH.getID());
        if (xmlAwbNumber == null) {
            addError(VG_XMLDATAERROR, "Missing AWB number, expected in BusinessHeaderDocument/ID");
        } else if (xmlAwbNumber.length() < 11) {
            addError(VG_XMLDATAERROR, "AWB number '" + xmlAwbNumber + "' too short");
        } else if (xmlAwbNumber.length() > 12) {
            addError(VG_XMLDATAERROR, "AWB number '" + xmlAwbNumber + "' too long");
        } else if (xmlAwbNumber.length() == 11 && !xmlAwbNumber.matches("[0-9]{11}")) {
            addError(VG_XMLDATAERROR, "AWB number '" + xmlAwbNumber + "' not matching expected format");
        } else if (xmlAwbNumber.length() == 12 && !xmlAwbNumber.matches("[0-9]{3}-[0-9]{8}")) {
            addError(VG_XMLDATAERROR, "AWB number '" + xmlAwbNumber + "' not matching expected format");
        } else {
            waybill.setWaybillPrefix(xmlAwbNumber.substring(0, 3));
            waybill.setWaybillNumber(xmlAwbNumber.substring(xmlAwbNumber.length() - 8));
        }

        // Add the main carrier, as per AWB prefix
        // mainAirline.setAirlinePrefix(String.format("%03d", waybill.getWaybillPrefix()));
        mainAirline.setAirlinePrefix(waybill.getWaybillPrefix());

        // departure and destination
        mainTransportSegment.setDepartureLocation(value(xmlMC.getOriginLocation()));
        mainTransportSegment.setArrivalLocation(value(xmlMC.getFinalDestinationLocation()));

        // totalPieceCount
        mainShipment.setTotalPieceCount(integerValue(xmlMC.getTotalPieceQuantity()));

        // totalGrossWeight
        mainShipment.setTotalGrossWeight(value(xmlMC.getIncludedTareGrossWeightMeasure()));
        mainPiece.setGrossWeight(mainShipment.getTotalGrossWeight());
        addHint(VG_INFORMATION, "(Total)GrossWeight is mandatory on Shipment and on Piece, value from MasterConsignment/IncludedTareGrossWeightMeasure is used for both");

        VolumetricWeight volumetricWeight = OneRecordTypeConstants.createVolumetricWeight();
        volumetricWeight.setChargeableWeight(mainShipment.getTotalGrossWeight());
        mainShipment.setVolumetricWeight(buildSet(volumetricWeight));
        mainPiece.setVolumetricWeight(volumetricWeight);
        addHint(VG_INFORMATION, "VolumetricWeight is mandatory on Shipment and on Piece, value from MasterConsignment/IncludedTareGrossWeightMeasure is used for both");

        // totalVolume
        if (xmlMC.getGrossVolumeMeasure() != null) {
            Dimensions volume = OneRecordTypeConstants.createDimensions();
            volume.setVolume(value(xmlMC.getGrossVolumeMeasure()));
            mainPiece.setDimensions(volume);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 3: Flight Bookings (O)
    // FLT is optional and must not contain more than 2 flights
    //
    // CIMP FWB Segment 4: Routing (M) (1st destination+carrier, onward des/car...)
    // RTG is mandatory and must not contain more then 3 entries
    //
    // FLT + RTG together define the flights which are assembled together
    // in XFWB SpecifiedLogisticsTransportMovement
    // *************************************************************************
    /*
     * Version 1.0: TransportSegments got used
     *
     * Changed in Version 1.1 to TransportMovement
     * Note: in TransportMovement, method setDepartureDate is no longer available!
    private void convertCIMPSegment03to04Flights() {
        Set<TransportSegment> flights = buildSet();
        for (LogisticsTransportMovementType ltm : xmlMC.getSpecifiedLogisticsTransportMovement()) {
            TransportSegment ts = OneRecordTypeConstants.createTransportSegment();
            // ModeCode is the same in 1R+XFWB, so we apply 1:1
            // In XFWB, ModeCode="4" means "Air transport"
            // In XFWB, there is also a ltm.getMode() with value "Air transport"
            ts.setModeCode(ltm.getModeCode().getValue());
            ts.setModeQualifier(value(ltm.getStageCode()));
            // as per 1R Ontology comments, valid values are "Actual" or "Planned"
            // "Planned" applies best for XFWB since also the flight day
            // is determined by the _scheduled_ departures date/time
            ts.setMovementType(OneRecordTypeConstants.MOVEMENT_TYPE_PLANNED);
            // TransportIdentifier = Flight Prefix+Number 1:1 from XFWB
            ts.setTransportIdentifier(value(ltm.getID()));
            if (ltm.getDepartureEvent() != null) {
                ts.setDepartureLocation(
                    value(ltm.getDepartureEvent().getOccurrenceDepartureLocation()));
                if (ltm.getDepartureEvent().getScheduledOccurrenceDateTime() != null) {
                    // Flight day = ScheduledOccurrenceDateTime via DepartureDate
                    ts.setDepartureDate(ltm.getDepartureEvent().getScheduledOccurrenceDateTime().toGregorianCalendar().getTime());
                }
            }

            if (ltm.getArrivalEvent() != null) {
                ts.setArrivalLocation(
                    value(ltm.getArrivalEvent().getOccurrenceArrivalLocation()));
            }
            flights.add(ts);
        }
        if (!flights.isEmpty()) {
            mainPiece.setTransportSegments(flights);
        }
    }
     */

    private void convertCIMPSegment03to04Flights() {
        Set<TransportMovement> flights = buildSet();
        for (LogisticsTransportMovementType ltm : xmlMC.getSpecifiedLogisticsTransportMovement()) {
            TransportMovement tm = OneRecordTypeConstants.createTransportMovement();
            // ModeCode is the same in 1R+XFWB, so we apply 1:1
            // In XFWB, ModeCode="4" means "Air transport"
            // In XFWB, there is also a ltm.getMode() with value "Air transport"
            if (ltm.getModeCode() != null) {
                tm.setModeCode(ltm.getModeCode().getValue());
            }
            tm.setModeQualifier(value(ltm.getStageCode()));
            if (ltm.getDepartureEvent() != null) {
                tm.setDepartureLocation(
                    value(ltm.getDepartureEvent().getOccurrenceDepartureLocation()));
                if (ltm.getDepartureEvent().getScheduledOccurrenceDateTime() == null) {
                    tm.setTransportIdentifier(value(ltm.getID()));
                } else {
                    // The getDepartureEvent().getScheduledOccurrenceDateTime()
                    // should map to 1R MovementTimes but in Ontology v1.1
                    // MovementTimes is not linked in TransportMovement :-/
                    MovementTimes mt = new MovementTimes();
                    mt.setMovementTimestamp(ltm.getDepartureEvent().getScheduledOccurrenceDateTime().toGregorianCalendar().getTime());
                    mt.setMovementMilestone(MovementIndicatorCode.SCHEDULED_DEPARTURE);
                    // now do nothing with the MovementTimes :-/
                    int day = ltm.getDepartureEvent().getScheduledOccurrenceDateTime().toGregorianCalendar().get(Calendar.DAY_OF_MONTH);
                    tm.setTransportIdentifier(value(ltm.getID()) + String.format("/%02d", day));
                }
            }

            if (ltm.getArrivalEvent() != null) {
                tm.setArrivalLocation(
                    value(ltm.getArrivalEvent().getOccurrenceArrivalLocation()));
            }
            flights.add(tm);
        }
        if (!flights.isEmpty()) {
            mainPiece.setTransportMovements(flights);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 5: Shipper (M)
    // *************************************************************************
    private void convertCIMPSegment05() {
        Company shipper = convertCompany(xmlMC.getConsignorParty(),
            getCustomsNotesBySubjectCode("SHP"));
        // mainBooking.setShipper(shipper);
        if (mainBooking.getParties() == null) {
            mainBooking.setParties(buildSet());
        }
        mainBooking.getParties().add(createParty(PartyRoleCode.SHP, shipper));
    }

    // *************************************************************************
    // CIMP FWB Segment 6: Consignee (M)
    // *************************************************************************
    private void convertCIMPSegment06() {
        Company consignee = convertCompany(xmlMC.getConsigneeParty(),
            getCustomsNotesBySubjectCode("CNE"));
        // mainBooking.setConsignee(consignee);
        if (mainBooking.getParties() == null) {
            mainBooking.setParties(buildSet());
        }
        mainBooking.getParties().add(createParty(PartyRoleCode.CNE, consignee));
    }

    // *************************************************************************
    // CIMP FWB Segment 7: (Export) Agent (C: if entitled to commission)
    // *************************************************************************
    private void convertCIMPSegment07() {
        FreightForwarderPartyType xmlForwarder = xmlMC.getFreightForwarderParty();
        Company forwarder = convertCompany(xmlForwarder,
            getCustomsNotesBySubjectCode("AGT"));
        // mainBooking.setFreightForwarder(buildSet(forwarder));
        if (mainBooking.getParties() == null) {
            mainBooking.setParties(buildSet());
        }
        mainBooking.getParties().add(createParty(PartyRoleCode.FFW, forwarder));
        if (xmlForwarder != null) {
            String value;
            value = value(xmlForwarder.getCargoAgentID());
            if (value != null) {
                if (!value.matches("[0-9]+")) {
                    addError(VG_XMLDATAERROR,
                        "FreightForwarderParty/CargoAgentID '" + value + "' not matching expected number format");
                } else {
                    forwarder.setIataCargoAgentCode(value);
                }
            }
            if (xmlForwarder.getSpecifiedCargoAgentLocation() != null) {
                value = value(xmlForwarder.getSpecifiedCargoAgentLocation().getID());
                if (!value.matches("[0-9]+")) {
                    addError(VG_XMLDATAERROR,
                        "FreightForwarderParty/SpecifiedCargoAgentLocation/ID '" + value + "' not matching expected number format");
                } else {
                    forwarder.getBranch().setIataCargoAgentLocationIdentifier(value);
                }
            }
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 8: Special Service Request (O)
    // Elements 8.2 thru 8.4 can be repeated. Can occur a maximum of three times.
    // *************************************************************************
    private void convertCIMPSegment08() {
        if (isNullOrEmpty(xmlMC.getHandlingSSRInstructions())) {
            return;
        }
        // filed as https://github.com/IATA-Cargo/ONE-Record/issues/134
        addWarning(VG_UNCERTAINTY,
            "Unclear where to put HandlingSSRInstructions, using ServiceRequest with code=\""
            + OneRecordTypeConstants.SERVICE_REQUEST_TYPE_SSR
            + "\" as workaround");
        if (mainPiece.getServiceRequest() == null) {
            mainPiece.setServiceRequest(buildSet());
        }
        for (SSRInstructionsType xmlInstr : xmlMC.getHandlingSSRInstructions()) {
            ServiceRequest sr = OneRecordTypeConstants.createServiceRequest();
            sr.setCode(OneRecordTypeConstants.SERVICE_REQUEST_TYPE_SSR);
            if (xmlInstr.getDescriptionCode() != null) {
                sr.setStatementType(value(xmlInstr.getDescriptionCode()));
            }
            sr.setServiceRequestDescription(value(xmlInstr.getDescription()));
            mainPiece.getServiceRequest().add(sr);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 9: Notify (O)
    // CIMP FWB Segment 26: Nominated Handling Party (Optional, only used in Handling FWBs)
    // CIMP FWB Segment 28: OPI: Other Participant Information
    // *************************************************************************
    private void convertCIMPSegment09and26and28() {
        List<AssociatedPartyType> xmlNotifies = xmlMC.getAssociatedParty();
        if (xmlNotifies != null) {
            for (AssociatedPartyType party : xmlNotifies) {
                String xmlRoleCode = party.getRoleCode() != null && party.getRoleCode().getValue() != null
                    ? party.getRoleCode().getValue().value()
                    : null;
                if ("NI".equals(xmlRoleCode)) {
                    Company otherPary = convertCompany(party, getCustomsNotesBySubjectCode(xmlRoleCode));
                    mainBooking.getParties().add(createParty(PartyRoleCode.NFY, otherPary));
                } else if ("FB".equals(xmlRoleCode)) {
                    Company otherPary = convertCompany(party, getCustomsNotesBySubjectCode(xmlRoleCode));
                    mainBooking.getParties().add(createParty(PartyRoleCode.NOM, otherPary));
                } else if ("OJ".equals(xmlRoleCode)) {
                    Company otherPary = convertCompany(party, getCustomsNotesBySubjectCode(xmlRoleCode));
                    mainBooking.getParties().add(createParty(PartyRoleCode.OPI, otherPary));
                } else {
                    addWarning(VG_UNCERTAINTY,
                        "Unclear where to put AssociatedParty/RoleCode=" + xmlRoleCode
                        + ", not adding to result!");
                }
            }
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 10: Accounting Info (O)
    // Element 10.2 can be repeated. Can occur a maximum of six times.
    // official 1R documention (IATA-1R-DM-Logical-Data-Model-vCOTB-Nov2020) tells
    // "Accounting information	Only accounting information required by carriers"
    // *************************************************************************
    private void convertCIMPSegment10() {
        if (isNullOrEmpty(xmlMC.getIncludedAccountingNote())) {
            return;
        }
        for (AccountingNoteType xmlAccInfo : xmlMC.getIncludedAccountingNote()) {
            String type = value(xmlAccInfo.getContentCode());
            String info = value(xmlAccInfo.getContent());
            if (type != null && !"GEN".equals(type)) {
                info = type + ":" + info;
            }
            if (waybill.getAccountingInformation() == null) {
                waybill.setAccountingInformation(info);
            } else {
                waybill.setAccountingInformation(waybill.getAccountingInformation() + "\n" + info);
            }
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 11: Charge Declaration (M)
    // *************************************************************************
    private void convertCIMPSegment11() {
        Insurance insurance = OneRecordTypeConstants.createInsurance();
        insurance.setNvdIndicator(xmlMC.isNilInsuranceValueIndicator());
        boolean isNilInsurance = xmlMC.isNilInsuranceValueIndicator() != null && xmlMC.isNilInsuranceValueIndicator();
        if (!isNilInsurance) {
            insurance.setInsuranceAmount(value(xmlMC.getInsuranceValueAmount(), awbCurrency));
        }
        mainShipment.setInsurance(insurance);

        mainPiece.setNvdForCarriage(xmlMC.isNilCarriageValueIndicator());
        boolean isNilCarriage = xmlMC.isNilCarriageValueIndicator() != null && xmlMC.isNilCarriageValueIndicator();
        if (!isNilCarriage) {
            mainPiece.setDeclaredValueForCarriage(buildSet(
                xmlMC.getDeclaredValueForCarriageAmount().getValue().toString()
            ));
        }

        mainPiece.setNvdForCustoms(xmlMC.isNilCustomsValueIndicator());
        boolean isNilCustoms = xmlMC.isNilCustomsValueIndicator() != null && xmlMC.isNilCustomsValueIndicator();
        if (!isNilCustoms) {
            mainPiece.setDeclaredValueForCustoms(buildSet(
                xmlMC.getDeclaredValueForCustomsAmount().getValue().toString()
            ));
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 12: Rate Description (M)
    // *************************************************************************
    private void convertCIMPSegment12() {
        boolean hasRateDescriptions = xmlMC.getApplicableRating() != null
            && xmlMC.getApplicableRating().size() > 0
            && xmlMC.getApplicableRating().get(0).getIncludedMasterConsignmentItem() != null
            && xmlMC.getApplicableRating().get(0).getIncludedMasterConsignmentItem().size() > 0;
        if (!hasRateDescriptions) {
            addError(VG_XMLDATAERROR,
                "No rate descriptions detected (ApplicableRating/IncludedMasterConsignmentItem)");
            return;
        }
        // Data which is exclusivly ONLY ONCE in forwarder (X)FWB:
        // xmlMC.getApplicableRating().get(0).getIncludedMasterConsignmentItem().get(0):
        // descr.getVolume())
        // descr.getShippersLoadAndCount() != null
        // descr.getCountryOfOriginOfGoods()
        // descr.getServiceCode() -> this is unused by Forwarder!

        // totalSLAC
        MasterConsignmentItemType firstMasterConsignmentItem = xmlMC.getApplicableRating().get(0).getIncludedMasterConsignmentItem().get(0);
        mainShipment.setTotalSLAC(integerValue(firstMasterConsignmentItem.getPackageQuantity()));

        // HTS:
        // loop+collect xmlMC.getApplicableRating().get(0).getIncludedMasterConsignmentItem().get(0).getTypeCode()
        List<String> hts = new ArrayList<>();
        // Note: avoid using "double" for calculation, we will run into rounding problems
        BigDecimal rateTotal = BigDecimal.ZERO;
        List<String> nog = new ArrayList<>();
        List<Item> allDims = new ArrayList<>();
        List<ULD> allULD = new ArrayList<>();
        Ratings mainRateDescription = null;
        BigDecimal mainRateQuantity = BigDecimal.ZERO;
        for (RatingType rt : xmlMC.getApplicableRating()) {
            for (MasterConsignmentItemType mci : rt.getIncludedMasterConsignmentItem()) {
                for (CodeType codeType : mci.getTypeCode()) {
                    if (codeType != null) {
                        hts.add(value(codeType));
                    }
                }
                if (mci.getNatureIdentificationTransportCargo() != null) {
                    String nature = value(
                        mci.getNatureIdentificationTransportCargo().getIdentification());
                    if (nature != null) {
                        nog.addAll(Arrays.asList(nature.split("\n")));
                    }
                }

                if (mainPiece.getProduct() == null) {
                    mainPiece.setProduct(buildSet());
                }

                if (mci.getOriginCountry() != null) {
                    mainPiece.setProductionCountry(
                        value(mci.getOriginCountry().getID(), null));
                }

                for (LogisticsPackageType lp : mci.getTransportLogisticsPackage()) {
                    int xmlPackageCount = lp.getItemQuantity() == null
                        ? 1
                        : integerValue(lp.getItemQuantity());
                    SpatialDimensionType xmlDim = lp.getLinearSpatialDimension();
                    if (xmlDim == null) {
                        continue;
                    }
                    Dimensions dim1R = OneRecordTypeConstants.createDimensions();
                    dim1R.setHeight(value(xmlDim.getHeightMeasure()));
                    dim1R.setLength(value(xmlDim.getLengthMeasure()));
                    dim1R.setWidth(value(xmlDim.getWidthMeasure()));
                    Item item = OneRecordTypeConstants.createItem();
                    // "Product" is mandatory for item as per Ontology
                    item.setProduct(OneRecordTypeConstants.createProduct());
                    item.setDimensions(dim1R);
                    item.setWeight(value(lp.getGrossWeightMeasure()));
                    Value count = OneRecordTypeConstants.createValue();
                    count.setValue(Double.valueOf(xmlPackageCount));
                    item.setQuantity(count);
                    allDims.add(item);
                }

                for (UnitLoadTransportEquipmentType xmlULD : mci.getAssociatedUnitLoadTransportEquipment()) {
                    ULD uld1R = OneRecordTypeConstants.createULD();
                    uld1R.setSerialNumber(value(xmlULD.getID()));
                    uld1R.setTareWeight(value(xmlULD.getTareWeightMeasure()));
                    uld1R.setUldTypeCode(value(xmlULD.getCharacteristicCode()));
                    if (xmlULD.getOperatingParty() != null) {
                        uld1R.setOwnerCode(value(xmlULD.getOperatingParty().getPrimaryID()));
                    }
                    allULD.add(uld1R);
                }

                if (hasElements(mci.getAssociatedUnitLoadTransportEquipment())) {
                    addWarning(VG_UNIMPLEMENTED,
                        "Mapping for ApplicableRating/MasterConsignmentItem/AssociatedUnitLoadTransportEquipment is not implemented yet");
                }
                FreightRateServiceChargeType xmlRate = mci.getApplicableFreightRateServiceCharge();

                if (xmlRate != null) {
                    if (mainRateDescription == null) {
                        // initialize
                        mainRateDescription = OneRecordTypeConstants.createRatings();
                        // chargeType is unresticted free text in Ontology 1.2
                        mainRateDescription.setChargeType("Freight");
                        mainRateDescription.setRanges(buildSet());
                        mainBooking.setPrice(OneRecordTypeConstants.createPrice());
                        mainBooking.getPrice().setRatings(buildSet(mainRateDescription));
                        // "Rate class" only fits into "in Ranges/rateClassCode"
                        // The only place where a "Ranges" is available is a "Ratings"
                        // "Ratings" is only available in "Price" or "Request"
                        // Neither "OtherCharges" nor "RateDescriptions" directly belong to a (booking)-Request
                        // Therefore we add them to a Price of the Booking which already exists.
                    }
                    // note: it might be a (Q)uantity rate or (M)inimum rate or other!
                    Ranges rate1R = OneRecordTypeConstants.createRanges();
                    rate1R.setRateClassCode(value(xmlRate.getCategoryCode()));
                    if (xmlRate.getCommodityItemID() != null) {
                        Product product = OneRecordTypeConstants.createProduct();
                        product.setCommodityItemNumber(value(xmlRate.getCommodityItemID()));
                        mainPiece.getProduct().add(product);
                    }
                    if (xmlRate.getAppliedRate() != null) {
                        rate1R.setAmount(xmlRate.getAppliedRate().doubleValue());
                    }

                    if (xmlRate.getChargeableWeightMeasure() != null) {
                        rate1R.setUnitBasis(unitCode(xmlRate.getChargeableWeightMeasure()));  // wie in Shipment
                        BigDecimal chargableWeight = bigDecimal(xmlRate.getChargeableWeightMeasure());
                        /*
                         * Ontology 2022-May: we no longer use rate1R.setMax/Min
                         *                    but we use quantity on mainRate!
                         */
                        // rate1R.setMaximumQuantity(chargableWeight.doubleValue());
                        // rate1R.setMinimumQuantity(rate1R.getMaximumQuantity());
                        mainRateQuantity = mainRateQuantity.add(chargableWeight);
                        mainRateDescription.setQuantity(mainRateQuantity.toString());
                    }
                    if (mci.getSpecifiedRateCombinationPointLocation() != null) {
                        // IATA considers RCP as deprecated field
                        // We are not sure about using mainRateDescription.setRcp()
                        // and supplying all details..
                        /*
                        mainRateDescription.setRcp(
                            value(mci.getSpecifiedRateCombinationPointLocation().getID())
                        );
                         */
                        addWarning(VG_XMLDATAWARNING,
                            "Rate Construction Points (RCP) is considered deprecated as per IATA 1R and not mapped (ApplicableRating/IncludedMasterConsignmentItem/SpecifiedRateCombinationPointLocation)");
                        return;
                    }
                    if (xmlRate.getAppliedAmount() != null) {
                        // the appliedAmount (=rate line total) is not
                        // stored in 1R, officially it's a
                        //
                        rateTotal = rateTotal.add(xmlRate.getAppliedAmount().getValue());
                        mainRateDescription.setSubTotal(rateTotal.doubleValue());
                    }
                    mainRateDescription.getRanges().add(rate1R);
                }
            }
        }
        if (!hts.isEmpty()) {
            if (mainPiece.getContainedItems() == null) {
                mainPiece.setContainedItems(buildSet());
            }
            for (String hsCode : hts) {
                Item item = OneRecordTypeConstants.createItem();
                item.setProduct(OneRecordTypeConstants.createProduct());
                item.getProduct().setHsCode(hsCode);
                mainPiece.getContainedItems().add(item);
            }
        }
        // Dimensions
        if (!allDims.isEmpty()) {
            if (mainPiece.getContainedItems() == null) {
                mainPiece.setContainedItems(OneRecordTypeConstants.buildSet());
            }
            mainPiece.getContainedItems().addAll(allDims);
        }
        // ULDs
        if (!allULD.isEmpty()) {
            if (mainTransportSegment.getTransportedUlds() == null) {
                mainTransportSegment.setTransportedUlds(OneRecordTypeConstants.buildSet());
            }
            mainTransportSegment.getTransportedUlds().addAll(allULD);
        }
        // Nature of Goods
        for (String s : nog) {
            if (mainPiece.getGoodsDescription() == null) {
                mainPiece.setGoodsDescription(s);
            } else {
                mainPiece.setGoodsDescription(mainPiece.getGoodsDescription() + "\n" + s);
            }
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 13: Other Charges (O)
    // *************************************************************************
    private void convertCIMPSegment13() {
        boolean othIsPrepaid = false, othIsCollect = false;
        if (isNullOrEmpty(xmlMC.getApplicableLogisticsAllowanceCharge())) {
            addWarning(VG_XMLDATAWARNING,
                "No other charges detected (ApplicableLogisticsAllowanceCharge)");
            return;
        }
        if (!xmlMC.getApplicableLogisticsAllowanceCharge().isEmpty()) {
            addWarning(VG_INFORMATION,
                "For other charges determined from \"<ApplicableLogisticsAllowanceCharge>\", "
                    + "we apply a 1R charge type value \"Surcharge\"");
        }
        for (LogisticsAllowanceChargeType xmlLAC : xmlMC.getApplicableLogisticsAllowanceCharge()) {
            Ratings otherCharge = OneRecordTypeConstants.createRatings();
            // chargeType is unresticted free text in Ontology 1.2
            otherCharge.setChargeType("Surcharge");
            // Still missing entitlement. It should be something like this:
            otherCharge.setOtherChargeCode(value(xmlLAC.getID()));
            // "due" (aka Entitlement) in Ratings as per
            // #116 https://github.com/IATA-Cargo/ONE-Record/issues/116
            otherCharge.setEntitlement(value(xmlLAC.getPartyTypeCode()));
            otherCharge.setChargePaymentType(buildSet(xmlLAC.isPrepaidIndicator() ? "P" : "C"));

            Value amount = value(xmlLAC.getActualAmount(), awbCurrency);
            otherCharge.setSubTotal(amount.getValue().doubleValue());
            if (amount.getUnit() != null) {
                Ranges ranges = OneRecordTypeConstants.createRanges();
                ranges.setUnitBasis(amount.getUnit());
                otherCharge.setRanges(buildSet(ranges));
            }

            othIsPrepaid |= xmlLAC.isPrepaidIndicator();
            othIsCollect |= !xmlLAC.isPrepaidIndicator();

            mainBooking.getPrice().getRatings().add(otherCharge);
        }
        if (othIsPrepaid && othIsCollect) {
            addError(VG_XMLDATAERROR,
                "ApplicableLogisticsAllowanceCharge contains Prepaid and Collect charges - all other charges are required to be of same type");
            mainShipment.setOtherChargesIndicator("P+C");
        } else if (othIsCollect) {
            mainShipment.setOtherChargesIndicator("C");
        } else if (othIsPrepaid) {
            mainShipment.setOtherChargesIndicator("P");
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 14: Prepaid Charge Summary (O)
    // CIMP FWB Segment 15: Collect Charge Summary (O)
    // IATA document "NOV-2020.xlsx" tells "Charge Summary" goes "in Ratings"
    // *************************************************************************
    private void convertCIMPSegment14to15ChargeSummary() {
        if (isNullOrEmpty(xmlMC.getApplicableTotalRating())) {
            addWarning(VG_XMLDATAWARNING,
                "No (prepaid or collect) charge summary detected (ApplicableTotalRating/ApplicablePrepaidCollectMonetarySummation)");
            return;
        }
        if (xmlMC.getApplicableTotalRating().size() > 1) {
            addWarning(VG_UNIMPLEMENTED,
                "Mapping only implemented for first ApplicableTotalRating, additional elements are ignored");
        }
        TotalRatingType xmlTR = xmlMC.getApplicableTotalRating().get(0);
        if (isNullOrEmpty(xmlTR.getApplicablePrepaidCollectMonetarySummation())) {
            addWarning(VG_XMLDATAWARNING,
                "No (prepaid or collect) charge summary detected (ApplicableTotalRating/ApplicablePrepaidCollectMonetarySummation)");
            return;
        }
        // TotalRatingType contains 1-2 PrepaidCollectMappings (either Prepaid or Collect)
        for (PrepaidCollectMonetarySummationType summary : xmlTR.getApplicablePrepaidCollectMonetarySummation()) {
            // in the sequence they appear on the AWB, with the names used in CXML XFWB
            if (summary.getWeightChargeTotalAmount() != null) {
                mainShipment.setWeightValuationIndicator(summary.isPrepaidIndicator() ? "P" : "C");
                Value value = value(summary.getWeightChargeTotalAmount(), awbCurrency);
                addTotalAmountRating(BillingChargeCode.TOTAL_WEIGHT_CHARGE, summary.isPrepaidIndicator(), value);
            }
            if (summary.getValuationChargeTotalAmount() != null) {
                Value value = value(summary.getValuationChargeTotalAmount(), awbCurrency);
                addTotalAmountRating(BillingChargeCode.VALUATION_CHARGE, summary.isPrepaidIndicator(), value);
            }
            if (summary.getTaxTotalAmount() != null) {
                Value value = value(summary.getTaxTotalAmount(), awbCurrency);
                addTotalAmountRating(BillingChargeCode.TAXES, summary.isPrepaidIndicator(), value);
            }
            if (summary.getAgentTotalDuePayableAmount() != null) {
                Value value = value(summary.getAgentTotalDuePayableAmount(), awbCurrency);
                addTotalAmountRating(BillingChargeCode.TOTAL_OTHER_CHARGES_DUE_AGENT, summary.isPrepaidIndicator(), value);
            }
            if (summary.getCarrierTotalDuePayableAmount() != null) {
                Value value = value(summary.getCarrierTotalDuePayableAmount(), awbCurrency);
                addTotalAmountRating(BillingChargeCode.TOTAL_OTHER_CHARGES_DUE_CARRIER, summary.isPrepaidIndicator(), value);
            }
            if (summary.getGrandTotalAmount() != null) {
                Value value = value(summary.getGrandTotalAmount(), awbCurrency);
                mainBooking.getPrice().setGrandTotal(value.getValue());
            }
        }
    }

    private void addTotalAmountRating(BillingChargeCode type, boolean isPrepaid, Value value) {
        Ratings totalAmount = OneRecordTypeConstants.createRatings();
        totalAmount.setBillingChargeIdentifier(type);
        totalAmount.setChargePaymentType(buildSet(isPrepaid ? "P" : "C"));
        totalAmount.setSubTotal(value.getValue().doubleValue());
        if (value.getUnit() != null) {
            Ranges ranges = OneRecordTypeConstants.createRanges();
            ranges.setUnitBasis(value.getUnit());
            totalAmount.setRanges(buildSet(ranges));
        }
        mainBooking.getPrice().getRatings().add(totalAmount);
    }

    // *************************************************************************
    // CIMP FWB Segment 16: Shipper's Certification (O)
    // CIMP FWB Segment 17: Carrier's Execution (M)
    // *************************************************************************
    private void convertCIMPSegment16to17Signatory() {
        CarrierAuthenticationType sigCarrier = xmlBH.getSignatoryCarrierAuthentication();
        if (sigCarrier != null) {
            waybill.setCarrierDeclarationSignature(value(sigCarrier.getSignatory()));
            waybill.setCarrierDeclarationDate(sigCarrier.getActualDateTime().toGregorianCalendar().getTime());
            AuthenticationLocationType location = sigCarrier.getIssueAuthenticationLocation();
            if (location != null) {
                waybill.setCarrierDeclarationPlace(OneRecordTypeConstants.createLocation());
                waybill.getCarrierDeclarationPlace().setCode(value(location.getName()));
            }
        }
        ConsignorAuthenticationType sigConsignor = xmlBH.getSignatoryConsignorAuthentication();
        if (sigConsignor != null) {
            waybill.setConsignorDeclarationSignature(buildSet(value(sigConsignor.getSignatory())));
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 18: Other Service Information (O)
    // Elements 18.2 thru 18.4 can be repeated. Can occur a maximum of three times
    // *************************************************************************
    private void convertCIMPSegment18() {
        if (isNullOrEmpty(xmlMC.getHandlingOSIInstructions())) {
            return;
        }
        // filed as https://github.com/IATA-Cargo/ONE-Record/issues/134
        addWarning(VG_UNCERTAINTY,
            "Unclear where to put HandlingOSIInstructions, using ServiceRequest with code=\""
            + OneRecordTypeConstants.SERVICE_REQUEST_TYPE_OSI
            + "\" as workaround");
        if (mainPiece.getServiceRequest() == null) {
            mainPiece.setServiceRequest(buildSet());
        }
        for (OSIInstructionsType xmlInstr : xmlMC.getHandlingOSIInstructions()) {
            ServiceRequest sr = OneRecordTypeConstants.createServiceRequest();
            sr.setCode(OneRecordTypeConstants.SERVICE_REQUEST_TYPE_OSI);
            if (xmlInstr.getDescriptionCode() != null) {
                sr.setStatementType(value(xmlInstr.getDescriptionCode()));
            }
            sr.setStatementText(value(xmlInstr.getDescription()));
            mainPiece.getServiceRequest().add(sr);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 19: CC Charges in Destination Currency (O)
    // only used by Airlines, not by Forwarders
    // *************************************************************************
    private void convertCIMPSegment19() {
        DestinationCurrencyExchangeType destCux = xmlMC.getApplicableDestinationCurrencyExchange();
        if (destCux != null) {
            if (destCux.getTargetCurrencyCode() != null) {
                waybill.setDestinationCurrencyCode(value(destCux.getTargetCurrencyCode()));
            }
            if (destCux.getConversionRate() != null) {
                waybill.setDestinationCurrencyRate(destCux.getConversionRate().doubleValue());
            }

        }
    }

    // *************************************************************************
    // CIMP FWB Segment 20: Sender Reference (M)
    // *************************************************************************
    private void convertCIMPSegment20() {
        /*
         * The REF segment contains some agent information (name, location)
         * (forwarders don't add the TypeB/Ref 20.3 fields for OfficeCityCode,
         *  OfficeFunctionDesignator or OfficeCompanyDesignator)
         * which indeed make more sense in the explicit Agent information
         * in the CXML. Any file reference (Riege adds location code to it)
         * is repeated in the CIMP FWB Segment 27: "SRI"
         * Shipment Reference Information segment anyway.
         * So no additional mapping needed here.
         *
         * Examples:
         * REF//HKG8100106391/AGT/SCHENKER/HKG
         * SRI/8100106391
         *
         * REF//AMSAAM-AE-02170/AGT/AIRCARGONLBV/AMS
         * SRI/AAM-AE-021705
         */
        // CIMP field 20.5.1 is already covered in convertCIMPSegment27()!
        // other fields in "Sender Reference" are mapping to CXML
        // send/receive parties and agent details.
    }

    // *************************************************************************
    // CIMP FWB Segment 21: Customs Origin (O)
    // *************************************************************************
    private void convertCIMPSegment21() {
        if (xmlMC.getAssociatedConsignmentCustomsProcedure() != null) {
            // https://github.com/IATA-Cargo/ONE-Record/issues/135
            addWarning(VG_UNCERTAINTY,
                "Unclear where to put AssociatedConsignmentCustomsProcedure, using ServiceRequest with code=\""
                + OneRecordTypeConstants.SERVICE_REQUEST_TYPE_CUSTOMS_ORIGIN
                + "\" as workaround");
            if (mainPiece.getServiceRequest() == null) {
                mainPiece.setServiceRequest(buildSet());
            }
            ServiceRequest sr = OneRecordTypeConstants.createServiceRequest();
            sr.setCode(OneRecordTypeConstants.SERVICE_REQUEST_TYPE_CUSTOMS_ORIGIN);
            sr.setStatementText(value(xmlMC.getAssociatedConsignmentCustomsProcedure().getGoodsStatusCode()));
            mainPiece.getServiceRequest().add(sr);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 22: Commission Information (Optional, only used in Handling FWBs)
    // *************************************************************************
    private void convertCIMPSegment22() {
        // not found in CargoXML?
        // addHint("Commission Information not implemented yet (CargoIMP FWB Segment 22)");
    }

    // *************************************************************************
    // CIMP FWB Segment 23: Sales Incentive (Optional, only used in Handling FWBs)
    // *************************************************************************
    private void convertCIMPSegment23() {
        // not found in CargoXML?
        // addHint("Sales Incentive not implemented yet (CargoIMP FWB Segment 23)");
    }

    // *************************************************************************
    // CIMP FWB Segment 24. Agent Reference Data (Optional, only used in Handling FWBs)
    // *************************************************************************
    private void convertCIMPSegment24() {
        // not found in CargoXML?
        // addHint("Agent Reference Data not implemented yet (CargoIMP FWB Segment 24)");
    }

    // *************************************************************************
    // CIMP FWB Segment 25: Special Handling Details (O)
    // *************************************************************************
    private void convertCIMPSegment25() {
        if (isNullOrEmpty(xmlMC.getHandlingSPHInstructions())) {
            return;
        }
        for (SPHInstructionsType xmlInstr : xmlMC.getHandlingSPHInstructions()) {
            if (xmlInstr.getDescriptionCode() != null) {
                if (mainPiece.getSpecialHandling() == null) {
                    mainPiece.setSpecialHandling(OneRecordTypeConstants.buildSet());
                }
                SpecialHandling sh = OneRecordTypeConstants.createSpecialHandling();
                sh.setCode(value(xmlInstr.getDescriptionCode()));
                mainPiece.getSpecialHandling().add(sh);
            }
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 27: SRI Shipment Reference Information
    // *************************************************************************
    private void convertCIMPSegment27() {
        // prefer BusinessHeaderDocument.SenderAssignedID over MasterConsignment.ID
        // for setOptionalShippingRefNo if available
        String senderAssignedID = value(xmlBH.getSenderAssignedID());
        String shippingRef = value(xmlMC.getID());
        if (senderAssignedID != null && shippingRef != null) {
            if (senderAssignedID.length() >= shippingRef.length()) {
                waybill.setOptionalShippingRefNo(senderAssignedID);
                addHint(VG_INFORMATION, "BusinessHeaderDocument.SenderAssignedID (=CIMP 20.5.1) got preferred over MasterConsignment.ID as 'longer ID' for 1R.OptionalShippingRefNo");
            } else {
                waybill.setOptionalShippingRefNo(shippingRef);
                addHint(VG_INFORMATION, "MasterConsignment.ID got preferred over BusinessHeaderDocument.SenderAssignedID (=CIMP 20.5.1) as 'longer ID' for 1R.OptionalShippingRefNo");
            }
        } else if (senderAssignedID != null) {
            waybill.setOptionalShippingRefNo(senderAssignedID);
        } else {
            waybill.setOptionalShippingRefNo(shippingRef);
        }
        waybill.setOptionalShippingInfo(value(xmlMC.getAdditionalID()));
    }

    // *************************************************************************
    // CIMP FWB Segment 29: OCI: Other Customs Information
    // *************************************************************************
    private void convertCIMPSegment29() {
        if (isNullOrEmpty(xmlMC.getIncludedCustomsNote())) {
            return;
        }

        SecurityDeclaration secDec = OneRecordTypeConstants.createSecurityDeclaration();
        String previousCiSubjectCode = "X";
        boolean haveSecDec = false;
        boolean haveCTCP = false;
        for (CustomsNoteType xmlCustNote : xmlMC.getIncludedCustomsNote()) {
            String contentCode = value(xmlCustNote.getContentCode());
            String note = value(xmlCustNote.getContent());
            String subjectCode = value(xmlCustNote.getSubjectCode());
            String information = null;
            CustomsInfo custInfo = OneRecordTypeConstants.createCustomsInfo();
            custInfo.setCustomsInfoContentCode(contentCode);
            custInfo.setCustomsInfoNote(note);
            custInfo.setCustomsInfoSubjectCode(subjectCode);
            if (xmlCustNote.getCountryID() != null) {
                information = value(xmlCustNote.getCountryID(), null).getCountryCode();
                custInfo.setCustomsInformation(information);
            }
            if ("CT".equals(contentCode) ||
                "CP".equals(contentCode))
            {
                // CT = Contact Telephone
                // CP = Contact Person
                // we already added them during address processing!
                if (!haveCTCP) {
                    addHint(VG_INFORMATION, "IncludedCustomsNote 'CT' and 'CP' (Contact Telephone / Contact Person) are added as contact to the applicable company rather converted to a CustomsInfo");
                }
                haveCTCP = true;
                continue;
            }
            if (updateSecurityDeclaration(custInfo, secDec, previousCiSubjectCode)) {
                haveSecDec = true;
                // /IE/ISS/RA/00084-01
                addHint(VG_INFORMATION,
                    "IncludedCustomsNote '"
                    + (information == null ? "" : information)
                    + "/"
                    + (subjectCode == null ? "" : subjectCode)
                    + "/"
                    + (contentCode == null ? "" : contentCode)
                    + "/"
                    + (note == null ? "" : note)
                    + "' transformed into SecurityStatus."
                );
            } else {
                if (mainPiece.getCustomsInfo() == null) {
                    mainPiece.setCustomsInfo(OneRecordTypeConstants.buildSet());
                }
                mainPiece.getCustomsInfo().add(custInfo);
            }
            if (subjectCode != null) {
                previousCiSubjectCode = subjectCode;
            }
        }
        if (haveSecDec) {
            mainPiece.setSecurityStatus(secDec);
        }
    }

    private boolean updateSecurityDeclaration(CustomsInfo ci, SecurityDeclaration secDec,
        String previousCiSubjectCode)
    {
        // ISS = "Issuing" Security Status = issuing
        // OSS = "Receiving" Security Status = accepting
        if ("ISS".equals(ci.getCustomsInfoSubjectCode())) {
            secDec.setRegulatedEntityIssuer(convert(ci));
            return true;
        }
        if ("OSS".equals(ci.getCustomsInfoSubjectCode()) && secDec.getOtherRegulatedEntity() == null) {
            // the "Receiving" cannot be the "ReceivedFrom"
            // so we add the OSS to the "OtherRegulatedEntity".
            // for simplicity reasons we only support one OSS
            secDec.setOtherRegulatedEntity(buildSet(convert(ci)));
            return true;
        }
        if ("AC".equals(ci.getCustomsInfoContentCode()) && secDec.getReceivedFrom() == null) {
            secDec.setReceivedFrom(convert(ci));
            return true;
        }
        if ("KC".equals(ci.getCustomsInfoContentCode()) && secDec.getReceivedFrom() == null) {
            secDec.setReceivedFrom(convert(ci));
            return true;
        }
        if ("ED".equals(ci.getCustomsInfoContentCode())) {
            // map expiry to a previous(!!) IncludedCustomsNote that matches
            try {
                int i = Integer.parseInt(ci.getCustomsInfoNote());
                int month = i / 100;
                Calendar cal = Calendar.getInstance();
                // ensure calculation to longer than the year 2100 ;-)
                int year = (cal.get(Calendar.YEAR) / 100 * 100) + i % 100;
                cal.clear();
                cal.set(year, month-1, 1);
                if (currentRegulatedEntity(secDec, previousCiSubjectCode) != null) {
                    currentRegulatedEntity(secDec, previousCiSubjectCode).setExpiryDate(cal.getTime());
                    return true;
                }
            } catch (NumberFormatException e) {
                // if the expiry "date" is not a integer, then we ignore it
            }
        }
        if ("L".equals(ci.getCustomsInfoContentCode())) {
            // Screening Exemption Code
            if (secDec.getGroundsForExemption() == null) {
                secDec.setGroundsForExemption(buildSet());
            }
            secDec.getGroundsForExemption().add(ci.getCustomsInfoNote());
            return true;
        }
        if ("SM".equals(ci.getCustomsInfoContentCode())) {
            // Screening Method
            if (secDec.getScreeningMethod() == null) {
                secDec.setScreeningMethod(buildSet());
            }
            secDec.getScreeningMethod().add(ci.getCustomsInfoNote());
            return true;
        }
        // the following checks apply independent from followin ISS or OSS or AC:
        if ("SN".equals(ci.getCustomsInfoContentCode())) {
        // operator issuing the security status
            secDec.setIssuedBy(OneRecordTypeConstants.createPerson());
            secDec.getIssuedBy().setLastName(ci.getCustomsInfoNote());
            return true;
        }
        // SD is an alphanumeric entry identifying the exact date and time when the security status was issued by the Regulated Agent
        // ddmmmyytttt
        if ("SD".equals(ci.getCustomsInfoContentCode())) {
            DateFormat df = new SimpleDateFormat("ddMMMyyHHmm", Locale.US);
            try {
                Date date = df.parse(ci.getCustomsInfoNote());
                secDec.setIssuedOn(date);
                return true;
            } catch (ParseException e) {
                // if timestamp is not parsable, then we ignore it
            }
        }
        if ("SS".equals(ci.getCustomsInfoContentCode())) {
            secDec.setSecurityStatus(ci.getCustomsInfoNote());
            return true;
        }
        if ("ST".equals(ci.getCustomsInfoContentCode())) {
            if (secDec.getAdditionalSecurityInformation() == null) {
                secDec.setAdditionalSecurityInformation(ci.getCustomsInfoNote());
            } else {
                secDec.setAdditionalSecurityInformation(
                    secDec.getAdditionalSecurityInformation() + "\n" + ci.getCustomsInfoNote()
                );
            }
            return true;
        }
        return false;
    }

    private RegulatedEntity currentRegulatedEntity(SecurityDeclaration secDec, String previousCiSubjectCode) {
        if ("ISS".equals(previousCiSubjectCode)) {
            return secDec.getRegulatedEntityIssuer();
        }
        if ("OSS".equals(previousCiSubjectCode)) {
            RegulatedEntity[] arr = new RegulatedEntity[1];
            secDec.getOtherRegulatedEntity().toArray(arr);
            return arr[0];
        }
        if ("KC".equals(previousCiSubjectCode)) {
            return secDec.getReceivedFrom();
        }
        return null;
    }

    private RegulatedEntity convert(CustomsInfo ci) {
        // Example: IE/ISS/RA/00084-01
        // Example: ///AC/12345ABCDE
        // CustomsInformation / CustomsInfoSubjectCode / CustomsInfoContentCode / CustomsInfoNote
        RegulatedEntity regulated = OneRecordTypeConstants.createRegulatedEntity();
        regulated.setRegulatedEntityCategory(ci.getCustomsInfoContentCode());
        Company company = OneRecordTypeConstants.createCompany();
        Address address = prepareCompanyAddress(company);
        Country country = OneRecordTypeConstants.createCountry();
        country.setCountryCode(ci.getCustomsInformation());
        address.setCountry(country);
        OtherIdentifier oi = OneRecordTypeConstants.createOtherIdentifier();
        oi.setOtherIdentifierType(regulated.getRegulatedEntityCategory());
        oi.setIdentifier(ci.getCustomsInfoNote());
        company.getBranch().setOtherIdentifiers(buildSet(oi));
        regulated.setRegulatedEntityIdentifier(company);
        return regulated;
    }

    // *************************************************************************

    private Party createParty(PartyRoleCode partyRole, Company company) {
        Party party = OneRecordTypeConstants.createParty();
        party.setPartyRole(partyRole);
        party.setPartyDetails(company);
        return party;
    }

    private Company convertCompany(ConsignorPartyType xmlPary,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = createCompany(xmlPary.getPostalStructuredAddress());
        return enhanceCompany(company,
            xmlPary.getName(),
            xmlPary.getAccountID(),
            xmlPary.getDefinedTradeContact(),
            xmlCustomsNotes);
    }

    private Company convertCompany(ConsigneePartyType xmlPary,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = createCompany(xmlPary.getPostalStructuredAddress());
        return enhanceCompany(company,
            xmlPary.getName(),
            xmlPary.getAccountID(),
            xmlPary.getDefinedTradeContact(),
            xmlCustomsNotes);
    }

    private Company convertCompany(AssociatedPartyType xmlPary,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = createCompany(xmlPary.getPostalStructuredAddress());
        return enhanceCompany(company,
            xmlPary.getName(),
            null,
            xmlPary.getDefinedTradeContact(),
            xmlCustomsNotes);
    }

    private Company convertCompany(FreightForwarderPartyType xmlAddress,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = createCompany(xmlAddress.getFreightForwarderAddress());
        return enhanceCompany(company,
            xmlAddress.getName(),
            xmlAddress.getAccountID(),
            xmlAddress.getDefinedTradeContact(),
            xmlCustomsNotes);
    }

    private Company createCompany(StructuredAddressType xmlAddress) {
        if (xmlAddress == null) {
            return null;
        }
        Company company = OneRecordTypeConstants.createCompany();
        Address address = prepareCompanyAddress(company);
        String street = value(xmlAddress.getStreetName());
        if (street != null) {
            address.setStreet(buildSet(street.split("\n")));
        }
        address.setCityName(value(xmlAddress.getCityName()));
        address.setPostalCode(value(xmlAddress.getPostcodeCode()));
        address.setCountry(value(xmlAddress.getCountryID(), xmlAddress.getCountryName()));
        return company;
    }

    private Company createCompany(FreightForwarderAddressType xmlAddress) {
        if (xmlAddress == null) {
            return null;
        }
        Company company = OneRecordTypeConstants.createCompany();
        Address address = prepareCompanyAddress(company);
        address.setStreet(buildSet(value(xmlAddress.getStreetName())));
        address.setCityName(value(xmlAddress.getCityName()));
        address.setPostalCode(value(xmlAddress.getPostcodeCode()));
        address.setCountry(value(xmlAddress.getCountryID(), xmlAddress.getCountryName()));
        return company;
    }

    private Address prepareCompanyAddress(Company company) {
        if (company.getBranch() == null) {
            company.setBranch(OneRecordTypeConstants.createCompanyBranch());
        }
        if (company.getBranch().getLocation() == null) {
            company.getBranch().setLocation(OneRecordTypeConstants.createLocation());
        }
        if (company.getBranch().getLocation().getAddress() == null) {
            company.getBranch().getLocation().setAddress(OneRecordTypeConstants.createAddress());
        }
        return company.getBranch().getLocation().getAddress();
    }

    private Contact createContact(ContactTypeCode type, String value) {
        Contact contact = OneRecordTypeConstants.createContact();
        contact.setContactType(type);
        contact.setContactValue(value);
        return contact;
    }

    private Company enhanceCompany(Company company, TextType xmlName, IDType xmlAccountID,
        List<TradeContactType> xmlContacts, List<CustomsNoteType> xmlPartyCustomsNotes)
    {
        if (company == null) {
            return null;
        }
        String name = value(xmlName);
        if (name != null && name.contains("\n")) {
            String[] array = name.split("\n");
            company.setCompanyName(array[0]);
            // company.getBranch().setBranchName(array[1]);
            company.getBranch().setBranchName(name);
        } else {
            company.setCompanyName(name);
            company.getBranch().setBranchName(name);
        }
        if (xmlAccountID != null) {
            String value = value(xmlAccountID);
            // filed as https://github.com/IATA-Cargo/ONE-Record/issues/130
            addWarning(VG_UNCERTAINTY,
                "Unclear where to put address-account-number '"
                + value + "', using OtherIdentifier with type '"
                + OneRecordTypeConstants.OTHER_IDENTIFIER_FORWARDER_ACCOUNT_NUMBER_AT_CARRIER
                + "' as workaround");
            OtherIdentifier oi = OneRecordTypeConstants.createOtherIdentifier();
            oi.setOtherIdentifierType(OneRecordTypeConstants.OTHER_IDENTIFIER_FORWARDER_ACCOUNT_NUMBER_AT_CARRIER);
            oi.setIdentifier(value);
            company.getBranch().setOtherIdentifiers(buildSet(oi));
        }

        final Person person = OneRecordTypeConstants.createPerson();
        String phone = null;
        String fax = null;
        String mail = null;
        boolean haveContact = false;
        if (xmlContacts != null) {
            for (TradeContactType tc : xmlContacts) {
                person.setLastName(value(tc.getPersonName()));
                if (tc.getDirectTelephoneCommunication() != null) {
                    haveContact = true;
                    phone = value(
                        tc.getDirectTelephoneCommunication().getCompleteNumber());
                }
                if (tc.getURIEmailCommunication() != null) {
                    haveContact = true;
                    mail = value(tc.getURIEmailCommunication().getURIID());
                }
                if (tc.getFaxCommunication() != null) {
                    haveContact = true;
                    fax = value(tc.getFaxCommunication().getCompleteNumber());
                }
            }
        }
        if (xmlPartyCustomsNotes != null) {
            for (CustomsNoteType customsNote : xmlPartyCustomsNotes) {
                if ("CT".equals(value(customsNote.getContentCode()))) {
                    // CT = Contact Telephone
                    haveContact = true;
                    phone = value(customsNote.getContent());
                } else
                if ("CP".equals(value(customsNote.getContentCode()))) {
                    // CP = Contact Person
                    haveContact = true;
                    person.setLastName(value(customsNote.getContent()));
                }
            }
        }
        if (haveContact) {
            person.setContact(buildSet());
            if (phone != null) {
                person.getContact().add(createContact(ContactTypeCode.PHONE, phone));
            }
            if (fax != null) {
                person.getContact().add(createContact(ContactTypeCode.FAX, fax));
            }
            if (mail != null) {
                person.getContact().add(createContact(ContactTypeCode.EMAIL, mail));
            }
            company.getBranch().setContactPersons(buildSet(person));
        }
        return company;
    }

    private List<CustomsNoteType> getCustomsNotesBySubjectCode(String subjectCode)
    {
        if (subjectCode == null) {
            return null;
        }
        return xmlMC.getIncludedCustomsNote().stream()
            .filter(icn -> subjectCode.equals(value(icn.getSubjectCode())))
            .collect(Collectors.toList());
    }

}
