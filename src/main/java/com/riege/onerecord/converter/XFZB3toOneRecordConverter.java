package com.riege.onerecord.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.iata.onerecord.cargo.codelists.ContactTypeCode;
import org.iata.onerecord.cargo.codelists.OtherIdentifierTypeCode;
import org.iata.onerecord.cargo.codelists.PartyRoleCode;
import org.iata.onerecord.cargo.codelists.WaybillTypeCode;
import org.iata.onerecord.cargo.model.Address;
import org.iata.onerecord.cargo.model.Booking;
import org.iata.onerecord.cargo.model.BookingOption;
import org.iata.onerecord.cargo.model.BookingRequest;
import org.iata.onerecord.cargo.model.Carrier;
import org.iata.onerecord.cargo.model.Company;
import org.iata.onerecord.cargo.model.CompanyBranch;
import org.iata.onerecord.cargo.model.Contact;
import org.iata.onerecord.cargo.model.CustomsInfo;
import org.iata.onerecord.cargo.model.Dimensions;
import org.iata.onerecord.cargo.model.Insurance;
import org.iata.onerecord.cargo.model.Item;
import org.iata.onerecord.cargo.model.Location;
import org.iata.onerecord.cargo.model.OtherIdentifier;
import org.iata.onerecord.cargo.model.Party;
import org.iata.onerecord.cargo.model.Person;
import org.iata.onerecord.cargo.model.Piece;
import org.iata.onerecord.cargo.model.Product;
import org.iata.onerecord.cargo.model.SecurityDeclaration;
import org.iata.onerecord.cargo.model.Shipment;
import org.iata.onerecord.cargo.model.TransportMovement;
import org.iata.onerecord.cargo.model.ULD;
import org.iata.onerecord.cargo.model.Value;
import org.iata.onerecord.cargo.model.VolumetricWeight;
import org.iata.onerecord.cargo.model.Waybill;
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
    private TransportMovement mainTransportSegment;
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
        waybill.setBooking(ONERecordCargoUtil.create(Booking.class));
        waybill.getBooking().setBookingRequest(ONERecordCargoUtil.create(BookingRequest.class));
        waybill.getBooking().getBookingRequest().setBookingOption(mainBooking);

        // NOTE: BookingOption has no setter for TransportMovement yet!
        mainTransportSegment = ONERecordCargoUtil.create(TransportMovement.class);

        mainShipment = ONERecordCargoUtil.create(Shipment.class);
        mainBooking.setShipmentDetails(mainShipment);

        mainPiece = ONERecordCargoUtil.create(Piece.class);
        mainPiece.setTransportMovements(ONERecordCargoUtil.buildSet(mainTransportSegment));
        mainShipment.setContainedPieces(ONERecordCargoUtil.buildSet(mainPiece));

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

        waybill.setWaybillType(WaybillTypeCode.HOUSE.code());
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
        mainShipment.setTotalSLAC(integerValue(xmlHouse.getPackageQuantity()));
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

            if (mainPiece.getProduct() == null) {
                mainPiece.setProduct(ONERecordCargoUtil.buildSet());
            }

            if (hci.getOriginCountry() != null) {
                mainPiece.setProductionCountry(
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
                // "Product" is mandatory for item as per Ontology
                item.setProduct(ONERecordCargoUtil.create(Product.class));
                item.setDimensions(dim1R);
                item.setWeight(value(lp.getGrossWeightMeasure()));
                Value count = ONERecordCargoUtil.create(Value.class);
                count.setValue(Double.valueOf(xmlPackageCount));
                item.setQuantity(count);
                allDims.add(item);
            }

            UnitLoadTransportEquipmentType xmlULD = hci.getAssociatedUnitLoadTransportEquipment();
            if (xmlULD != null) {
                ULD uld1R = ONERecordCargoUtil.create(ULD.class);
                uld1R.setSerialNumber(value(xmlULD.getID()));
                uld1R.setTareWeight(value(xmlULD.getTareWeightMeasure()));
                uld1R.setUldTypeCode(value(xmlULD.getCharacteristicCode()));
                if (xmlULD.getOperatingParty() != null) {
                    uld1R.setOwnerCode(value(xmlULD.getOperatingParty().getPrimaryID()));
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
            if (mainTransportSegment.getTransportedUlds() == null) {
                mainTransportSegment.setTransportedUlds(ONERecordCargoUtil.buildSet());
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


        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment02:
        // departure and destination
        mainTransportSegment.setDepartureLocation(value(xmlHouse.getOriginLocation()));
        mainTransportSegment.setArrivalLocation(value(xmlHouse.getFinalDestinationLocation()));

        // totalPieceCount
        mainShipment.setTotalPieceCount(integerValue(xmlHouse.getTotalPieceQuantity()));

        // totalGrossWeight
        mainShipment.setTotalGrossWeight(value(xmlHouse.getIncludedTareGrossWeightMeasure()));
        mainPiece.setGrossWeight(mainShipment.getTotalGrossWeight());
        addHint(VG_INFORMATION, "(Total)GrossWeight is mandatory on Shipment and on Piece, value from MasterConsignment/IncludedTareGrossWeightMeasure is used for both");

        VolumetricWeight volumetricWeight = ONERecordCargoUtil.create(VolumetricWeight.class);
        volumetricWeight.setChargeableWeight(mainShipment.getTotalGrossWeight());
        mainShipment.setVolumetricWeight(ONERecordCargoUtil.buildSet(volumetricWeight));

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
            if (XFWB3toOneRecordConverter.updateSecurityDeclaration(custInfo, secDec, previousCiSubjectCode)) {
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

    // *************************************************************************
    // CIMP FHL Segment 7: Shipper Name and Address (O)
    // *************************************************************************
    private void convertCIMPSegment07() {
        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment05:
        if (mainBooking.getParties() == null) {
            mainBooking.setParties(ONERecordCargoUtil.buildSet());
        }
        mainBooking.getParties().add(createParty(
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
        if (mainBooking.getParties() == null) {
            mainBooking.setParties(ONERecordCargoUtil.buildSet());
        }
        mainBooking.getParties().add(createParty(
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
        party.setPartyRole(partyRole.code());
        party.setPartyDetails(company);
        if (accountID != null) {
            // See https://github.com/IATA-Cargo/ONE-Record/issues/130
            OtherIdentifier oi = ONERecordCargoUtil.create(OtherIdentifier.class);
            oi.setOtherIdentifierType(OtherIdentifierTypeCode.ACCOUNT_ID.code());
            oi.setOtherIdentifierType("AccountID");
            oi.setIdentifier(accountID);
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
        return xmlHouse.getIncludedCustomsNote().stream()
            .filter(icn -> subjectCode.equals(value(icn.getSubjectCode())))
            .collect(Collectors.toList());
    }

    // *************************************************************************
    // CIMP FHL Segment 9: Charge Declarations (0)
    // *************************************************************************
    private void convertCIMPSegment09() {
        if (xmlHouse.getTotalPrepaidChargeAmount() != null) {
            addWarning(VG_UNCERTAINTY,
                "Unclear where to put <IncludedHouseConsignment><TotalPrepaidChargeAmount>");
        }
        if (xmlHouse.getTotalCollectChargeAmount() != null) {
            addWarning(VG_UNCERTAINTY,
                "Unclear where to put <IncludedHouseConsignment><TotalCollectChargeAmount>");
        }

        // Copied from XFWB3toOneRecordConverter#convertCIMPSegment11:
        Insurance insurance = ONERecordCargoUtil.create(Insurance.class);
        insurance.setNvdIndicator(xmlHouse.isNilInsuranceValueIndicator());
        boolean isNilInsurance = xmlHouse.isNilInsuranceValueIndicator() != null && xmlHouse.isNilInsuranceValueIndicator();
        if (!isNilInsurance) {
            insurance.setInsuranceAmount(
                value(xmlHouse.getInsuranceValueAmount(), null));
        }
        mainShipment.setInsurance(insurance);

        mainPiece.setNvdForCarriage(xmlHouse.isNilCarriageValueIndicator());
        boolean isNilCarriage = xmlHouse.isNilCarriageValueIndicator() != null && xmlHouse.isNilCarriageValueIndicator();
        if (!isNilCarriage) {
            mainPiece.setDeclaredValueForCarriage(
                xmlHouse.getDeclaredValueForCarriageAmount().getValue().toString()
            );
        }

        mainPiece.setNvdForCustoms(xmlHouse.isNilCustomsValueIndicator());
        boolean isNilCustoms = xmlHouse.isNilCustomsValueIndicator() != null && xmlHouse.isNilCustomsValueIndicator();
        if (!isNilCustoms) {
            mainPiece.setDeclaredValueForCustoms(
                xmlHouse.getDeclaredValueForCustomsAmount().getValue().toString()
            );
        }
    }

}
