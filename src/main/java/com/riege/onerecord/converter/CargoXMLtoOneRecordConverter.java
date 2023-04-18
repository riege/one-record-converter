package com.riege.onerecord.converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.iata.onerecord.cargo.model.LogisticsObject;

/**
 * Abstract superclass for converters into ONE Record type T
 * @param <T> the type of ONE Record LogisticsObject to convert into
 */
public abstract class CargoXMLtoOneRecordConverter<T extends LogisticsObject> {

    public static final String VG_GENERAL = "General";
    public static final String VG_UNCERTAINTY = "Mapping-Uncertainty";
    public static final String VG_UNIMPLEMENTED = "Not-Implemented-Yet";
    public static final String VG_XMLDATAWARNING = "XML-Data-Warning";
    public static final String VG_XMLDATAERROR = "XML-Data-Error";
    public static final String VG_INFORMATION = "Info";
    protected final ValidationResult validationResult;

    public CargoXMLtoOneRecordConverter() {
        validationResult = new ValidationResult();

        // General Hints
        addHint(VG_GENERAL, "This converter intentionally does neither set IDs nor makes use of persisted data for linked-data purposes.");
        addHint(VG_GENERAL, "This converter is based on the schema from IATA CargoXML Toolkit 8th Edition.");
        addHint(VG_GENERAL, "This converter is based on ONE RECORD datamodel Ontology 2.0, see https://github.com/IATA-Cargo/ONE-Record/tree/master/June-2021-standard-forCOTBendorsement/Data-Model");
        addHint(VG_GENERAL, "Codes and units are applied 1:1 from CargoXML where applicable.");
        addHint(VG_GENERAL, "Line breaks are respected for some fields if provided in XML, e.g. for multi-line goods description or address name/street");
        addHint(VG_GENERAL, "Line breaks are intentionally added in 'goodsDescription' and 'accountingInformation' to preserve and indicate descriptions from more than one field if applicaple from original XML");
        addHint(VG_GENERAL, "The 1R converter is not mapping all possible data yet and has focus on the use-case of 'message from forwarder to airline'");
    }

    /**
     * @return converted ONE Record result
     */
    public abstract T getOneRecordResult();

    /**
     * @return complete conversion errors, warnings and hints
     */
    public final ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * @return conversion hints
     */
    public final List<ValidationMessage> getValidationHints() {
        return validationResult.getHints();
    }

    /**
     * @return conversion warnings
     */
    public final List<ValidationMessage> getValidationWarnings() {
        return validationResult.getWarnings();
    }

    /**
     * @return conversion warnings
     */
    public final List<ValidationMessage> getValidationErrors() {
        return validationResult.getErrors();
    }

    protected final void addHint(String group, String text) {
        validationResult.addHint(group, text);
    }

    protected final void addWarning(String group, String text) {
        validationResult.addWarning(group, text);
    }

    protected final void addError(String group, String text) {
        validationResult.addError(group, text);
    }

    // ***** Helper methods *****

    protected static Date convertToDate(XMLGregorianCalendar gregorianCalendar) {
        return gregorianCalendar.toGregorianCalendar().getTime();
    }

    protected static OffsetDateTime convertToOffsetDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        GregorianCalendar gc = xmlGregorianCalendar.toGregorianCalendar() ;
        ZonedDateTime zdt = gc.toZonedDateTime() ;
        return convertToOffsetDateTime(zdt.toInstant());
    }

    protected static OffsetDateTime convertToOffsetDateTime(Date date) {
        return convertToOffsetDateTime(date.toInstant());
    }

    protected static OffsetDateTime convertToOffsetDateTime(Instant instant) {
        // ZoneOffset zoneOffset = ZoneOffset.UTC;
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(instant);
        OffsetDateTime odt = instant.atOffset( zoneOffset ) ;
        return odt;
    }

    protected static OffsetDateTime convertToOffsetDateTime(Calendar calendar) {
        return convertToOffsetDateTime(calendar.getTime());
    }

}
