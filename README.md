# Cargo-XML XFWB to ONE Record Converter
Note: A live demo of this converter is available at https://onerecord.riege.com/

## Release versioning and IATA Ontology versions

The IATA Ontology version is reflected by the version of the Riege [one-record-ontologymodel library](https://github.com/riege/one-record-ontologymodel).

Versions of the converter library:
* 0.9.x / `branch_0.9`: based upon IATA Ontology 1.1, the ONE Record datamodel as per June 2021, see https://github.com/IATA-Cargo/ONE-Record/tree/master/June-2021-standard-COTB-endorsed.
  Uses one-record-ontologymodel version 1.1.x
* current development / `main`: based on IATA ONE Record datamodel working draft, see https://github.com/IATA-Cargo/ONE-Record/tree/master/working_draft/ontology

## General Backgound Information
This converter intentionally does neither set IDs nor makes use of persisted data for linked-data purposes.

The converter is based on two main data structures to convert from Cargo-XML to ONE Record:

* For parsing Cargo-XML the converter uses Java classes which had been generated from the 
  Cargo-XML XFWB3 schema. These generated classes are included via library
  https://github.com/riege/cargoxml-jaxb.
  Please note that the cargoxml-jaxb package does not contain any schema information from the IATA Cargo-XML
  Toolkit.
  Please also note that the schema information from 
  the _IATA Cargo-XML Toolkit_ is published on 
  the _IATA Cargo-XML validation portal_ available at
  https://cargo-xml-autocheck.iata.org/Docs/IATA/XML%20Waybill%20Message%20XFWB%203.00/011.htm
* For generating ONE Record Logistics Object structures, IATA provides Java classes 
  in project https://github.com/IATA-Cargo/one-record-server-java 
  (see https://github.com/IATA-Cargo/one-record-server-java/tree/master/src/main/generated-sources/org/iata/cargo).
  These classes have been updated with recently IATA endorsed Ontology via the project 
  https://github.com/riege/one-record-ontologymodel which is used by this converter.

Codes and units are copied 1:1 from the provided Cargo-XML message where applicable.

Line breaks are respected for some fields if provided in XML, e.g. for multi-line goods description or address name/street. Line breaks are intentionally used in GoodsDescription to preserve and indicate descriptions from more than one field if applicaple from original XML

At the moment the converted is limited to map the Cargo-XML XFWB3 message to ONE Record JSON. 
Please note that the XFWB3 to 1R converter is not mapping all possible data yet and has focus on a XFWB message from a forwarder to an airline

## Usage

### Programming 
The main Cargo-XML class for XFWB3 is `com.riege.cargoxml.schema.xfwb3.WaybillType`.

The `com.riege.onerecord.converter.XFWB3toOneRecordConverter` converts a provided 
Cargo-XML XFWB `WaybillType`
into validation results plus hints and especially into ONE Record logistics data model
`org.iata.cargo.model.Waybill`:

    InputStream is = ...
    WaybillType xfwb = new ConverterUtil().unmarshalXFWB3(is);
    XFWB3toOneRecordConverter converter = new XFWB3toOneRecordConverter(xfwb);
    for (ValidationMessage msg : converter.getValidationHints()) {
        System.out.println("HINT: " + msg.getMessage());
    }
    for (ValidationMessage msg : converter.getValidationWarnings()) {
        System.out.println("WARNING: " + msg.getMessage());
    }
    for (ValidationMessage msg : converter.getValidationErrors()) {
        System.out.println("ERROR: " + msg.getMessage());
    }
    Waybill oneRecordWaybill = converter.getOneRecordResult();

The `Waybill` can be serialized easily into JSON, e.g. with https://github.com/FasterXML/jackson 


### Library usage
The converter library is not published on mavenCentral (yet).

Nevertheless releases are available for download at https://github.com/riege/one-record-converter/releases 
and it can be also integrated into a Java project as a depencency via https://jitpack.io:

[gradle](https://gradle.org/):

    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }

    dependencies {
      implementation 'com.github.riege:one-record-converter:0.9.0'
    }

[maven](https://maven.apache.org):

    <dependency>
      <groupId>com.github.riege</groupId>
      <artifactId>one-record-converter</artifactId>
      <version>0.9.0</version>
    </dependency>

See https://jitpack.io/#riege/one-record-converter for more details.

### Java version
**Update for version 0.9 and above**: The one-record-converter published jar includes 
the required Ontologymodel classes, Cargo-XML JAXB classes as well as 
`jakarta.xml.bind-api` and `jaxb-impl` classes. 


**Only for version 0.8 and older**:
The converter uses Jakarta XML Binding (JAXB), which was part of 
the Java Enterprise Edition with Java version 8,
deprecated in the following Java versions and finally removed in Java 11.

When this library is used with Java 11 or younger, a dependency needs to be added,
e.g. for Java EE 8, in use with maven:

    <dependency>
      <groupId>jakarta.xml.bind</groupId>
      <artifactId>jakarta.xml.bind-api</artifactId>
      <version>2.3.3</version>
    </dependency>
    <dependency>
      <groupId>com.sun.xml.bind</groupId>
      <artifactId>jaxb-impl</artifactId>
      <version>2.3.5</version>
      <scope>runtime</scope>
    </dependency>
