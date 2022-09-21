package com.riege.onerecord.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.iata.cargo.Vocabulary;
import org.iata.cargo.model.Address;
import org.iata.cargo.model.Booking;
import org.iata.cargo.model.BookingOption;
import org.iata.cargo.model.Branch;
import org.iata.cargo.model.Carrier;
import org.iata.cargo.model.Company;
import org.iata.cargo.model.CompanyBranch;
import org.iata.cargo.model.Contact;
import org.iata.cargo.model.ContactOther;
import org.iata.cargo.model.Country;
import org.iata.cargo.model.CustomsInfo;
import org.iata.cargo.model.Dimensions;
import org.iata.cargo.model.Event;
import org.iata.cargo.model.HandlingInstructions;
import org.iata.cargo.model.Insurance;
import org.iata.cargo.model.Item;
import org.iata.cargo.model.Location;
import org.iata.cargo.model.MovementTimes;
import org.iata.cargo.model.OtherIdentifier;
import org.iata.cargo.model.Party;
import org.iata.cargo.model.Person;
import org.iata.cargo.model.Piece;
import org.iata.cargo.model.Price;
import org.iata.cargo.model.Product;
import org.iata.cargo.model.Ranges;
import org.iata.cargo.model.Ratings;
import org.iata.cargo.model.RegulatedEntity;
import org.iata.cargo.model.Routing;
import org.iata.cargo.model.SecurityDeclaration;
import org.iata.cargo.model.ServiceRequest;
import org.iata.cargo.model.Shipment;
import org.iata.cargo.model.SpecialHandling;
import org.iata.cargo.model.TransportMeans;
import org.iata.cargo.model.TransportMovement;
import org.iata.cargo.model.TransportSegment;
import org.iata.cargo.model.ULD;
import org.iata.cargo.model.Value;
import org.iata.cargo.model.VolumetricWeight;
import org.iata.cargo.model.Waybill;

public class OneRecordTypeConstants {

    public static final Value createValue() {
        Value model = new Value();
        model.setTypes(buildTypeset(Vocabulary.s_c_Value));
        return model;
    }

    public static final Event createEvent() {
        Event model = new Event();
        model.setTypes(buildTypeset(Vocabulary.s_c_Event));
        return model;
    }

    public static final Shipment createShipment() {
        Shipment model = new Shipment();
        model.setTypes(buildTypeset(Vocabulary.s_c_Shipment));
        return model;
    }

    public static final Waybill createWaybill() {
        Waybill model = new Waybill();
        model.setTypes(buildTypeset(Vocabulary.s_c_Waybill));
        return model;
    }

    public static final BookingOption createBookingOption() {
        BookingOption model = new BookingOption();
        model.setTypes(buildTypeset(Vocabulary.s_c_BookingOption));
        return model;
    }

    public static final Party createParty() {
        Party model = new Party();
        model.setTypes(buildTypeset(Vocabulary.s_c_Party));
        return model;
    }

    public static final Company createCompany() {
        Company model = new Company();
        model.setTypes(buildTypeset(Vocabulary.s_c_Company));
        return model;
    }

    public static final Carrier createCarrier() {
        Carrier model = new Carrier();
        model.setTypes(buildTypeset(Vocabulary.s_c_Carrier));
        return model;
    }

    public static final CompanyBranch createCompanyBranch() {
        CompanyBranch model = new CompanyBranch();
        model.setTypes(buildTypeset(Vocabulary.s_c_CompanyBranch));
        return model;
    }

    public static final Location createLocation() {
        Location model = new Location();
        model.setTypes(buildTypeset(Vocabulary.s_c_Location));
        return model;
    }

    public static final Address createAddress() {
        Address model = new Address();
        model.setTypes(buildTypeset(Vocabulary.s_c_Address));
        return model;
    }

    public static final Country createCountry() {
        Country model = new Country();
        model.setTypes(buildTypeset(Vocabulary.s_c_Country));
        return model;
    }

    public static final TransportSegment createTransportSegment() {
        TransportSegment model = new TransportSegment();
        model.setTypes(buildTypeset(Vocabulary.s_c_TransportSegment));
        return model;
    }

    public static final TransportMovement createTransportMovement() {
        TransportMovement model = new TransportMovement();
        model.setTypes(buildTypeset(Vocabulary.s_c_TransportMovement));
        return model;
    }

    public static final MovementTimes createMovementTimes() {
        MovementTimes model = new MovementTimes();
        model.setTypes(buildTypeset(Vocabulary.s_c_MovementTimes));
        return model;
    }

    public static final TransportMeans createTransportMeans() {
        TransportMeans model = new TransportMeans();
        model.setTypes(buildTypeset(Vocabulary.s_c_TransportMeans));
        return model;
    }

    public static final Routing createRouting() {
        Routing model = new Routing();
        model.setTypes(buildTypeset(Vocabulary.s_c_Routing));
        return model;
    }

    public static final Piece createPiece() {
        Piece model = new Piece();
        model.setTypes(buildTypeset(Vocabulary.s_c_Piece));
        return model;
    }

    public static final VolumetricWeight createVolumetricWeight() {
        VolumetricWeight model = new VolumetricWeight();
        model.setTypes(buildTypeset(Vocabulary.s_c_VolumetricWeight));
        return model;
    }

    public static final ServiceRequest createServiceRequest() {
        ServiceRequest model = new ServiceRequest();
        model.setTypes(buildTypeset(Vocabulary.s_c_ServiceRequest));
        return model;
    }

    public static final Dimensions createDimensions() {
        Dimensions model = new Dimensions();
        model.setTypes(buildTypeset(Vocabulary.s_c_Dimensions));
        return model;
    }

    public static final OtherIdentifier createOtherIdentifier() {
        OtherIdentifier model = new OtherIdentifier();
        model.setTypes(buildTypeset(Vocabulary.s_c_OtherIdentifier));
        return model;
    }

    public static final Person createPerson() {
        Person model = new Person();
        model.setTypes(buildTypeset(Vocabulary.s_c_Person));
        return model;
    }

    public static final Contact createContact() {
        Contact model = new Contact();
        model.setTypes(buildTypeset(Vocabulary.s_c_Contact));
        return model;
    }

    public static final Insurance createInsurance() {
        Insurance model = new Insurance();
        model.setTypes(buildTypeset(Vocabulary.s_c_Insurance));
        return model;
    }

    public static final Price createPrice() {
        Price model = new Price();
        model.setTypes(buildTypeset(Vocabulary.s_c_Price));
        return model;
    }

    public static final Item createItem() {
        Item model = new Item();
        model.setTypes(buildTypeset(Vocabulary.s_c_Item));
        return model;
    }

    public static final Product createProduct() {
        Product model = new Product();
        model.setTypes(buildTypeset(Vocabulary.s_c_Product));
        return model;
    }

    public static final Ranges createRanges() {
        Ranges model = new Ranges();
        model.setTypes(buildTypeset(Vocabulary.s_c_Ranges));
        return model;
    }

    public static final Ratings createRatings() {
        Ratings model = new Ratings();
        model.setTypes(buildTypeset(Vocabulary.s_c_Ratings));
        return model;
    }

    public static final ULD createULD() {
        ULD model = new ULD();
        model.setTypes(buildTypeset(Vocabulary.s_c_ULD));
        return model;
    }

    public static final CustomsInfo createCustomsInfo() {
        CustomsInfo model = new CustomsInfo();
        model.setTypes(buildTypeset(Vocabulary.s_c_CustomsInfo));
        return model;
    }

    public static final SpecialHandling createSpecialHandling() {
        SpecialHandling model = new SpecialHandling();
        model.setTypes(buildTypeset(Vocabulary.s_c_SpecialHandling));
        return model;
    }

    public static final SecurityDeclaration createSecurityDeclaration() {
        SecurityDeclaration model = new SecurityDeclaration();
        model.setTypes(buildTypeset(Vocabulary.s_c_SecurityDeclaration));
        return model;
    }

    public static final RegulatedEntity createRegulatedEntity() {
        RegulatedEntity model = new RegulatedEntity();
        model.setTypes(buildTypeset(Vocabulary.s_c_RegulatedEntity));
        return model;
    }

    public static HandlingInstructions createHandlingInstructions() {
        HandlingInstructions model = new HandlingInstructions();
        model.setTypes(buildTypeset(Vocabulary.s_c_HandlingInstructions));
        return model;
    }

    // *************************************************************************

    private static final Set<String> buildTypeset(String vocabularyString) {
        return buildSet(vocabularyString);
    }

    public static final <T> Set<T> buildSet() {
        // LinkedHashSet used to keep the order in which elements were inserted
        return new LinkedHashSet<>();
    }

    /**
     * @param <T> the type of elements maintained by this set
     * @param singleEntry is added to the generated and returned Set
     * @return Set built from the provided data, null if provided data is null
     */
    public static final <T> Set<T> buildSet(T singleEntry) {
        // LinkedHashSet used to keep the order in which elements were inserted
        return singleEntry == null
            ? null
            : new LinkedHashSet<>(Collections.singletonList(singleEntry));
    }

    /**
     * @param <T> the type of elements maintained by this set
     * @param array entries are added to the generated and returned Set
     * @return Set built from the provided data even if the array is empty, null if provided data is null
     */
    public static final <T> Set<T> buildSet(T[] array) {
        // LinkedHashSet used to keep the order in which elements were inserted
        return array == null
            ? null
            : new LinkedHashSet<>(Arrays.asList(array));
    }

    /**
     * @param <T> the type of elements maintained by this set
     * @param list entries are added to the generated and returned Set
     * @return Set built from the provided data even if the list is empty, null if provided data is null
     */
    public static final <T> Set<T> buildSet(List<T> list) {
        // LinkedHashSet used to keep the order in which elements were inserted
        return list == null
            ? null
            : new LinkedHashSet<>(list);
    }

}
