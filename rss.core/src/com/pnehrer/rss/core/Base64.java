/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/

package com.pnehrer.rss.core;

/**
 * 
 * Encode/decode byte arrays into base64 strings. Code originally
 * acquired from
 * ftp://ftp.ora.com/pub/examples/java/crypto/files/oreilly/jonathan/util/
 * 
 * Several small modifications were made: the '_' character was substituted for
 * the '/' character, the '-' char substituted for the '=' char, and the '.'
 * substituted for the '+' char so that the resulting string does not use any of
 * the reserved characters in the URI reserved character set as described in
 * RFC2396. See ftp://ftp.isi.edu/in-notes/rfc2396.txt for details.
 * 
 */
final class Base64 {

    /**
     * Encode a byte array into a String
     * 
     * @param raw
     *            the raw data to encode
     * @return String that is base64 encoded
     */
    public static String encode(byte[] raw) {
        if (raw == null)
            throw new NumberFormatException("Input data cannot be null");

        StringBuffer encoded = new StringBuffer();
        for (int i = 0; i < raw.length; i += 3) {
            encoded.append(encodeBlock(raw, i));
        }
        return encoded.toString();
    }

    protected static char[] encodeBlock(byte[] raw, int offset) {
        int block = 0;
        int slack = raw.length - offset - 1;
        int end = (slack >= 2) ? 2 : slack;
        for (int i = 0; i <= end; i++) {
            byte b = raw[offset + i];
            int neuter = (b < 0) ? b + 256 : b;
            block += neuter << (8 * (2 - i));
        }
        char[] base64 = new char[4];
        for (int i = 0; i < 4; i++) {
            int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
            base64[i] = getChar(sixbit);
        }
        // modify to use '-' instead of '='
        if (slack < 1)
            base64[2] = '-';
        if (slack < 2)
            base64[3] = '-';
        return base64;
    }

    protected static char getChar(int sixBit) {
        if (sixBit >= 0 && sixBit <= 25)
            return (char) ('A' + sixBit);
        if (sixBit >= 26 && sixBit <= 51)
            return (char) ('a' + (sixBit - 26));
        if (sixBit >= 52 && sixBit <= 61)
            return (char) ('0' + (sixBit - 52));
        if (sixBit == 62)
            return '.';
        // modify to use '_' instead of '/'
        if (sixBit == 63)
            return '_';
        return '?';
    }
}