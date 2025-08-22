package com.riege.onerecord.converter;

import java.util.Locale;
import java.util.Set;

import org.iata.onerecord.cargo.Vocabulary;
import org.iata.onerecord.cargo.model.CodeListElement;
import org.iata.onerecord.cargo.model.Country;
import org.iata.onerecord.cargo.model.CurrencyCode;
import org.iata.onerecord.cargo.model.CurrencyValue;
import org.iata.onerecord.cargo.model.Location;
import org.iata.onerecord.cargo.model.MeasurementUnitCode;
import org.iata.onerecord.cargo.model.Value;
import org.iata.onerecord.cargo.util.ONERecordCargoUtil;

import com.riege.cargoxml.schema.xfwb3.AmountType;
import com.riege.cargoxml.schema.xfwb3.ArrivalLocationType;
import com.riege.cargoxml.schema.xfwb3.CodeType;
import com.riege.cargoxml.schema.xfwb3.CountryIDType;
import com.riege.cargoxml.schema.xfwb3.CurrencyCodeType;
import com.riege.cargoxml.schema.xfwb3.DepartureLocationType;
import com.riege.cargoxml.schema.xfwb3.DocumentCodeType;
import com.riege.cargoxml.schema.xfwb3.FinalDestinationLocationType;
import com.riege.cargoxml.schema.xfwb3.IDType;
import com.riege.cargoxml.schema.xfwb3.ISO3AlphaCurrencyCodeContentType;
import com.riege.cargoxml.schema.xfwb3.MeasureType;
import com.riege.cargoxml.schema.xfwb3.OriginLocationType;
import com.riege.cargoxml.schema.xfwb3.QuantityType;
import com.riege.cargoxml.schema.xfwb3.TextType;

public class XFWB3ParserHelper {

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
        if (measure == null || measure.getValue() == null || measure.getUnitCode() == null) {
            return null;
        }
        Value result = ONERecordCargoUtil.create(Value.class);
        MeasurementUnitCode muc = ONERecordCargoUtil.create(MeasurementUnitCode.class);
        muc.setId(Vocabulary.s_c_MeasurementUnitCode + "_" + measure.getUnitCode());
        result.setUnit(muc);
        result.setNumericalValue(measure.getValue().doubleValue());
        return result;
    }

    public static Value value(double appliedRate) {
        Value result = ONERecordCargoUtil.create(Value.class);
        result.setNumericalValue(appliedRate);
        return result;
    }

    public static Integer integerValue(QuantityType quantity) {
        return quantity != null && quantity.getValue() != null ? quantity.getValue().intValue()
            : null;
    }

    public static CurrencyValue value(AmountType measure, String defaultCurrency) {
        if (measure == null) {
            return null;
        }
        CurrencyValue result = ONERecordCargoUtil.create(CurrencyValue.class);
        CurrencyCode currencyCode = ONERecordCargoUtil.create(CurrencyCode.class);
        currencyCode.setId(Vocabulary.s_c_CurrencyCode + "_" + (measure.getCurrencyID() == null
            ? defaultCurrency
            : measure.getCurrencyID().value()));
        result.setCurrencyUnit(currencyCode);
        result.setNumericalValue(measure.getValue().doubleValue());
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
        Location location = ONERecordCargoUtil.create(Location.class);
        Set<CodeListElement> locationCodes =
            ONERecordCargoUtil.buildSet(createCodeListElementGeneral(value(locationCode)));
        location.setLocationCodes(locationCodes);
        location.setLocationType(value(typeCode));
        location.setLocationName(value(locationName));
        return location;
    }

    public static Country value(CountryIDType countryIDType, TextType countryName) {
        if (countryIDType == null) {
            return null;
        }
        Country country = ONERecordCargoUtil.create(Country.class);
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

    public static CodeListElement createCodeListElementGeneral(String code) {
        CodeListElement cle = ONERecordCargoUtil.create(CodeListElement.class);
        cle.setCode(code);
        return cle;
    }

    public static String determineModeCodeIRI(String modeCode) {

        String cclModeCodeBaseIRI = Vocabulary.s_c_ModeCode;
        switch (modeCode) {
            case "0":
                return cclModeCodeBaseIRI + "_TRANSPORT_MODE_NOT_SPECIFIED";
            case "1":
                return cclModeCodeBaseIRI + "_MARITIME_TRANSPORT";
            case "2":
                return cclModeCodeBaseIRI + "_RAIL_TRANSPORT";
            case "3":
                return cclModeCodeBaseIRI + "_ROAD_TRANSPORT";
            case "4":
                return cclModeCodeBaseIRI + "_AIR_TRANSPORT";
            case "5":
                return cclModeCodeBaseIRI + "_MAIL";
            case "6":
                return cclModeCodeBaseIRI + "_MULTIMODAL_TRANSPORT";
            case "7":
                return cclModeCodeBaseIRI + "_FIXED_TRANSPORT_INSTALLATION";
            case "8":
                return cclModeCodeBaseIRI + "_INLAND_WATER_TRANSPORT";
            case "9":
               return cclModeCodeBaseIRI + "_TRANSPORT_MODE_NOT_APPLICABLE";
            default:
                return null;
        }
    }

    public static String determineModeQualifierIRI(String modeQualifier) {
        String cclModeQualifierBaseIRI = "https://onerecord.iata.org/ns/cargo#";
        String modeQualifierAdapted = modeQualifier.toUpperCase(Locale.ENGLISH).replace("-", "_");
        return cclModeQualifierBaseIRI + modeQualifierAdapted;
    }

    public static String determinePartyRoleCodeIRI(String partyRoleCode) {

        String cclParticipantIdentifier = Vocabulary.s_c_ParticipantIdentifier;
        switch (partyRoleCode) {
            case "Agent":
                return cclParticipantIdentifier + "_AGT";
            case "Airline":
                return cclParticipantIdentifier + "_AIR";
            case "Airport Authority":
                return cclParticipantIdentifier + "_APT";
            case "Broker":
                return cclParticipantIdentifier + "_BRK";
            case "Commissionable Agent":
                return cclParticipantIdentifier + "_CAG";
            case "Consignee":
                return cclParticipantIdentifier + "_CNE";
            case "Customs":
                return cclParticipantIdentifier + "_CTM";
            case "Declarant":
                return cclParticipantIdentifier + "_DCL";
            case "Deconsolidator":
                return cclParticipantIdentifier + "_DEC";
            case "Freight Forwarder":
                return cclParticipantIdentifier + "_FFW";
            case "Ground Handling Agent":
                return cclParticipantIdentifier + "_GHA";
            case "Nominated freight company":
                // not included in coreCodeLists#ParticipantIdentifier
                return cclParticipantIdentifier + "_NOM";
            case "Notify Party":
                // not included in coreCodeLists#ParticipantIdentifier
                return cclParticipantIdentifier + "_NFY";
            case "Other Participant Identifier":
                // not included in coreCodeLists#ParticipantIdentifier
                return cclParticipantIdentifier + "_OPI";
            case "Post Office":
                return cclParticipantIdentifier + "_PTT";
            case "Shipper":
                return cclParticipantIdentifier + "_SHP";
            case "Trucker":
                return cclParticipantIdentifier + "_TRK";
            default:
                return null;
        }
    }

    public static String determineSpecialHandlingCodeIRI(String sphDescriptionCode) {
        SPH_DGR_SecSt sphDgrSecSt = SPH_DGR_SecSt.byCode(sphDescriptionCode);
        String result = null;
        if (sphDgrSecSt != null) {
            String iriPrefix = sphDgrSecSt.getIriPrefix();
            String code = sphDgrSecSt.getCode();
            result = iriPrefix + "_" + code;
        }
        return result;
    }
}
