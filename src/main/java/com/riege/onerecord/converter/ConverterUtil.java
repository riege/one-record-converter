package com.riege.onerecord.converter;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import com.riege.cargoxml.schema.xfwb3.WaybillType;
import com.riege.cargoxml.schema.xfzb3.HouseWaybillType;

public final class ConverterUtil {

    private Unmarshaller unmarshallerXFWB3;
    private Unmarshaller unmarshallerXFZB3;

    private Unmarshaller getXFWB3Unmarshaller() throws JAXBException {
        if (unmarshallerXFWB3 == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(WaybillType.class.getPackage().getName());
            unmarshallerXFWB3 = jaxbContext.createUnmarshaller();
        }
        return unmarshallerXFWB3;
    }

    private Unmarshaller getXFZB3Unmarshaller() throws JAXBException {
        if (unmarshallerXFZB3 == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(HouseWaybillType.class.getPackage().getName());
            unmarshallerXFZB3 = jaxbContext.createUnmarshaller();
        }
        return unmarshallerXFZB3;
    }

    /**
     * Deprecated in version 1.0, use unmarshallXFWB3 instead
     */
    @Deprecated
    public WaybillType unmarshalXFWB3(InputStream inputStream) throws JAXBException {
        return unmarshallXFWB3(inputStream);
    }

    public WaybillType unmarshallXFWB3(InputStream inputStream) throws JAXBException {
        Object result = getXFWB3Unmarshaller().unmarshal(inputStream);
        if (!(result instanceof JAXBElement)) {
            throw new UnmarshalException("Unexpected class " + result.getClass().getName()
                + " while unmarshalling, expected JAXBElement");
        }
        Object cargoxml = ((JAXBElement)result).getValue();
        if (!(cargoxml instanceof WaybillType)) {
            throw new UnmarshalException("No CargoXML XFWB3 format detected");
        }
        return (WaybillType) cargoxml;
    }

    public HouseWaybillType unmarshallXFZB3(InputStream inputStream) throws JAXBException {
        Object result = getXFZB3Unmarshaller().unmarshal(inputStream);
        if (!(result instanceof JAXBElement)) {
            throw new UnmarshalException("Unexpected class " + result.getClass().getName()
                + " while unmarshalling, expected JAXBElement");
        }
        Object cargoxml = ((JAXBElement)result).getValue();
        if (!(cargoxml instanceof HouseWaybillType)) {
            throw new UnmarshalException("No CargoXML XFWZB3 format detected");
        }
        return (HouseWaybillType) cargoxml;
    }

    public static boolean isNullOrEmpty( final Collection< ? > c ) {
        return c == null || c.isEmpty();
    }

    public static boolean isNullOrEmpty( final Map< ?, ? > m ) {
        return m == null || m.isEmpty();
    }

    public static boolean hasElements( final Collection< ? > c ) {
        return !isNullOrEmpty(c);
    }

    public static boolean hasElements( final Map< ?, ? > m ) {
        return !isNullOrEmpty(m);
    }

}
