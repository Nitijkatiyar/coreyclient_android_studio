/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.coremobile.coreyhealth.aesencryption;

/**
 * <p>
 * The javaType binding declaration can be used to customize the binding of
 * an XML schema datatype to a Java datatype. Customizations can involve
 * writing a parse and print method for parsing and printing lexical
 * representations of a XML schema datatype respectively. However, writing
 * parse and print methods requires knowledge of the lexical representations (
 * <a href="http://www.w3.org/TR/xmlschema-2/"> XML Schema Part2: Datatypes
 * specification </a>) and hence may be difficult to write.
 * </p>
 * <p>
 * This class makes it easier to write parse and print methods. It defines
 * static parse and print methods that provide access to a JAXB provider's
 * implementation of parse and print methods. These methods are invoked by
 * custom parse and print methods. For example, the binding of xsd:dateTime
 * to a long can be customized using parse and print methods as follows:
 * <blockquote>
 *    <pre>
 *    // Customized parse method
 *    public long myParseCal( String dateTimeString ) {
 *        java.util.Calendar cal = DatatypeConverter.parseDateTime(dateTimeString);
 *        long longval = convert_calendar_to_long(cal); //application specific
 *        return longval;
 *    }
 *
 *    // Customized print method
 *    public String myPrintCal( Long longval ) {
 *        java.util.Calendar cal = convert_long_to_calendar(longval) ; //application specific
 *        String dateTimeString = DatatypeConverter.printDateTime(cal);
 *        return dateTimeString;
 *    }
 *    </pre>
 * </blockquote>
 * <p>
 * There is a static parse and print method corresponding to each parse and
 * print method respectively in the {@link DatatypeConverterInterface
 * DatatypeConverterInterface}.
 * <p>
 * The static methods defined in the class can also be used to specify
 * a parse or a print method in a javaType binding declaration.
 * </p>
 * <p>
 * JAXB Providers are required to call the
 * {@link #setDatatypeConverter(DatatypeConverterInterface)
 * setDatatypeConverter} api at some point before the first marshal or unmarshal
 * operation (perhaps during the call to JAXBContext.newInstance).  This step is
 * necessary to configure the converter that should be used to perform the
 * print and parse functionality.
 * </p>
 *
 * <p>
 * A print method for a XML schema datatype can output any lexical
 * representation that is valid with respect to the XML schema datatype.
 * If an error is encountered during conversion, then an IllegalArgumentException,
 * or a subclass of IllegalArgumentException must be thrown by the method.
 * </p>
 *
 * @author <ul><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems Inc.</li><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Ryan Shoemaker,Sun Microsystems Inc.</li></ul>
 * @version $Revision: 1.4 $
 * @see DatatypeConverterInterface
 * @see ParseConversionEvent
 * @see PrintConversionEvent
 * @since JAXB1.0
 */

final public class DatatypeConverter {

    // delegate to this instance of DatatypeConverter
    private static DatatypeConverterInterface theConverter = new DatatypeConverterImpl();

    private DatatypeConverter() {
        // private constructor
    }

    /**
     * This method is for JAXB provider use only.
     * <p>
     * JAXB Providers are required to call this method at some point before
     * allowing any of the JAXB client marshal or unmarshal operations to
     * occur.  This is necessary to configure the datatype converter that
     * should be used to perform the print and parse conversions.
     *
     * <p>
     * Calling this api repeatedly will have no effect - the
     * DatatypeConverterInterface instance passed into the first invocation is
     * the one that will be used from then on.
     *
     * @param converter an instance of a class that implements the
     * DatatypeConverterInterface class - this parameter must not be null.
     * @throws IllegalArgumentException if the parameter is null
     */
    public static void setDatatypeConverter( DatatypeConverterInterface converter ) {
        if( converter == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.CONVERTER_MUST_NOT_BE_NULL ) );
        } else if( theConverter == null ) {
            theConverter = converter;
        }
    }

    /**
     * <p>
     * Convert the lexical XSD string argument into a String value.
     * @param lexicalXSDString
     *     A string containing a lexical representation of
     *     xsd:string.
     * @return
     *     A String value represented by the string argument.
     */
    public static String parseString( String lexicalXSDString ) {
        return theConverter.parseString( lexicalXSDString );
    }

    

    /**
     * <p>
     * Converts the string argument into an array of bytes.
     * @param lexicalXSDBase64Binary
     *     A string containing lexical representation
     *     of xsd:base64Binary.
     * @return
     *     An array of bytes represented by the string argument.
     * @throws IllegalArgumentException if string parameter does not conform to lexical value space defined in XML Schema Part 2: Datatypes for xsd:base64Binary
     */
    public static byte[] parseBase64Binary( String lexicalXSDBase64Binary ) {
        return theConverter.parseBase64Binary( lexicalXSDBase64Binary );
    }

    /**
     * <p>
     * Converts the string argument into an array of bytes.
     * @param lexicalXSDHexBinary
     *     A string containing lexical representation of
     *     xsd:hexBinary.
     * @return
     *     An array of bytes represented by the string argument.
     * @throws IllegalArgumentException if string parameter does not conform to lexical value space defined in XML Schema Part 2: Datatypes for xsd:hexBinary.
     */
   public static byte[] parseHexBinary( String lexicalXSDHexBinary ) {
        return theConverter.parseHexBinary( lexicalXSDHexBinary );
    }

   
    /**
     * <p>
     * Converts an array of bytes into a string.
     * @param val
     *     An array of bytes
     * @return
     *     A string containing a lexical representation of xsd:base64Binary
     * @throws IllegalArgumentException if <tt>val</tt> is null.
     */
    public static String printBase64Binary( byte[] val ) {
        return theConverter.printBase64Binary( val );
    }

    /**
     * <p>
     * Converts an array of bytes into a string.
     * @param val
     *     An array of bytes
     * @return
     *     A string containing a lexical representation of xsd:hexBinary
     * @throws IllegalArgumentException if <tt>val</tt> is null.
     */
    public static String printHexBinary( byte[] val ) {
        return theConverter.printHexBinary( val );
    }

   
}
