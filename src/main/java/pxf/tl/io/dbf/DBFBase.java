/*
	$Id: DBFBase.java,v 1.3 2004/03/31 15:59:40 anil Exp $
	Serves as the base class of DBFReader adn DBFWriter.

	@author: anil@linuxense.com

	Support for choosing implemented character Sets as
	suggested by Nick Voznesensky <darkers@mail.ru>
*/
/**
 * Base class for DBFReader and DBFWriter.
 */
package pxf.tl.io.dbf;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;

public abstract class DBFBase {

    public static final int ALIGN_LEFT = 10;
    public static final int ALIGN_RIGHT = 12;
    protected final int END_OF_DATA = 0x1A;
    private String charsetName = "8859_1";

    public static int readLittleEndianInt(DataInput in) throws IOException {

        int bigEndian = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {

            bigEndian |= (in.readUnsignedByte() & 0xff) << shiftBy;
        }

        return bigEndian;
    }

    public static short readLittleEndianShort(DataInput in) throws IOException {

        int low = in.readUnsignedByte() & 0xff;
        int high = in.readUnsignedByte();

        return (short) (high << 8 | low);
    }

    public static byte[] trimLeftSpaces(byte[] arr) {

        StringBuilder t_sb = new StringBuilder(arr.length);

        for (byte b : arr) {

            if (b != ' ') {

                t_sb.append((char) b);
            }
        }

        return t_sb.toString().getBytes();
    }

    public byte[] textPadding(String text, int length) throws UnsupportedEncodingException {
        return textPadding(text, length, ALIGN_LEFT);
    }

    public byte[] textPadding(String text, int length, int alignment)
            throws UnsupportedEncodingException {
        return textPadding(text, length, alignment, (byte) ' ');
    }

    public byte[] textPadding(String text, int length, int alignment, byte paddingByte)
            throws UnsupportedEncodingException {
        if (text.length() >= length) {

            return text.substring(0, length).getBytes(charsetName);
        }

        byte[] byte_array = new byte[length];
        Arrays.fill(byte_array, paddingByte);

        switch (alignment) {
            case ALIGN_LEFT:
                System.arraycopy(text.getBytes(charsetName), 0, byte_array, 0, text.length());
                break;

            case ALIGN_RIGHT:
                int t_offset = length - text.length();
                System.arraycopy(text.getBytes(charsetName), 0, byte_array, t_offset, text.length());
                break;
        }

        return byte_array;
    }

    public byte[] doubleFormatting(Double doubleNum, int fieldLength, int sizeDecimalPart)
            throws UnsupportedEncodingException {

        int sizeWholePart = fieldLength - (sizeDecimalPart > 0 ? (sizeDecimalPart + 1) : 0);

        StringBuilder format = new StringBuilder(fieldLength);

        for (int i = 0; i < sizeWholePart; i++) {

            format.append("#");
        }

        if (sizeDecimalPart > 0) {

            format.append(".");

            for (int i = 0; i < sizeDecimalPart; i++) {

                format.append("0");
            }
        }

        DecimalFormat df = new DecimalFormat(format.toString());

        return textPadding(df.format(doubleNum.doubleValue()), fieldLength, ALIGN_RIGHT);
    }

    /*
     If the library is used in a non-latin environment use this method to set
     corresponding character set. More information:
     http://www.iana.org/assignments/character-sets
     Also see the documentation of the class java.nio.charset.Charset
    */
    public String getCharsetName() {

        return this.charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String newString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, charsetName);
    }
}
