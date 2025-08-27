package com.riege.onerecord.converter;

import org.apache.commons.lang3.StringUtils;
import org.iata.onerecord.cargo.Vocabulary;

public enum SPH_DGR_SecSt {

    ACT("Active Temperature Controlled System", Vocabulary.s_c_SpecialHandlingCode),
    AOG("Aircraft on Ground", Vocabulary.s_c_SpecialHandlingCode),
    ATT("Goods Attached to Air Waybill", Vocabulary.s_c_SpecialHandlingCode),
    AVI("Live Animal", Vocabulary.s_c_SpecialHandlingCode),
    BIG("Outsized", Vocabulary.s_c_SpecialHandlingCode),
    BUP("Bulk Unitization Programme, Shipper/Consignee Handled Unit", Vocabulary.s_c_SpecialHandlingCode),
    CAO("Cargo Aircraft Only", Vocabulary.s_c_SpecialHandlingCode),
    CAT("Cargo Attendant Accompanying Shipment", Vocabulary.s_c_SpecialHandlingCode),
    CIC("Cargo may be loaded in the passenger cabin", Vocabulary.s_c_SpecialHandlingCode),
    COL("Cool Goods", Vocabulary.s_c_SpecialHandlingCode),
    COM("Company Mail", Vocabulary.s_c_SpecialHandlingCode),
    CRT("Control Room Temperature +15°C to +25°C", Vocabulary.s_c_SpecialHandlingCode),
    DIP("Diplomatic Mail", Vocabulary.s_c_SpecialHandlingCode),
    EAP("e-freight Consignment with Accompanying Paper Documents", Vocabulary.s_c_SpecialHandlingCode),
    EAT("Foodstuffs", Vocabulary.s_c_SpecialHandlingCode),
    EAW("e-freight Consignment with No Accompanying Paper Documents", Vocabulary.s_c_SpecialHandlingCode),
    ECC("Consignment established with an electronically concluded cargo contract with no accompanying paper airwaybill", Vocabulary.s_c_SpecialHandlingCode),
    ECP("Consignment established with a paper air waybill contract being printed under an e-AWB agreement", Vocabulary.s_c_SpecialHandlingCode),
    EMD("Electronic Monitoring Devices on/in Cargo/Container", Vocabulary.s_c_SpecialHandlingCode),
    ERT("Extended Room Temperature +2°C to +25°C", Vocabulary.s_c_SpecialHandlingCode),
    FIL("Undeveloped/Unexposed Film", Vocabulary.s_c_SpecialHandlingCode),
    FRI("Frozen Goods Subject to Veterinary/Phytosanitary Inspections", Vocabulary.s_c_SpecialHandlingCode),
    FRO("Frozen Goods", Vocabulary.s_c_SpecialHandlingCode),
    GOH("Hanging Garments", Vocabulary.s_c_SpecialHandlingCode),
    HEA("Heavy Cargo/150 kilograms and over per piece", Vocabulary.s_c_SpecialHandlingCode),
    HEG("Hatching Eggs", Vocabulary.s_c_SpecialHandlingCode),
    HUM("Human Remains in Coffin", Vocabulary.s_c_SpecialHandlingCode),
    LHO("Living Human Organs/Blood", Vocabulary.s_c_SpecialHandlingCode),
    LIC("License Required", Vocabulary.s_c_SpecialHandlingCode),
    MAL("Mail", Vocabulary.s_c_SpecialHandlingCode),
    MUW("Munitions of War", Vocabulary.s_c_SpecialHandlingCode),
    NST("Non Stackable Cargo", Vocabulary.s_c_SpecialHandlingCode),
    NWP("Newspapers, Magazines", Vocabulary.s_c_SpecialHandlingCode),
    OBX("Obnoxious Cargo", Vocabulary.s_c_SpecialHandlingCode),
    OHG("Overhang Item", Vocabulary.s_c_SpecialHandlingCode),
    PAC("Passenger and Cargo", Vocabulary.s_c_SpecialHandlingCode),
    PEA("Hunting trophies, skin, hide and all articles made from or"
        + " containing parts of species listed in the CITES (Convention on"
        + " International Trade in Endangered Species) appendices", Vocabulary.s_c_SpecialHandlingCode),
    PEB("Animal products for non-human consumption", Vocabulary.s_c_SpecialHandlingCode),
    PEF("Flowers", Vocabulary.s_c_SpecialHandlingCode),
    PEM("Meat", Vocabulary.s_c_SpecialHandlingCode),
    PEP("Fruits and Vegetables", Vocabulary.s_c_SpecialHandlingCode),
    PER("Perishable Cargo", Vocabulary.s_c_SpecialHandlingCode),
    PES("Fish/Seafood", Vocabulary.s_c_SpecialHandlingCode),
    PHY("Goods subject to phytosanitary inspections", Vocabulary.s_c_SpecialHandlingCode),
    PIL("Pharmaceuticals", Vocabulary.s_c_SpecialHandlingCode),
    PIP("Passive Insulated Packaging", Vocabulary.s_c_SpecialHandlingCode),
    QRT("Quick Ramp Transfer", Vocabulary.s_c_SpecialHandlingCode),
    RAC("Reserved Air Cargo", Vocabulary.s_c_SpecialHandlingCode),
    RDS("Diagnostic Specimens", Vocabulary.s_c_SpecialHandlingCode),
    REQ("Excepted Quantities of Dangerous Goods", Vocabulary.s_c_SpecialHandlingCode),
    RRE("Excepted Quantities of Radioactive Material", Vocabulary.s_c_SpecialHandlingCode),
    SHL("Save Human Life", Vocabulary.s_c_SpecialHandlingCode),
    SPF("Laboratory Animals", Vocabulary.s_c_SpecialHandlingCode),
    SUR("Surface Transportation", Vocabulary.s_c_SpecialHandlingCode),
    SWP("Sporting Weapons", Vocabulary.s_c_SpecialHandlingCode),
    VAL("Valuable Cargo", Vocabulary.s_c_SpecialHandlingCode),
    VIC("Very Important Cargo", Vocabulary.s_c_SpecialHandlingCode),
    VOL("Volume", Vocabulary.s_c_SpecialHandlingCode),
    VUN("Vulnerable Cargo", Vocabulary.s_c_SpecialHandlingCode),
    WET("Shipments of Wet Material not Packed in Watertight Containers", Vocabulary.s_c_SpecialHandlingCode),
    XPS("Priority Small Package", Vocabulary.s_c_SpecialHandlingCode),
    EBI("Lithium ion batteries excepted as per Section II of PI 965", Vocabulary.s_c_DangerousGoodsCode),
    EBM("Lithium metal batteries excepted as per Section II of PI 968", Vocabulary.s_c_DangerousGoodsCode),
    ELI("Lithium ion batteries otherwise excepted from the IATA DGR", Vocabulary.s_c_DangerousGoodsCode),
    ELM("Lithium metal batteries otherwise excepted from the IATA DGR", Vocabulary.s_c_DangerousGoodsCode),
    ICE("Dry Ice", Vocabulary.s_c_DangerousGoodsCode),
    MAG("Magnetized Material", Vocabulary.s_c_DangerousGoodsCode),
    RBI("Fully regulated lithium ion batteries (Class 9, UN 3480) as per Section IA and IB of PI 965", Vocabulary.s_c_DangerousGoodsCode),
    RBM("Fully regulated lithium metal batteries (Class 9, UN 3090) as per Section IA and IB of PI 968", Vocabulary.s_c_DangerousGoodsCode),
    RCL("Cryogenic Liquids", Vocabulary.s_c_DangerousGoodsCode),
    RCM("Corrosive", Vocabulary.s_c_DangerousGoodsCode),
    RCX("Explosives 1.3C", Vocabulary.s_c_DangerousGoodsCode),
    REX("To be reserved for normally forbidden Explosives, Divisions 1.1, 1.2, 1.3, 1.4F, 1.5 and 1.6", Vocabulary.s_c_DangerousGoodsCode),
    RFG("Flammable Gas", Vocabulary.s_c_DangerousGoodsCode),
    RFL("Flammable Liquid", Vocabulary.s_c_DangerousGoodsCode),
    RFS("Flammable Solid", Vocabulary.s_c_DangerousGoodsCode),
    RFW("Dangerous When Wet", Vocabulary.s_c_DangerousGoodsCode),
    RGX("Explosives 1.3G", Vocabulary.s_c_DangerousGoodsCode),
    RIS("Infectious Substance", Vocabulary.s_c_DangerousGoodsCode),
    RLI("Fully Regulated Lithium Ion Batteries (Class 9)", Vocabulary.s_c_DangerousGoodsCode),
    RLM("Fully Regulated Lithium Metal Batteries (Class 9)", Vocabulary.s_c_DangerousGoodsCode),
    RMD("Miscellaneous Dangerous Goods", Vocabulary.s_c_DangerousGoodsCode),
    RNG("Non-Flammable Non-Toxic Gas", Vocabulary.s_c_DangerousGoodsCode),
    ROP("Organic Peroxide", Vocabulary.s_c_DangerousGoodsCode),
    ROX("Oxidizer", Vocabulary.s_c_DangerousGoodsCode),
    RPB("Toxic Substance", Vocabulary.s_c_DangerousGoodsCode),
    RPG("Toxic Gas", Vocabulary.s_c_DangerousGoodsCode),
    RSB("Polymeric Beads", Vocabulary.s_c_DangerousGoodsCode),
    RRW("Radioactive Material Category I-White", Vocabulary.s_c_DangerousGoodsCode),
    RRY("Radioactive Material Categories II-Yellow and III-Yellow", Vocabulary.s_c_DangerousGoodsCode),
    RSC("Spontaneously Combustible", Vocabulary.s_c_DangerousGoodsCode),
    RXB("Explosives 1.4B", Vocabulary.s_c_DangerousGoodsCode),
    RXC("Explosives 1.4C", Vocabulary.s_c_DangerousGoodsCode),
    RXD("Explosives 1.4D", Vocabulary.s_c_DangerousGoodsCode),
    RXE("Explosives 1.4E", Vocabulary.s_c_DangerousGoodsCode),
    RXG("Explosives 1.4G", Vocabulary.s_c_DangerousGoodsCode),
    RXS("Explosives 1.4S", Vocabulary.s_c_DangerousGoodsCode),
    HR("HIGH RISK", Vocabulary.s_c_SecurityStatus),
    NSC("Not secure", Vocabulary.s_c_SecurityStatus),
    SCO("Secure for cargo only", Vocabulary.s_c_SecurityStatus),
    SPX("Secure also for passenger flights", Vocabulary.s_c_SecurityStatus),
    SHR("Secure according to high risk requirements", Vocabulary.s_c_SecurityStatus);

    private final String meaning;
    private final String iriPrefix;


    SPH_DGR_SecSt(String meaning, String iriPrefix) {
        this.meaning = meaning;
        this.iriPrefix = iriPrefix;
    }

    public String getCode() {
        return name();
    }

    public String getMeaning() {
        return meaning;
    }

    public String getIriPrefix() {
        return iriPrefix;
    }

    public static SPH_DGR_SecSt byCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            return valueOf(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


}
