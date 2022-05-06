package com.riege.onerecord.converter;

import java.math.BigDecimal;

import org.iata.cargo.model.Country;
import org.iata.cargo.model.Location;
import org.iata.cargo.model.Value;

import com.riege.cargoxml.schema.xfzb3.AmountType;
import com.riege.cargoxml.schema.xfzb3.ArrivalLocationType;
import com.riege.cargoxml.schema.xfzb3.CodeType;
import com.riege.cargoxml.schema.xfzb3.CountryIDType;
import com.riege.cargoxml.schema.xfzb3.CurrencyCodeType;
import com.riege.cargoxml.schema.xfzb3.DepartureLocationType;
import com.riege.cargoxml.schema.xfzb3.DocumentCodeType;
import com.riege.cargoxml.schema.xfzb3.FinalDestinationLocationType;
import com.riege.cargoxml.schema.xfzb3.IDType;
import com.riege.cargoxml.schema.xfzb3.ISO3AlphaCurrencyCodeContentType;
import com.riege.cargoxml.schema.xfzb3.MeasureType;
import com.riege.cargoxml.schema.xfzb3.OriginLocationType;
import com.riege.cargoxml.schema.xfzb3.QuantityType;
import com.riege.cargoxml.schema.xfzb3.TextType;

public class XFZB3ParserHelper {

    public static String value(DocumentCodeType id) {
        return id != null && id.getValue() != null ? id.getValue() : null;
    }

    public static String value(IDType id) {
        return id != null && id.getValue() != null ? id.getValue() : null;
    }

    public static String value(CodeType code) {
        return code != null && code.getValue() != null ? code.getValue() : null;
    }

    public static String value(TextType text) {
        return text != null && text.getValue() != null ? text.getValue() : null;
    }

    public static Value value(MeasureType measure) {
        if (measure == null || measure.getValue() == null) {
            return null;
        }
        Value result = OneRecordTypeConstants.createValue();
        result.setUnit(measure.getUnitCode());
        result.setValue(measure.getValue().doubleValue());
        return result;
    }

    public static BigDecimal bigDecimal(MeasureType weightMeasure) {
        return weightMeasure == null ? null : weightMeasure.getValue();
    }

    public static String unitCode(MeasureType measure) {
        if (measure == null || measure.getUnitCode() == null) {
            return null;
        }
        return measure.getUnitCode();
    }

    public static Integer integerValue(QuantityType quantity) {
        return quantity != null && quantity.getValue() != null ? Integer.valueOf(quantity.getValue().intValue()) : null;
    }

    public static Value value(AmountType measure, String defaultCurrency) {
        if (measure == null) {
            return null;
        }
        Value result = OneRecordTypeConstants.createValue();
        result.setUnit(measure.getCurrencyID() == null ? defaultCurrency : measure.getCurrencyID().value());
        result.setValue(measure.getValue().doubleValue());
        return result;
    }

    // *************************************************************************

    public static Location value(DepartureLocationType locationType) {
        return locationType == null
            ? null
            : value(locationType.getID(), locationType.getName(), locationType.getTypeCode());
    }

    public static Location value(ArrivalLocationType locationType) {
        return locationType == null
            ? null
            : value(locationType.getID(), locationType.getName(), locationType.getTypeCode());
    }

    public static Location value(OriginLocationType locationType) {
        return locationType == null
            ? null
            : value(locationType.getID(), locationType.getName());
    }

    public static Location value(FinalDestinationLocationType locationType) {
        return locationType == null
            ? null
            : value(locationType.getID(), locationType.getName());
    }

    private static Location value(IDType locationCode, TextType locationName) {
        return value(locationCode, locationName, null);
    }

    private static Location value(IDType locationCode, TextType locationName, CodeType typeCode) {
        if (locationCode == null) {
            return null;
        }
        Location location = OneRecordTypeConstants.createLocation();
        location.setCode(value(locationCode));
        location.setLocationType(value(typeCode));
        location.setLocationName(value(locationName));
        return location;
    }

    public static Country value(CountryIDType countryIDType, TextType countryName) {
        if (countryIDType == null) {
            return null;
        }
        Country country = OneRecordTypeConstants.createCountry();
        country.setCountryCode(countryIDType.getValue().value());
        country.setCountryName(value(countryName));
        return country;
    }

    public static String value(CurrencyCodeType currencyCodeType) {
        return currencyCodeType == null ? null : value(currencyCodeType.getValue());
    }

    public static String value(ISO3AlphaCurrencyCodeContentType currencyCodeType) {
        return currencyCodeType == null ? null : currencyCodeType.value();
    }

}
