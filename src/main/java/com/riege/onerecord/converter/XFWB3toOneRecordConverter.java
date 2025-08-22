package com.riege.onerecord.converter;

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

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.codelists.BillingChargeCode;
import org.iata.onerecord.cargo.codelists.ContactTypeCode;
import org.iata.onerecord.cargo.codelists.MovementIndicatorCode;
import org.iata.onerecord.cargo.codelists.OtherIdentifierTypeCode;
import org.iata.onerecord.cargo.codelists.PartyRoleCode;
import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.iata.onerecord.cargo.model.ActivitySequence;
import org.iata.onerecord.cargo.model.Address;
import org.iata.onerecord.cargo.model.Booking;
import org.iata.onerecord.cargo.model.BookingOption;
import org.iata.onerecord.cargo.model.BookingRequest;
import org.iata.onerecord.cargo.model.Carrier;
import org.iata.onerecord.cargo.model.ChargeIdentifier;
import org.iata.onerecord.cargo.model.CodeListElement;
import org.iata.onerecord.cargo.model.Company;
import org.iata.onerecord.cargo.model.ContactDetail;
import org.iata.onerecord.cargo.model.ContactDetailType;
import org.iata.onerecord.cargo.model.CurrencyCode;
import org.iata.onerecord.cargo.model.CurrencyValue;
import org.iata.onerecord.cargo.model.CustomsInformation;
import org.iata.onerecord.cargo.model.Dimensions;
import org.iata.onerecord.cargo.model.EntitlementCode;
import org.iata.onerecord.cargo.model.Insurance;
import org.iata.onerecord.cargo.model.Item;
import org.iata.onerecord.cargo.model.Location;
import org.iata.onerecord.cargo.model.ModeCode;
import org.iata.onerecord.cargo.model.ModeQualifier;
import org.iata.onerecord.cargo.model.MovementIndicator;
import org.iata.onerecord.cargo.model.MovementTime;
import org.iata.onerecord.cargo.model.OtherChargeCode;
import org.iata.onerecord.cargo.model.OtherIdentifier;
import org.iata.onerecord.cargo.model.ParticipantIdentifier;
import org.iata.onerecord.cargo.model.Party;
import org.iata.onerecord.cargo.model.Person;
import org.iata.onerecord.cargo.model.Piece;
import org.iata.onerecord.cargo.model.PrepaidCollectIndicator;
import org.iata.onerecord.cargo.model.Price;
import org.iata.onerecord.cargo.model.Product;
import org.iata.onerecord.cargo.model.Ranges;
import org.iata.onerecord.cargo.model.RateClassCode;
import org.iata.onerecord.cargo.model.Ratings;
import org.iata.onerecord.cargo.model.RegulatedEntity;
import org.iata.onerecord.cargo.model.RegulatedEntityCategoryCode;
import org.iata.onerecord.cargo.model.ScreeningExemption;
import org.iata.onerecord.cargo.model.ScreeningMethod;
import org.iata.onerecord.cargo.model.SecurityDeclaration;
import org.iata.onerecord.cargo.model.SecurityStatus;
import org.iata.onerecord.cargo.model.Shipment;
import org.iata.onerecord.cargo.model.SpecialHandlingCode;
import org.iata.onerecord.cargo.model.TransportMovement;
import org.iata.onerecord.cargo.model.ULD;
import org.iata.onerecord.cargo.model.Value;
import org.iata.onerecord.cargo.model.VolumetricWeight;
import org.iata.onerecord.cargo.model.Waybill;
import org.iata.onerecord.cargo.model.WaybillLineItem;
import org.iata.onerecord.cargo.util.ONERecordCargoUtil;

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
import static com.riege.onerecord.converter.XFWB3ParserHelper.createCodeListElementGeneral;
import static com.riege.onerecord.converter.XFWB3ParserHelper.determineModeCodeIRI;
import static com.riege.onerecord.converter.XFWB3ParserHelper.determineModeQualifierIRI;
import static com.riege.onerecord.converter.XFWB3ParserHelper.determinePartyRoleCodeIRI;
import static com.riege.onerecord.converter.XFWB3ParserHelper.determineSpecialHandlingCodeIRI;
import static com.riege.onerecord.converter.XFWB3ParserHelper.integerValue;
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
public final class XFWB3toOneRecordConverter extends CargoXMLtoOneRecordConverter<Waybill> {

    private final Waybill waybill;

    /**
     * Converter for CargoXML XFWB into a OneRecord (master or direct) Waybill.
     *
     * @param xfwb CargoXML XFWB
     */
    public XFWB3toOneRecordConverter(WaybillType xfwb) {
        super();

        waybill = ONERecordCargoUtil.create(Waybill.class);
        convertData(xfwb);
    }

    /**
     * @return converted OneRecord (master or direct) Waybill
     */
    @Override
    public Waybill getOneRecordResult() {
        return waybill;
    }

    // *************************************************************************

    // OneRecord helper instances
    private BookingOption mainBooking;
    private Booking mainBookingOption;
    private Carrier mainAirline;
    private Shipment mainShipment;
    private Piece mainPiece;

    // CargoXML shortcuts
    private MessageHeaderDocumentType xmlMH;
    private MasterConsignmentType xmlMC;
    private BusinessHeaderDocumentType xmlBH;
    private String awbCurrency;

    private void convertData(WaybillType xfwb) {
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
        mainBooking = ONERecordCargoUtil.create(BookingOption.class);
        mainBookingOption = ONERecordCargoUtil.create(Booking.class);
        waybill.setReferredBookingOption(mainBookingOption);
        mainBookingOption.setBookingRequest(ONERecordCargoUtil.create(BookingRequest.class));
        mainBookingOption.getBookingRequest().setForBookingOption(mainBooking);

        mainShipment = ONERecordCargoUtil.create(Shipment.class);
        waybill.setShipment(mainShipment);

        mainPiece = ONERecordCargoUtil.create(Piece.class);
        mainShipment.setPieces(ONERecordCargoUtil.buildSet(mainPiece));

        // Add the main carrier, as per AWB prefix
        mainAirline = ONERecordCargoUtil.create(Carrier.class);
        mainBooking.setCarrier(mainAirline);

        /*
         * initialize some shortcuts to main CargoXML elements
         */
        xmlMH = xfwb.getMessageHeaderDocument();
        xmlMC = xfwb.getMasterConsignment();
        xmlBH = xfwb.getBusinessHeaderDocument();

        if (xmlMC.getApplicableOriginCurrencyExchange() != null && xmlMC.getApplicableOriginCurrencyExchange().getSourceCurrencyCode() != null) {
            awbCurrency = value(xmlMC.getApplicableOriginCurrencyExchange().getSourceCurrencyCode());
            CurrencyCode awbCurrencyCLE = ONERecordCargoUtil.create(CurrencyCode.class);
            awbCurrencyCLE.setId(Vocabulary.s_c_CurrencyCode + "_" + awbCurrency);
            waybill.setOriginCurrency(awbCurrencyCLE);
        }

        /*
         * Conversion is split by topic of the individual CIMP segments,
         * just to allow easier navigation though source code.
         * CargoXML elements which have no CIMP mapping should get mapped
         * in the CIMP segment which matches best topic-wise.
         */
        /*
         * CIMPSegment27 is converted at the beginning because it might generate a hint
         * which makes sense to put as the first Hint in the validation result list.
         */
        convertCIMPSegment27();
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
        convertCIMPSegment29();
    }

    // *************************************************************************
    // CIMP FWB Segment 2: AWB Consignment Details (M)
    // *************************************************************************
    private void convertCIMPSegment02() {
        // Determine type of AWB ("Master" or "Direct")
        String typeCode = value(xmlMH.getTypeCode());
        org.iata.onerecord.cargo.model.WaybillType waybillType =
            ONERecordCargoUtil.create(org.iata.onerecord.cargo.model.WaybillType.class);
        if (typeCode == null) {
            addError(VG_XMLDATAERROR, "Missing TypeCode in MessageHeaderDocumentType");
        } else if ("740".equals(typeCode.trim())) {
            waybillType.setId(Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.DIRECT.code());
            waybill.setWaybillType(waybillType);
        } else if ("741".equals(typeCode.trim())) {
            waybillType.setId(Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.MASTER.code());
            waybill.setWaybillType(waybillType);
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
        mainAirline.setPrefix(waybill.getWaybillPrefix());

        // departure and destination
        waybill.setDepartureLocation(value(xmlMC.getOriginLocation()));
        waybill.setArrivalLocation(value(xmlMC.getFinalDestinationLocation()));

        // totalPieceCount
        // not available anymore on shipment
//        mainShipment.setTotalPieceCount(integerValue(xmlMC.getTotalPieceQuantity()));
        // -> would now be retrievable via sum of mainPiece.containedItems.itemQuantity.numericalValue

        // totalGrossWeight
        mainShipment.setTotalGrossWeight(value(xmlMC.getIncludedTareGrossWeightMeasure()));
        mainPiece.setGrossWeight(mainShipment.getTotalGrossWeight());
        addHint(VG_INFORMATION, "(Total)GrossWeight is mandatory on Shipment and on Piece, value from MasterConsignment/IncludedTareGrossWeightMeasure is used for both");

        VolumetricWeight volumetricWeight = ONERecordCargoUtil.create(VolumetricWeight.class);
        volumetricWeight.setChargeableWeight(mainShipment.getTotalGrossWeight());
        mainShipment.setVolumetricWeight(ONERecordCargoUtil.buildSet(volumetricWeight));

        // totalVolume
        if (xmlMC.getGrossVolumeMeasure() != null) {
            Dimensions volume = ONERecordCargoUtil.create(Dimensions.class);
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
                    ts.setDepartureDate(gregorianCalendarToOffsetDateTime(ltm.getDepartureEvent().getScheduledOccurrenceDateTime()));
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
        // Flights are now mapped as ActivitySequence on mainBookingOption
        Set<ActivitySequence> flights = ONERecordCargoUtil.buildSet();
        for (LogisticsTransportMovementType ltm : xmlMC.getSpecifiedLogisticsTransportMovement()) {
            ActivitySequence seq = ONERecordCargoUtil.create(ActivitySequence.class);
            TransportMovement tm = ONERecordCargoUtil.create(TransportMovement.class);
            seq.setSequenceNumber(String.valueOf(ltm.getSequenceNumeric()));
            // ModeCode is the same in 1R+XFWB, so we apply 1:1
            // In XFWB, ModeCode="4" means "Air transport"
            // In XFWB, there is also a ltm.getMode() with value "Air transport"
            if (ltm.getModeCode() != null) {
                ModeCode modeCode = ONERecordCargoUtil.create(ModeCode.class);
                String modeCodeIRI = determineModeCodeIRI(ltm.getModeCode().getValue());
                modeCode.setId(modeCodeIRI);
                tm.setModeCode(modeCode);
            }
            ModeQualifier modeQualifier = ONERecordCargoUtil.create(ModeQualifier.class);
            String modeQualifierIRI = determineModeQualifierIRI(value(ltm.getStageCode()));
            modeQualifier.setId(modeQualifierIRI);
            tm.setModeQualifier(modeQualifier);
            if (ltm.getUsedLogisticsTransportMeans() != null) {
            // this is supposed to be set on TransportMovement#operatingParties -> Party.partyDetails -> Carrier#airlineCode
            // this seems to be missing in 3.0.0 but got added in 3.1.1
//                tm.setCompanyIdentifier(value(ltm.getUsedLogisticsTransportMeans().getName()));
            }
            if (ltm.getDepartureEvent() != null) {
                tm.setDepartureLocation(
                    value(ltm.getDepartureEvent().getOccurrenceDepartureLocation()));
                if (ltm.getDepartureEvent().getScheduledOccurrenceDateTime() == null) {
                    tm.setTransportIdentifier(value(ltm.getID()));
                } else {
                    MovementTime mt = ONERecordCargoUtil.create(MovementTime.class);
                    mt.setMovementTimestamp(
                        convertToOffsetDateTime(ltm.getDepartureEvent().getScheduledOccurrenceDateTime()));
                    MovementIndicator movementIndicator =
                        ONERecordCargoUtil.create(MovementIndicator.class);
                    movementIndicator.setCode(MovementIndicatorCode.SCHEDULED_DEPARTURE.code());
                    mt.setMovementMilestone(movementIndicator);
                    // Ontology v1.1:
                    //int day = ltm.getDepartureEvent().getScheduledOccurrenceDateTime().toGregorianCalendar().get(Calendar.DAY_OF_MONTH);
                    //tm.setTransportIdentifier(value(ltm.getID()) + String.format("/%02d", day));
                    // Ontology v1.2:
                    tm.setTransportIdentifier(value(ltm.getID()));
                    tm.setMovementTimes(ONERecordCargoUtil.buildSet(mt));
                }
            }
            if (ltm.getArrivalEvent() != null) {
                tm.setArrivalLocation(
                    value(ltm.getArrivalEvent().getOccurrenceArrivalLocation()));
            }
            seq.setActivity(tm);
            flights.add(seq);
        }
        if (!flights.isEmpty()) {
            mainBookingOption.setActivitySequences(flights);
        }
    }

    // *************************************************************************
    // CIMP FWB Segment 5: Shipper (M)
    // *************************************************************************
    private void convertCIMPSegment05() {
        if (mainShipment.getInvolvedParties() == null) {
            mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
        }
        mainShipment.getInvolvedParties().add(createParty(
            PartyRoleCode.SHP,
            xmlMC.getConsignorParty(),
            getCustomsNotesBySubjectCode("SHP")));
    }

    // *************************************************************************
    // CIMP FWB Segment 6: Consignee (M)
    // *************************************************************************
    private void convertCIMPSegment06() {
        if (mainShipment.getInvolvedParties() == null) {
            mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
        }
        mainShipment.getInvolvedParties().add(createParty(
            PartyRoleCode.CNE,
            xmlMC.getConsigneeParty(),
            getCustomsNotesBySubjectCode("CNE")));
    }

    // *************************************************************************
    // CIMP FWB Segment 7: (Export) Agent (C: if entitled to commission)
    // *************************************************************************
    private void convertCIMPSegment07() {

        FreightForwarderPartyType xmlForwarder = xmlMC.getFreightForwarderParty();
        if (xmlForwarder != null) {
            if (mainShipment.getInvolvedParties() == null) {
                mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
            }
            Party ffw = createParty(
                PartyRoleCode.FFW,
                xmlForwarder,
                getCustomsNotesBySubjectCode("AGT"));
            mainShipment.getInvolvedParties().add(ffw);

            String value;
            value = value(xmlForwarder.getCargoAgentID());
            Company partyDetails = (Company) ffw.getPartyDetails();
            if (value != null) {
                if (!value.matches("[0-9]+")) {
                    addError(VG_XMLDATAERROR,
                        "FreightForwarderParty/CargoAgentID '" + value + "' not matching expected number format");
                } else {
                    partyDetails.setIataCargoAgentCode(value);
                }
            }
            if (xmlForwarder.getSpecifiedCargoAgentLocation() != null) {
                value = value(xmlForwarder.getSpecifiedCargoAgentLocation().getID());
                if (!value.matches("[0-9]+")) {
                    addError(VG_XMLDATAERROR,
                        "FreightForwarderParty/SpecifiedCargoAgentLocation/ID '" + value + "' not matching expected number format");
                } else {
                    partyDetails.setIataCargoAgentLocationIdentifier(value);
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
        // 2022-May Ontology:
        // use mainPiece.getHandlingInstructions(...) instead of ServiceRequest
        // See https://github.com/IATA-Cargo/ONE-Record/issues/134
        for (SSRInstructionsType xmlInstr : xmlMC.getHandlingSSRInstructions()) {
            if (mainPiece.getHandlingInstructions() == null) {
                mainPiece.setHandlingInstructions(ONERecordCargoUtil.buildSet());
            }
            HandlingInstructions hi = ONERecordCargoUtil.create(HandlingInstructions.class);
            hi.setServiceType(HandlingInstructionsServiceTypeCode.SSR.code());
            if (xmlInstr.getDescriptionCode() != null) {
                hi.setServiceType(value(xmlInstr.getDescriptionCode()));
            }
            hi.setServiceDescription(value(xmlInstr.getDescription()));
            mainPiece.getHandlingInstructions().add(hi);
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
            if (mainShipment.getInvolvedParties() == null) {
                mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
            }
            for (AssociatedPartyType party : xmlNotifies) {
                String xmlRoleCode = party.getRoleCode() != null && party.getRoleCode().getValue() != null
                    ? party.getRoleCode().getValue().value()
                    : null;
                if ("NI".equals(xmlRoleCode)) {
                    mainShipment.getInvolvedParties().add(createParty(
                        PartyRoleCode.NFY, party, getCustomsNotesBySubjectCode(xmlRoleCode)
                    ));
                } else if ("FB".equals(xmlRoleCode)) {
                    mainShipment.getInvolvedParties().add(createParty(
                        PartyRoleCode.NOM, party, getCustomsNotesBySubjectCode(xmlRoleCode)
                    ));
                } else if ("OJ".equals(xmlRoleCode)) {
                    mainShipment.getInvolvedParties().add(createParty(
                        PartyRoleCode.OPI, party, getCustomsNotesBySubjectCode(xmlRoleCode)
                    ));
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
        Insurance insurance = ONERecordCargoUtil.create(Insurance.class);
        insurance.setNvdIndicator(xmlMC.isNilInsuranceValueIndicator());
        boolean isNilInsurance = xmlMC.isNilInsuranceValueIndicator() != null && xmlMC.isNilInsuranceValueIndicator();
        if (!isNilInsurance) {
            insurance.setInsuranceAmount(value(xmlMC.getInsuranceValueAmount(), awbCurrency));
        }
        mainShipment.setInsurance(insurance);

        mainPiece.setNvdForCarriage(xmlMC.isNilCarriageValueIndicator());
        boolean isNilCarriage = xmlMC.isNilCarriageValueIndicator() != null && xmlMC.isNilCarriageValueIndicator();
        if (!isNilCarriage) {
            mainPiece.setDeclaredValueForCarriage(
                xmlMC.getDeclaredValueForCarriageAmount().getValue().toString()
            );
        }

        mainPiece.setNvdForCustoms(xmlMC.isNilCustomsValueIndicator());
        boolean isNilCustoms = xmlMC.isNilCustomsValueIndicator() != null && xmlMC.isNilCustomsValueIndicator();
        if (!isNilCustoms) {
            mainPiece.setDeclaredValueForCustoms(
                xmlMC.getDeclaredValueForCustomsAmount().getValue().toString()
            );
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
                    mainPiece.setProduct(ONERecordCargoUtil.buildSet());
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
                    Dimensions dim1R = ONERecordCargoUtil.create(Dimensions.class);
                    dim1R.setHeight(value(xmlDim.getHeightMeasure()));
                    dim1R.setLength(value(xmlDim.getLengthMeasure()));
                    dim1R.setWidth(value(xmlDim.getWidthMeasure()));
                    Item item = ONERecordCargoUtil.create(Item.class);
                    // "Product" is mandatory for item as per Ontology
                    item.setProduct(ONERecordCargoUtil.create(Product.class));
                    item.setDimensions(dim1R);
                    item.setWeight(value(lp.getGrossWeightMeasure()));
                    Value count = ONERecordCargoUtil.create(Value.class);
                    count.setValue(Double.valueOf(xmlPackageCount));
                    item.setQuantity(count);
                    allDims.add(item);
                }

                for (UnitLoadTransportEquipmentType xmlULD : mci.getAssociatedUnitLoadTransportEquipment()) {
                    ULD uld1R = ONERecordCargoUtil.create(ULD.class);
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
                        mainRateDescription = ONERecordCargoUtil.create(Ratings.class);
                        // chargeType is unresticted free text in Ontology 1.2
                        mainRateDescription.setChargeType("Freight");
                        mainRateDescription.setRanges(ONERecordCargoUtil.buildSet());
                        mainBooking.setPrice(ONERecordCargoUtil.create(Price.class));
                        mainBooking.getPrice().setRatings(
                            ONERecordCargoUtil.buildSet(mainRateDescription));
                        // "Rate class" only fits into "in Ranges/rateClassCode"
                        // The only place where a "Ranges" is available is a "Ratings"
                        // "Ratings" is only available in "Price" or "Request"
                        // Neither "OtherCharges" nor "RateDescriptions" directly belong to a (booking)-Request
                        // Therefore we add them to a Price of the Booking which already exists.
                    }
                    // note: it might be a (Q)uantity rate or (M)inimum rate or other!
                    Ranges rate1R = ONERecordCargoUtil.create(Ranges.class);
                    rate1R.setRateClassCode(value(xmlRate.getCategoryCode()));
                    if (xmlRate.getCommodityItemID() != null) {
                        Product product = ONERecordCargoUtil.create(Product.class);
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
                mainPiece.setContainedItems(ONERecordCargoUtil.buildSet());
            }
            for (String hsCode : hts) {
                Item item = ONERecordCargoUtil.create(Item.class);
                item.setProduct(ONERecordCargoUtil.create(Product.class));
                item.getProduct().setHsCode(hsCode);
                mainPiece.getContainedItems().add(item);
            }
        }
        // Dimensions
        if (!allDims.isEmpty()) {
            if (mainPiece.getContainedItems() == null) {
                mainPiece.setContainedItems(ONERecordCargoUtil.buildSet());
            }
            mainPiece.getContainedItems().addAll(allDims);
        }
        // ULDs
        if (!allULD.isEmpty()) {
            for (TransportMovement tm : mainPiece.getTransportMovements()) {
                tm.setTransportedUlds(ONERecordCargoUtil.buildSet(allULD));
            }
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
        // In 2022-May Ontology, the field becomes optional:
        //if (!xmlMC.getApplicableLogisticsAllowanceCharge().isEmpty()) {
        //    addWarning(VG_INFORMATION,
        //        "For other charges determined from \"<ApplicableLogisticsAllowanceCharge>\", "
        //            + "we apply a 1R charge type value \"Surcharge\"");
        //}
        for (LogisticsAllowanceChargeType xmlLAC : xmlMC.getApplicableLogisticsAllowanceCharge()) {
            Ratings otherCharge = ONERecordCargoUtil.create(Ratings.class);
            // Ratings:chargeType is unresticted free text in Ontology 1.2
            // In 2022-May Ontology, the field becomes optional, we skip it:
            // https://github.com/IATA-Cargo/ONE-Record/issues/92#issuecomment-1041301746
            // otherCharge.setChargeType("Surcharge");
            otherCharge.setOtherChargeCode(value(xmlLAC.getID()));
            // "due" (aka Entitlement) in Ratings as per
            // #116 https://github.com/IATA-Cargo/ONE-Record/issues/116
            otherCharge.setEntitlement(value(xmlLAC.getPartyTypeCode()));
            otherCharge.setChargePaymentType(xmlLAC.isPrepaidIndicator() ? "P" : "C");

            Value amount = value(xmlLAC.getActualAmount(), awbCurrency);
            otherCharge.setSubTotal(amount.getValue().doubleValue());
            if (amount.getUnit() != null) {
                Ranges ranges = ONERecordCargoUtil.create(Ranges.class);
                ranges.setUnitBasis(amount.getUnit());
                otherCharge.setRanges(ONERecordCargoUtil.buildSet(ranges));
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
        Ratings totalAmount = ONERecordCargoUtil.create(Ratings.class);
        totalAmount.setBillingChargeIdentifier(type.code());
        totalAmount.setChargePaymentType(isPrepaid ? "P" : "C");
        totalAmount.setSubTotal(value.getValue().doubleValue());
        if (value.getUnit() != null) {
            Ranges ranges = ONERecordCargoUtil.create(Ranges.class);
            ranges.setUnitBasis(value.getUnit());
            totalAmount.setRanges(ONERecordCargoUtil.buildSet(ranges));
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
            waybill.setCarrierDeclarationDate(convertToOffsetDateTime(sigCarrier.getActualDateTime()));
            AuthenticationLocationType location = sigCarrier.getIssueAuthenticationLocation();
            if (location != null) {
                waybill.setCarrierDeclarationPlace(ONERecordCargoUtil.create(Location.class));
                waybill.getCarrierDeclarationPlace().setCode(value(location.getName()));
            }
        }
        ConsignorAuthenticationType sigConsignor = xmlBH.getSignatoryConsignorAuthentication();
        if (sigConsignor != null) {
            String singleEntry = value(sigConsignor.getSignatory());
            waybill.setConsignorDeclarationSignature(ONERecordCargoUtil.buildSet(singleEntry));
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
        for (OSIInstructionsType xmlInstr : xmlMC.getHandlingOSIInstructions()) {
            // 2022-May Ontology:
            // use mainPiece.getHandlingInstructions(...) instead of ServiceRequest
            // See https://github.com/IATA-Cargo/ONE-Record/issues/134
            if (mainPiece.getHandlingInstructions() == null) {
                mainPiece.setHandlingInstructions(ONERecordCargoUtil.buildSet());
            }
            HandlingInstructions hi = ONERecordCargoUtil.create(HandlingInstructions.class);
            hi.setServiceType(HandlingInstructionsServiceTypeCode.OSI.code());
            if (xmlInstr.getDescriptionCode() != null) {
                hi.setServiceType(value(xmlInstr.getDescriptionCode()));
            }
            hi.setServiceDescription(value(xmlInstr.getDescription()));
            mainPiece.getHandlingInstructions().add(hi);
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
            // As per https://github.com/IATA-Cargo/ONE-Record/issues/135
            waybill.setCustomsOriginCode(value(xmlMC.getAssociatedConsignmentCustomsProcedure().getGoodsStatusCode()));
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
                // 2022-May Ontology:
                // use mainPiece.getHandlingInstructions(...) instead of ServiceRequest
                // See https://github.com/IATA-Cargo/ONE-Record/issues/134
                if (mainPiece.getHandlingInstructions() == null) {
                    mainPiece.setHandlingInstructions(ONERecordCargoUtil.buildSet());
                }
                HandlingInstructions hi = ONERecordCargoUtil.create(HandlingInstructions.class);
                hi.setServiceType(HandlingInstructionsServiceTypeCode.SPH.code());
                hi.setServiceTypeCode(value(xmlInstr.getDescriptionCode()));
                mainPiece.getHandlingInstructions().add(hi);
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
        // See also XFZB3toOneRecordConverter#convertCIMPSegment06
        // keep in sync!
        if (isNullOrEmpty(xmlMC.getIncludedCustomsNote())) {
            return;
        }

        SecurityDeclaration secDec = ONERecordCargoUtil.create(SecurityDeclaration.class);
        String previousCiSubjectCode = "X";
        boolean haveSecDec = false;
        boolean haveCTCP = false;
        for (CustomsNoteType xmlCustNote : xmlMC.getIncludedCustomsNote()) {
            /*
             * Example:
             *   <ContentCode>CP</ContentCode>
             *   <Content>HILDA HILARIOUS</Content>
             *   <SubjectCode>SHP</SubjectCode>
             *   <CountryID>IE</CountryID>
             */
            String contentCode = value(xmlCustNote.getContentCode());
            String contentText = value(xmlCustNote.getContent());
            String subjectCode = value(xmlCustNote.getSubjectCode());
            String countryCode = xmlCustNote.getCountryID() == null
                    ? null
                    : value(xmlCustNote.getCountryID(), null).getCountryCode();
            CustomsInfo custInfo = ONERecordCargoUtil.create(CustomsInfo.class);

            custInfo.setCustomsInfoContentCode(contentCode);
            custInfo.setCustomsInfoCountryCode(countryCode);
            // data field "customsInfoNote":
            // Free text for customs remarks, not used in OCI Composition Rules Table
            custInfo.setCustomsInfoSubjectCode(subjectCode);
            custInfo.setCustomsInformation(contentText);

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
                    + (countryCode == null ? "" : countryCode)
                    + "/"
                    + (subjectCode == null ? "" : subjectCode)
                    + "/"
                    + (contentCode == null ? "" : contentCode)
                    + "/"
                    + (contentText == null ? "" : contentText)
                    + "' transformed into SecurityStatus."
                );
            } else {
                if (mainPiece.getCustomsInfo() == null) {
                    mainPiece.setCustomsInfo(ONERecordCargoUtil.buildSet());
                }
                mainPiece.getCustomsInfo().add(custInfo);
            }
            if (subjectCode != null) {
                previousCiSubjectCode = subjectCode;
            }
        }
        if (haveSecDec) {
            mainPiece.setSecurityDeclaration(secDec);
        }
    }

    static boolean updateSecurityDeclaration(CustomsInfo ci, SecurityDeclaration secDec,
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
            RegulatedEntity singleEntry = convert(ci);
            secDec.setOtherRegulatedEntity(ONERecordCargoUtil.buildSet(singleEntry));
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
                int i = Integer.parseInt(ci.getCustomsInformation());
                int month = i / 100;
                Calendar cal = Calendar.getInstance();
                // ensure calculation to longer than the year 2100 ;-)
                int year = (cal.get(Calendar.YEAR) / 100 * 100) + i % 100;
                cal.clear();
                cal.set(year, month-1, 1);
                if (currentRegulatedEntity(secDec, previousCiSubjectCode) != null) {
                    currentRegulatedEntity(secDec, previousCiSubjectCode)
                        .setExpiryDate(convertToOffsetDateTime(cal));
                    return true;
                }
            } catch (NumberFormatException e) {
                // if the expiry "date" is not a integer, then we ignore it
            }
        }
        if ("L".equals(ci.getCustomsInfoContentCode())) {
            // Screening Exemption Code
            if (secDec.getGroundsForExemption() == null) {
                secDec.setGroundsForExemption(ONERecordCargoUtil.buildSet());
            }
            secDec.getGroundsForExemption().add(ci.getCustomsInformation());
            return true;
        }
        if ("SM".equals(ci.getCustomsInfoContentCode())) {
            // Screening Method
            if (secDec.getScreeningMethod() == null) {
                secDec.setScreeningMethod(ONERecordCargoUtil.buildSet());
            }
            secDec.getScreeningMethod().add(ci.getCustomsInformation());
            return true;
        }
        // the following checks apply independent from followin ISS or OSS or AC:
        if ("SN".equals(ci.getCustomsInfoContentCode())) {
        // operator issuing the security status
            secDec.setIssuedBy(ONERecordCargoUtil.create(Person.class));
            secDec.getIssuedBy().setLastName(ci.getCustomsInformation());
            return true;
        }
        // SD is an alphanumeric entry identifying the exact date and time when the security status was issued by the Regulated Agent
        // ddmmmyytttt
        if ("SD".equals(ci.getCustomsInfoContentCode())) {
            DateFormat df = new SimpleDateFormat("ddMMMyyHHmm", Locale.US);
            try {
                Date date = df.parse(ci.getCustomsInformation());
                secDec.setIssuedOn(convertToOffsetDateTime(date));
                return true;
            } catch (ParseException e) {
                // if timestamp is not parsable, then we ignore it
            }
        }
        if ("SS".equals(ci.getCustomsInfoContentCode())) {
            secDec.setSecurityStatus(ci.getCustomsInformation());
            return true;
        }
        if ("ST".equals(ci.getCustomsInfoContentCode())) {
            if (secDec.getAdditionalSecurityInformation() == null) {
                secDec.setAdditionalSecurityInformation(ci.getCustomsInformation());
            } else {
                secDec.setAdditionalSecurityInformation(
                    secDec.getAdditionalSecurityInformation() + "\n" + ci.getCustomsInformation()
                );
            }
            return true;
        }
        return false;
    }

    static RegulatedEntity currentRegulatedEntity(SecurityDeclaration secDec, String previousCiSubjectCode) {
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

    static RegulatedEntity convert(CustomsInfo ci) {
        // Example: IE/ISS/RA/00084-01
        // Example: ///AC/12345ABCDE
        // CustomsInformation / CustomsInfoSubjectCode / CustomsInfoContentCode / CustomsInfoNote
        RegulatedEntity regulated = ONERecordCargoUtil.create(RegulatedEntity.class);
        regulated.setRegulatedEntityCategory(ci.getCustomsInfoContentCode());
        Company company = ONERecordCargoUtil.create(Company.class);
        company.setBranch(ONERecordCargoUtil.create(CompanyBranch.class));
        OtherIdentifier oi = ONERecordCargoUtil.create(OtherIdentifier.class);
        oi.setOtherIdentifierType(regulated.getRegulatedEntityCategory());
        oi.setIdentifier(ci.getCustomsInformation());
        company.getBranch().setOtherIdentifiers(ONERecordCargoUtil.buildSet(oi));
        regulated.setRegulatedEntityIdentifier(company);
        return regulated;
    }

    // *************************************************************************

    private Party createParty(PartyRoleCode partyRole,
        ConsignorPartyType xmlParty,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = enhanceCompany(
            partyRole,
            createCompany(xmlParty.getPostalStructuredAddress()),
            xmlParty.getName(),
            xmlParty.getDefinedTradeContact(),
            xmlCustomsNotes);
        String accountID = xmlParty.getAccountID() != null
            ? value(xmlParty.getAccountID())
            : null;
        return createParty(partyRole, company, accountID);
    }

    private Party createParty(PartyRoleCode partyRole,
        ConsigneePartyType xmlParty,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = enhanceCompany(
            partyRole,
            createCompany(xmlParty.getPostalStructuredAddress()),
            xmlParty.getName(),
            xmlParty.getDefinedTradeContact(),
            xmlCustomsNotes);
        String accountID = xmlParty.getAccountID() != null
            ? value(xmlParty.getAccountID())
            : null;
        return createParty(partyRole, company, accountID);
    }

    private Party createParty(PartyRoleCode partyRole,
        FreightForwarderPartyType xmlParty,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = enhanceCompany(
            partyRole,
            createCompany(xmlParty.getFreightForwarderAddress()),
            xmlParty.getName(),
            xmlParty.getDefinedTradeContact(),
            xmlCustomsNotes);
        String accountID = xmlParty.getAccountID() != null
            ? value(xmlParty.getAccountID())
            : null;
        return createParty(partyRole, company, accountID);
    }

    private Party createParty(PartyRoleCode partyRole,
        AssociatedPartyType xmlParty,
        List<CustomsNoteType> xmlCustomsNotes)
    {
        Company company = enhanceCompany(
            partyRole,
            createCompany(xmlParty.getPostalStructuredAddress()),
            xmlParty.getName(),
            xmlParty.getDefinedTradeContact(),
            xmlCustomsNotes);
        return createParty(partyRole, company, null);
    }

    private Party createParty(PartyRoleCode partyRole, Company company, String accountID) {
        Party party = ONERecordCargoUtil.create(Party.class);
        ParticipantIdentifier pi = ONERecordCargoUtil.create(ParticipantIdentifier.class);
        pi.setId(determinePartyRoleCodeIRI(partyRole.code()));
        party.setPartyRole(pi);
        party.setPartyDetails(company);
        if (accountID != null) {
            // See https://github.com/IATA-Cargo/ONE-Record/issues/130
            OtherIdentifier oi = ONERecordCargoUtil.create(OtherIdentifier.class);
            oi.setOtherIdentifierType(OtherIdentifierTypeCode.ACCOUNT_ID.code());
            oi.setOtherIdentifierType("AccountID");
            oi.setTextualValue(accountID);
            party.setOtherIdentifiers(ONERecordCargoUtil.buildSet(oi));
        }
        return party;
    }

    private Company createCompany(StructuredAddressType xmlAddress) {
        if (xmlAddress == null) {
            return null;
        }
        Company company = ONERecordCargoUtil.create(Company.class);
        Address address = prepareCompanyAddress(company);
        String street = value(xmlAddress.getStreetName());
        if (street != null) {
            address.setStreet(ONERecordCargoUtil.buildSet(street.split("\n")));
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
        Company company = ONERecordCargoUtil.create(Company.class);
        Address address = prepareCompanyAddress(company);
        String street = value(xmlAddress.getStreetName());
        if (street != null) {
            address.setStreet(ONERecordCargoUtil.buildSet(street.split("\n")));
        }
        address.setCityName(value(xmlAddress.getCityName()));
        address.setPostalCode(value(xmlAddress.getPostcodeCode()));
        address.setCountry(value(xmlAddress.getCountryID(), xmlAddress.getCountryName()));
        return company;
    }

    static Address prepareCompanyAddress(Company company) {
        if (company.getBranch() == null) {
            company.setBranch(ONERecordCargoUtil.create(CompanyBranch.class));
        }
        if (company.getBranch().getLocation() == null) {
            company.getBranch().setLocation(ONERecordCargoUtil.create(Location.class));
        }
        if (company.getBranch().getLocation().getAddress() == null) {
            company.getBranch().getLocation().setAddress(ONERecordCargoUtil.create(Address.class));
        }
        return company.getBranch().getLocation().getAddress();
    }

    private Contact createContact(ContactTypeCode type, String value) {
        Contact contact = ONERecordCargoUtil.create(Contact.class);
        contact.setContactType(type.code());
        contact.setContactValue(value);
        return contact;
    }

    private Company enhanceCompany(PartyRoleCode partyRole,
        Company company, TextType xmlName,
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

        final Person person = ONERecordCargoUtil.create(Person.class);
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
                    if (phone != null) {
                        addHint(VG_INFORMATION,
                            "IncludedCustomsNote 'CT' (Contact Telephone) got preferred over <DefinedTradeContact><DirectTelephoneCommunication> for "
                                + partyRole.code());
                    }
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
            person.setContact(ONERecordCargoUtil.buildSet());
            if (phone != null) {
                person.getContact().add(createContact(ContactTypeCode.PHONE, phone));
            }
            if (fax != null) {
                person.getContact().add(createContact(ContactTypeCode.FAX, fax));
            }
            if (mail != null) {
                person.getContact().add(createContact(ContactTypeCode.EMAIL, mail));
            }
            company.getBranch().setContactPersons(ONERecordCargoUtil.buildSet(person));
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
