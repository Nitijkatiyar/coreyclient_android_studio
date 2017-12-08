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
 * The DatatypeConverterInterface is for JAXB provider use only. A
 * JAXB provider must supply a class that implements this interface.
 * JAXB Providers are required to call the
 * {@link DatatypeConverter#setDatatypeConverter(DatatypeConverterInterface)
 * DatatypeConverter.setDatatypeConverter} api at
 * some point before the first marshal or unmarshal operation (perhaps during
 * the call to JAXBContext.newInstance).  This step is necessary to configure
 * the converter that should be used to perform the print and parse
 * functionality.  Calling this api repeatedly will have no effect - the
 * DatatypeConverter instance passed into the first invocation is the one that
 * will be used from then on.
 * </p>
 * <p>
 * <p>
 * This interface defines the parse and print methods. There is one
 * parse and print method for each XML schema datatype specified in the
 * the default binding Table 5-1 in the JAXB specification.
 * </p>
 * <p>
 * <p>
 * The parse and print methods defined here are invoked by the static parse
 * and print methods defined in the {@link DatatypeConverter DatatypeConverter}
 * class.
 * </p>
 * <p>
 * <p>
 * A parse method for a XML schema datatype must be capable of converting any
 * lexical representation of the XML schema datatype ( specified by the
 * <a href="http://www.w3.org/TR/xmlschema-2/"> XML Schema Part2: Datatypes
 * specification</a> into a value in the value space of the XML schema datatype.
 * If an error is encountered during conversion, then an IllegalArgumentException
 * or a subclass of IllegalArgumentException must be thrown by the method.
 * <p>
 * </p>
 * <p>
 * <p>
 * A print method for a XML schema datatype can output any lexical
 * representation that is valid with respect to the XML schema datatype.
 * If an error is encountered during conversion, then an IllegalArgumentException,
 * or a subclass of IllegalArgumentException must be thrown by the method.
 * </p>
 * <p>
 * The prefix xsd: is used to refer to XML schema datatypes
 * <a href="http://www.w3.org/TR/xmlschema-2/"> XML Schema Part2: Datatypes
 * specification.</a>
 * <p>
 * <p>
 *
 * @author <ul><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems Inc.</li><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Ryan Shoemaker,Sun Microsystems Inc.</li></ul>
 */

public interface DatatypeConverterInterface {

    public String parseString(String lexicalXSDString);

    /**
     * <p>
     * Converts the string argument into an array of bytes.
     *
     * @param lexicalXSDBase64Binary A string containing lexical representation
     *                               of xsd:base64Binary.
     * @return An array of bytes represented by the string argument.
     * @throws IllegalArgumentException if string parameter does not conform to lexical value space defined in XML Schema Part 2: Datatypes for xsd:base64Binary
     */
    public byte[] parseBase64Binary(String lexicalXSDBase64Binary);

    /**
     * <p>
     * Converts the string argument into an array of bytes.
     *
     * @param lexicalXSDHexBinary A string containing lexical representation of
     *                            xsd:hexBinary.
     * @return An array of bytes represented by the string argument.
     * @throws IllegalArgumentException if string parameter does not conform to lexical value space defined in XML Schema Part 2: Datatypes for xsd:hexBinary.
     */
    public byte[] parseHexBinary(String lexicalXSDHexBinary);


    /**
     * <p>
     * Converts an array of bytes into a string.
     *
     * @param val an array of bytes
     * @return A string containing a lexical representation of xsd:base64Binary
     * @throws IllegalArgumentException if <tt>val</tt> is null.
     */
    public String printBase64Binary(byte[] val);

    /**
     * <p>
     * Converts an array of bytes into a string.
     *
     * @param val an array of bytes
     * @return A string containing a lexical representation of xsd:hexBinary
     * @throws IllegalArgumentException if <tt>val</tt> is null.
     */
    public String printHexBinary(byte[] val);


}
