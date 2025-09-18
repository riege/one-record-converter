package com.riege.onerecord.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.codelists.ContactTypeCode;
import org.iata.onerecord.cargo.codelists.OtherIdentifierTypeCode;
import org.iata.onerecord.cargo.codelists.PartyRoleCode;
import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.iata.onerecord.cargo.model.Address;
import org.iata.onerecord.cargo.model.Booking;
import org.iata.onerecord.cargo.model.BookingOption;
import org.iata.onerecord.cargo.model.BookingRequest;
import org.iata.onerecord.cargo.model.Carrier;
import org.iata.onerecord.cargo.model.CodeListElement;
import org.iata.onerecord.cargo.model.Company;
import org.iata.onerecord.cargo.model.ContactDetail;
import org.iata.onerecord.cargo.model.ContactDetailType;
import org.iata.onerecord.cargo.model.CustomsInformation;
import org.iata.onerecord.cargo.model.Dimensions;
import org.iata.onerecord.cargo.model.Insurance;
import org.iata.onerecord.cargo.model.Item;
import org.iata.onerecord.cargo.model.Location;
import org.iata.onerecord.cargo.model.OtherIdentifier;
import org.iata.onerecord.cargo.model.ParticipantIdentifier;
import org.iata.onerecord.cargo.model.Party;
import org.iata.onerecord.cargo.model.Person;
import org.iata.onerecord.cargo.model.Piece;
import org.iata.onerecord.cargo.model.Product;
import org.iata.onerecord.cargo.model.SecurityDeclaration;
import org.iata.onerecord.cargo.model.Shipment;
import org.iata.onerecord.cargo.model.ULD;
import org.iata.onerecord.cargo.model.Value;
import org.iata.onerecord.cargo.model.VolumetricWeight;
import org.iata.onerecord.cargo.model.Waybill;
import org.iata.onerecord.cargo.model.WaybillLineItem;
import org.iata.onerecord.cargo.util.ONERecordCargoUtil;

import com.riege.cargoxml.schema.xfzb3.AuthenticationLocationType;
import com.riege.cargoxml.schema.xfzb3.BusinessHeaderDocumentType;
import com.riege.cargoxml.schema.xfzb3.CarrierAuthenticationType;
import com.riege.cargoxml.schema.xfzb3.CodeType;
import com.riege.cargoxml.schema.xfzb3.ConsigneePartyType;
import com.riege.cargoxml.schema.xfzb3.ConsignorAuthenticationType;
import com.riege.cargoxml.schema.xfzb3.ConsignorPartyType;
import com.riege.cargoxml.schema.xfzb3.CustomsNoteType;
import com.riege.cargoxml.schema.xfzb3.HouseConsignmentItemType;
import com.riege.cargoxml.schema.xfzb3.HouseConsignmentType;
import com.riege.cargoxml.schema.xfzb3.HouseWaybillType;
import com.riege.cargoxml.schema.xfzb3.LogisticsPackageType;
import com.riege.cargoxml.schema.xfzb3.MasterConsignmentType;
import com.riege.cargoxml.schema.xfzb3.MessageHeaderDocumentType;
import com.riege.cargoxml.schema.xfzb3.SpatialDimensionType;
import com.riege.cargoxml.schema.xfzb3.StructuredAddressType;
import com.riege.cargoxml.schema.xfzb3.TextType;
import com.riege.cargoxml.schema.xfzb3.TradeContactType;
import com.riege.cargoxml.schema.xfzb3.UnitLoadTransportEquipmentType;

import static com.riege.onerecord.converter.ConverterUtil.isNullOrEmpty;
import static com.riege.onerecord.converter.XFWB3ParserHelper.determinePartyRoleCodeIRI;
import static com.riege.onerecord.converter.XFWB3toOneRecordConverter.checkPieceQuantityAgainstItemCount;
import static com.riege.onerecord.converter.XFWB3toOneRecordConverter.updateSecurityDeclaration;
import static com.riege.onerecord.converter.XFZB3ParserHelper.createCodeListElementGeneral;
import static com.riege.onerecord.converter.XFZB3ParserHelper.integerValue;
import static com.riege.onerecord.converter.XFZB3ParserHelper.value;

public class XFZB3toOneRecordConverter extends CargoXMLtoOneRecordConverter<Waybill> {

    private final Waybill waybill;

    public XFZB3toOneRecordConverter(HouseWaybillType xfzb) {
        super();

        waybill = ONERecordCargoUtil.create(Waybill.class);
        convertData(xfzb);
    }

    @Override
    public Waybill getOneRecordResult() {
        return waybill;
    }

    private BookingOption mainBooking;
    private Carrier mainAirline;
    private Shipment mainShipment;
    private Piece mainPiece;

    // CargoXML shortcuts
    private MessageHeaderDocumentType xmlMH;
    private MasterConsignmentType xmlMC;
    private BusinessHeaderDocumentType xmlBH;

    private HouseConsignmentType xmlHouse;
    private void convertData(HouseWaybillType xfzb) {

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
        waybill.setReferredBookingOption(ONERecordCargoUtil.create(Booking.class));
        waybill.getReferredBookingOption().setBookingRequest(ONERecordCargoUtil.create(BookingRequest.class));
        waybill.getReferredBookingOption().getBookingRequest().setForBookingOption(mainBooking);

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
        xmlMH = xfzb.getMessageHeaderDocument();
        xmlMC = xfzb.getMasterConsignment();
        xmlBH = xfzb.getBusinessHeaderDocument();
        xmlHouse = xmlMC.getIncludedHouseConsignment();

        org.iata.onerecord.cargo.model.WaybillType waybillType =
            ONERecordCargoUtil.create(org.iata.onerecord.cargo.model.WaybillType.class);
        waybillType.setId(Vocabulary.ONTOLOGY_IRI_cargo + "#" + WaybillTypeCode.HOUSE.code());
        waybill.setWaybillType(waybillType);
        /*
         * Conversion is split by topic of the individual CIMP segments,
         * just to allow easier navigation though source code.
         * CargoXML elements which have no CIMP mapping should get mapped
         * in the CIMP segment which matches best topic-wise.
         */
        convertSignatory();         // Mandatory fields in XFZB not in FHL
        convertCIMPSegment02();     // Master AWB Consignment Detail
        convertCIMPSegment03to05(); // House Waybill Summary Details, HTS, NOG
        convertCIMPSegment06();     // OCI
        convertCIMPSegment07();     // Shipper
        convertCIMPSegment08();     // Consignee
        convertCIMPSegment09();     // Charge Declarations (O)
    }

    private void convertSignatory() {
        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment16to17Signatory
        // but using classes from xfzb3 schema package
        CarrierAuthenticationType sigCarrier = xmlBH.getSignatoryCarrierAuthentication();
        if (sigCarrier != null) {
            waybill.setCarrierDeclarationSignature(value(sigCarrier.getSignatory()));
            waybill.setCarrierDeclarationDate(convertToOffsetDateTime(sigCarrier.getActualDateTime()));
            AuthenticationLocationType location = sigCarrier.getIssueAuthenticationLocation();
            if (location != null) {
                waybill.setCarrierDeclarationPlace(ONERecordCargoUtil.create(Location.class));
                Set<CodeListElement> locationCodes = ONERecordCargoUtil.buildSet();
                CodeListElement locationCode = createCodeListElementGeneral(value(location.getName()));
                locationCodes.add(locationCode);
                waybill.getCarrierDeclarationPlace().setLocationCodes(locationCodes);
            }
        }
        ConsignorAuthenticationType sigConsignor = xmlBH.getSignatoryConsignorAuthentication();
        if (sigConsignor != null) {
            String singleEntry = value(sigConsignor.getSignatory());
            waybill.setConsignorDeclarationSignature(singleEntry);
        }
    }

    // *************************************************************************
    // CIMP FHL Segment 2: Master AWB Consignment Detail (M)
    // *************************************************************************
    private void convertCIMPSegment02() {
        addWarning(VG_INFORMATION, "<MasterConsignment><TransportContractDocument><ID> (=Master AWB number) is ignored, this House Waybill should be added to Master Waybill#containedWaybills instead");
    }

    // *************************************************************************
    // CIMP FHL Segment 3: House Waybill Summary Details (M)
    // CIMP FHL Segment 4: Free text Description Of Goods (O)
    // CIMP FHL Segment 5: HTS Codes (O)
    // *************************************************************************
    private void convertCIMPSegment03to05() {
        waybill.setWaybillNumber(value(xmlBH.getID()));

        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment12:
        Set<WaybillLineItem> waybillLineItems = ONERecordCargoUtil.buildSet();
        waybill.setWaybillLineItems(waybillLineItems);
        WaybillLineItem firstLineItem = ONERecordCargoUtil.create(WaybillLineItem.class);
        firstLineItem.setSlacForRate(integerValue(xmlHouse.getPackageQuantity()));
        // HTS:
        List<String> hts = new ArrayList<>();
        List<String> nog = new ArrayList<>();
        List<Item> allDims = new ArrayList<>();
        List<ULD> allULD = new ArrayList<>();
        for (HouseConsignmentItemType hci : xmlHouse.getIncludedHouseConsignmentItem()) {
            for (CodeType codeType : hci.getTypeCode()) {
                if (codeType != null) {
                    hts.add(value(codeType));
                }
            }
            if (hci.getNatureIdentificationTransportCargo() != null) {
                String nature = value(
                    hci.getNatureIdentificationTransportCargo().getIdentification());
                if (nature != null) {
                    nog.addAll(Arrays.asList(nature.split("\n")));
                }
            }

            if (hci.getOriginCountry() != null) {
                mainPiece.setContentProductionCountry(
                    value(hci.getOriginCountry().getID(), null));
            }

            for (LogisticsPackageType lp : hci.getTransportLogisticsPackage()) {
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
                item.setDimensions(dim1R);
                item.setWeight(value(lp.getGrossWeightMeasure()));
                Value count = ONERecordCargoUtil.create(Value.class);
                count.setNumericalValue((double) xmlPackageCount);
                item.setItemQuantity(count);
                allDims.add(item);
            }

            UnitLoadTransportEquipmentType xmlULD = hci.getAssociatedUnitLoadTransportEquipment();
            if (xmlULD != null) {
                ULD uld1R = ONERecordCargoUtil.create(ULD.class);
                uld1R.setUldSerialNumber(value(xmlULD.getID()));
                uld1R.setTareWeight(value(xmlULD.getTareWeightMeasure()));
                uld1R.setUldTypeCode(createCodeListElementGeneral(
                    value(xmlULD.getCharacteristicCode())));
                if (xmlULD.getOperatingParty() != null) {
                    uld1R.setOwnerCode(createCodeListElementGeneral(
                        value(xmlULD.getOperatingParty().getPrimaryID())));
                }
                allULD.add(uld1R);
            }
        }

        if (xmlHouse.getSummaryDescription() != null) {
            if (nog.isEmpty()) {
                nog.addAll(Arrays.asList(value(xmlHouse.getSummaryDescription()).split("\n")));
            } else {
                addHint(VG_INFORMATION, "IncludedHouseConsignmentItem.NatureIdentificationTransportCargo got preferred over IncludedHouseConsignment.SummaryDescription");
            }
        }

        // adding now:
        if (!hts.isEmpty()) {
            if (mainPiece.getContainedItems() == null) {
                mainPiece.setContainedItems(ONERecordCargoUtil.buildSet());
            }
            for (String hsCode : hts) {
                Item item = ONERecordCargoUtil.create(Item.class);
                item.setOfProduct(ONERecordCargoUtil.create(Product.class));
                item.getOfProduct().setHsCode(createCodeListElementGeneral(hsCode));
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
            for (int i = 0; i < allULD.size(); ++i) {
                ULD uld = allULD.get(i);
                if (i == 0) {
                    firstLineItem.setUldSerialNumber(uld.getUldSerialNumber());
                    firstLineItem.setUldTareWeightForRate(uld.getTareWeight());
                    firstLineItem.setUldType(uld.getUldTypeCode());
                    firstLineItem.setUldOwnerCode(uld.getOwnerCode());
                    waybill.getWaybillLineItems().add(firstLineItem);
                    continue;
                }
                WaybillLineItem lineItem = ONERecordCargoUtil.create(WaybillLineItem.class);
                lineItem.setUldSerialNumber(uld.getUldSerialNumber());
                lineItem.setUldTareWeightForRate(uld.getTareWeight());
                lineItem.setUldType(uld.getUldTypeCode());
                lineItem.setUldOwnerCode(uld.getOwnerCode());
                waybill.getWaybillLineItems().add(lineItem);
            }
        } else {
            waybill.getWaybillLineItems().add(firstLineItem);
        }
        // Nature of Goods
        for (String s : nog) {
            if (mainPiece.getGoodsDescription() == null) {
                mainPiece.setGoodsDescription(s);
            } else {
                mainPiece.setGoodsDescription(mainPiece.getGoodsDescription() + "\n" + s);
            }
        }


        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment02:
        // departure and destination
        waybill.setDepartureLocation(value(xmlHouse.getOriginLocation()));
        waybill.setArrivalLocation(value(xmlHouse.getFinalDestinationLocation()));

        // totalPieceCount -> not available anymore on shipment
//        mainShipment.setTotalPieceCount(integerValue(xmlHouse.getTotalPieceQuantity()));
        int pieceQuantityXML = integerValue(xmlHouse.getTotalPieceQuantity());
        checkPieceQuantityAgainstItemCount(pieceQuantityXML, mainPiece.getContainedItems());

        // totalGrossWeight
        mainShipment.setTotalGrossWeight(value(xmlHouse.getIncludedTareGrossWeightMeasure()));
        mainPiece.setGrossWeight(mainShipment.getTotalGrossWeight());
        addHint(VG_INFORMATION, "(Total)GrossWeight is mandatory on Shipment and on Piece, value from MasterConsignment/IncludedTareGrossWeightMeasure is used for both");

        VolumetricWeight volumetricWeight = ONERecordCargoUtil.create(VolumetricWeight.class);
        volumetricWeight.setChargeableWeight(mainShipment.getTotalGrossWeight());
        mainShipment.setTotalVolumetricWeight(volumetricWeight);

        // totalVolume
        if (xmlHouse.getGrossVolumeMeasure() != null) {
            Dimensions volume = ONERecordCargoUtil.create(Dimensions.class);
            volume.setVolume(value(xmlHouse.getGrossVolumeMeasure()));
            mainPiece.setDimensions(volume);
        }
    }

    // *************************************************************************
    // CIMP FHL Segment 6: OCI
    // *************************************************************************
    private void convertCIMPSegment06() {
        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment29:
        // keep in sync!
        if (isNullOrEmpty(xmlHouse.getIncludedCustomsNote())) {
            return;
        }
        SecurityDeclaration secDec = ONERecordCargoUtil.create(SecurityDeclaration.class);
        String previousCiSubjectCode = "X";
        boolean haveSecDec = false;
        boolean haveCTCP = false;
        for (CustomsNoteType xmlCustNote : xmlHouse.getIncludedCustomsNote()) {
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
            CustomsInformation custInfo = ONERecordCargoUtil.create(CustomsInformation.class);
            CodeListElement contentCodeCLE = createCodeListElementGeneral(contentCode);
            custInfo.setContentCode(contentCodeCLE);
            custInfo.setCountryCode(countryCode);
            // data field "customsInfoNote":
            // Free text for customs remarks, not used in OCI Composition Rules Table
            CodeListElement subjectCodeCLE = createCodeListElementGeneral(subjectCode);
            custInfo.setSubjectCode(subjectCodeCLE);
            custInfo.setNote(contentText);

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
                if (mainPiece.getCustomsInformation() == null) {
                    mainPiece.setCustomsInformation(ONERecordCargoUtil.buildSet());
                }
                mainPiece.getCustomsInformation().add(custInfo);
            }
            if (subjectCode != null) {
                previousCiSubjectCode = subjectCode;
            }
        }
        if (haveSecDec) {
            mainPiece.setSecurityDeclaration(secDec);
        }
    }

    // *************************************************************************
    // CIMP FHL Segment 7: Shipper Name and Address (O)
    // *************************************************************************
    private void convertCIMPSegment07() {
        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment05:
        if (mainShipment.getInvolvedParties() == null) {
            mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
        }
        mainShipment.getInvolvedParties().add(createParty(
            PartyRoleCode.SHP,
            xmlHouse.getConsignorParty(),
            getCustomsNotesBySubjectCode("SHP")
        ));
    }

    // *************************************************************************
    // CIMP FHL Segment 8: Consignee Name and Address (C)
    // *************************************************************************
    private void convertCIMPSegment08() {
        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment06:
        if (mainShipment.getInvolvedParties() == null) {
            mainShipment.setInvolvedParties(ONERecordCargoUtil.buildSet());
        }
        mainShipment.getInvolvedParties().add(createParty(
            PartyRoleCode.CNE,
            xmlHouse.getConsigneeParty(),
            getCustomsNotesBySubjectCode("CNE")
        ));
    }

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
            address.setStreetAddressLines(ONERecordCargoUtil.buildSet(street.split("\n")));
        }
        address.setCityName(value(xmlAddress.getCityName()));
        CodeListElement postalCodeCLE = createCodeListElementGeneral(value(xmlAddress.getPostcodeCode()));
        address.setPostalCode(postalCodeCLE);
        address.setCountry(value(xmlAddress.getCountryID(), xmlAddress.getCountryName()));
        return company;
    }
    static Address prepareCompanyAddress(Company company) {
        if (company.getBasedAtLocation() == null) {
            company.setBasedAtLocation(ONERecordCargoUtil.create(Location.class));
        }
        if (company.getBasedAtLocation().getAddress() == null) {
            company.getBasedAtLocation().setAddress(ONERecordCargoUtil.create(Address.class));
        }
        return company.getBasedAtLocation().getAddress();
    }

    private ContactDetail createContact(ContactTypeCode type, String value) {
        ContactDetail contact = ONERecordCargoUtil.create(ContactDetail.class);
        ContactDetailType contactDetailType = ONERecordCargoUtil.create(ContactDetailType.class);
        contactDetailType.setCode(type.code());
        contact.setContactDetailType(contactDetailType);
        contact.setTextualValue(value);
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
            company.setName(array[0]);
        } else {
            company.setName(name);
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
            person.setContactDetails(ONERecordCargoUtil.buildSet());
            if (phone != null) {
                person.getContactDetails().add(createContact(ContactTypeCode.PHONE, phone));
            }
            if (fax != null) {
                person.getContactDetails().add(createContact(ContactTypeCode.FAX, fax));
            }
            if (mail != null) {
                person.getContactDetails().add(createContact(ContactTypeCode.EMAIL, mail));
            }
            company.setContactPersons(ONERecordCargoUtil.buildSet(person));
        }
        return company;
    }

    private List<CustomsNoteType> getCustomsNotesBySubjectCode(String subjectCode)
    {
        if (subjectCode == null) {
            return null;
        }
        return xmlHouse.getIncludedCustomsNote().stream()
            .filter(icn -> subjectCode.equals(value(icn.getSubjectCode())))
            .collect(Collectors.toList());
    }

    // *************************************************************************
    // CIMP FHL Segment 9: Charge Declarations (0)
    // *************************************************************************
    private void convertCIMPSegment09() {
        if (xmlHouse.getTotalPrepaidChargeAmount() != null) {
            addWarning(VG_INFORMATION,
                "<IncludedHouseConsignment><TotalPrepaidChargeAmount> is not available as field in OneRecord, but intended to be calculated as sum over all prepaid charge amounts");
        }
        if (xmlHouse.getTotalCollectChargeAmount() != null) {
            addWarning(VG_INFORMATION,
                "<IncludedHouseConsignment><TotalCollectChargeAmount> is not available as field in OneRecord, but intended to be calculated as sum over all collect charge amounts");
        }

        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment11:
        Insurance insurance = ONERecordCargoUtil.create(Insurance.class);
        // If no data, it is considered no value declared
//        insurance.setNvdIndicator(xmlHouse.isNilInsuranceValueIndicator());
        boolean isNilInsurance = xmlHouse.isNilInsuranceValueIndicator() != null && xmlHouse.isNilInsuranceValueIndicator();
        if (!isNilInsurance) {
            insurance.setInsuredAmount(
                value(xmlHouse.getInsuranceValueAmount(), null));
        }
        mainShipment.setInsurance(insurance);

        mainPiece.setNvdForCarriage(xmlHouse.isNilCarriageValueIndicator());
        boolean isNilCarriage = xmlHouse.isNilCarriageValueIndicator() != null && xmlHouse.isNilCarriageValueIndicator();
        if (!isNilCarriage) {
            waybill.setDeclaredValueForCarriage(value(
                xmlHouse.getDeclaredValueForCarriageAmount(), null));
        }

        mainPiece.setNvdForCustoms(xmlHouse.isNilCustomsValueIndicator());
        boolean isNilCustoms = xmlHouse.isNilCustomsValueIndicator() != null && xmlHouse.isNilCustomsValueIndicator();
        if (!isNilCustoms) {
            waybill.setDeclaredValueForCustoms(value(
                xmlHouse.getDeclaredValueForCustomsAmount(), null));
        }
    }

}
