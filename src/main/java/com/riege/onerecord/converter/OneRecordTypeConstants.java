package com.riege.onerecord.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.model.Address;
import org.iata.onerecord.cargo.model.Booking;
import org.iata.onerecord.cargo.model.BookingOption;
import org.iata.onerecord.cargo.model.BookingRequest;
import org.iata.onerecord.cargo.model.Carrier;
import org.iata.onerecord.cargo.model.Company;
import org.iata.onerecord.cargo.model.CompanyBranch;
import org.iata.onerecord.cargo.model.Contact;
import org.iata.onerecord.cargo.model.Country;
import org.iata.onerecord.cargo.model.CustomsInfo;
import org.iata.onerecord.cargo.model.Dimensions;
import org.iata.onerecord.cargo.model.Event;
import org.iata.onerecord.cargo.model.HandlingInstructions;
import org.iata.onerecord.cargo.model.Insurance;
import org.iata.onerecord.cargo.model.Item;
import org.iata.onerecord.cargo.model.Location;
import org.iata.onerecord.cargo.model.MovementTimes;
import org.iata.onerecord.cargo.model.OtherIdentifier;
import org.iata.onerecord.cargo.model.Party;
import org.iata.onerecord.cargo.model.Person;
import org.iata.onerecord.cargo.model.Piece;
import org.iata.onerecord.cargo.model.Price;
import org.iata.onerecord.cargo.model.Product;
import org.iata.onerecord.cargo.model.Ranges;
import org.iata.onerecord.cargo.model.Ratings;
import org.iata.onerecord.cargo.model.RegulatedEntity;
import org.iata.onerecord.cargo.model.Routing;
import org.iata.onerecord.cargo.model.SecurityDeclaration;
import org.iata.onerecord.cargo.model.Shipment;
import org.iata.onerecord.cargo.model.TransportMeans;
import org.iata.onerecord.cargo.model.TransportMovement;
import org.iata.onerecord.cargo.model.ULD;
import org.iata.onerecord.cargo.model.Value;
import org.iata.onerecord.cargo.model.VolumetricWeight;
import org.iata.onerecord.cargo.model.Waybill;

public class OneRecordTypeConstants {

    public static final <T> T create(Class<T> clazz) {
        try {
            T instance = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            Field field = Vocabulary.class.getDeclaredField("s_c_" + clazz.getSimpleName());
            Set<String> typeValue = new LinkedHashSet<>(Collections.singletonList((String) field.get(null)));
            Method typeSetter = clazz.getMethod("setTypes", new Class[] { Set.class } );
            typeSetter.invoke(instance, typeValue);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Value createValue() {
        return create(Value.class);
    }

    public static final Event createEvent() {
        return create(Event.class);
    }

    public static final Shipment createShipment() {
        return create(Shipment.class);
    }

    public static final Waybill createWaybill() {
        return create(Waybill.class);
    }

    public static final Booking createBooking() {
        Booking model = new Booking();
        model.setTypes(buildTypeset(Vocabulary.s_c_Booking));
        return model;
    }

    public static final BookingRequest createBookingRequest() {
        BookingRequest model = new BookingRequest();
        model.setTypes(buildTypeset(Vocabulary.s_c_BookingRequest));
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
