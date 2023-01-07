package pxf.tlx.codec;

import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.Encoder;
import pxf.tl.api.Charsets;

/**
 * @author potatoxf
 */
public interface ConvertStringCoder<T> extends Decoder, Encoder {

    /**
     * @return the true to support same type convert,or false
     */
    boolean isSupportSame();

    /**
     * @param source   the source string
     * @param charsets the charset
     * @return to object
     */
    T convert(String source, Charsets charsets);

    /**
     * @param source   the source object
     * @param charsets the charset
     * @return to string
     */
    String convert(T source, Charsets charsets);

    /**
     * Decodes an "encoded" Object and returns a "decoded" Object. Note that the implementation of this interface will
     * try to cast the Object parameter to the specific type expected by a particular Decoder implementation. If a
     * {@link ClassCastException} occurs this decode method will throw a RuntimeException.
     *
     * @param source the object to decode
     * @return a 'decoded" object
     * @throws RuntimeException a decoder exception can be thrown for any number of reasons. Some good candidates are that the
     *                          parameter passed to this method is null, a param cannot be cast to the appropriate type for a
     *                          specific encoder.
     */
    T decodeTargetThrow(T source) throws Throwable;

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * {@code byte[]} or {@code String}s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     * @throws RuntimeException An encoder exception is thrown if the encoder experiences a failure condition during the encoding
     *                          process.
     */
    T encodeTargetThrow(T source) throws Throwable;

    /**
     * Decodes an "encoded" Object and returns a "decoded" Object. Note that the implementation of this interface will
     * try to cast the Object parameter to the specific type expected by a particular Decoder implementation. If a
     * {@link ClassCastException} occurs this decode method will throw a RuntimeException.
     *
     * @param source the object to decode
     * @return a 'decoded" object
     * @throws RuntimeException a decoder exception can be thrown for any number of reasons. Some good candidates are that the
     *                          parameter passed to this method is null, a param cannot be cast to the appropriate type for a
     *                          specific encoder.
     */
    default T decodeTarget(T source) {
        try {
            return decodeTargetThrow(source);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * {@code byte[]} or {@code String}s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     * @throws RuntimeException An encoder exception is thrown if the encoder experiences a failure condition during the encoding
     *                          process.
     */
    default T encodeTarget(T source) {
        try {
            return encodeTargetThrow(source);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String decodeString(String source) {
        return decodeString(source, Charsets.defaultCharsets());
    }

    /**
     * Encodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String encodeString(String source) {
        return encodeString(source, Charsets.defaultCharsets());
    }

    /**
     * Decodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String decodeString(String source, Charsets charsets) {
        return convert(decodeTarget(convert(source, charsets)), charsets);
    }

    /**
     * Encodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String encodeString(String source, Charsets charsets) {
        return convert(encodeTarget(convert(source, charsets)), charsets);
    }

    /**
     * Decodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default T decodeToTarget(String source) {
        return decodeToTarget(source, Charsets.defaultCharsets());
    }

    /**
     * Encodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default T encodeToTarget(String source) {
        return encodeToTarget(source, Charsets.defaultCharsets());
    }

    /**
     * Decodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default T decodeToTarget(String source, Charsets charsets) {
        return decodeTarget(convert(source, charsets));
    }

    /**
     * Encodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default T encodeToTarget(String source, Charsets charsets) {
        return encodeTarget(convert(source, charsets));
    }

    /**
     * Decodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String decodeFromTarget(T source) {
        return decodeFromTarget(source, Charsets.defaultCharsets());
    }

    /**
     * Encodes source
     *
     * @param source the source to encode
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String encodeFromTarget(T source) {
        return encodeFromTarget(source, Charsets.defaultCharsets());
    }

    /**
     * Decodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String decodeFromTarget(T source, Charsets charsets) {
        return convert(decodeTarget(source), charsets);
    }

    /**
     * Encodes source
     *
     * @param source   the source to encode
     * @param charsets the string charset
     * @return the encoded result
     * @throws RuntimeException thrown if there is an error condition during the encoding process.
     */
    default String encodeFromTarget(T source, Charsets charsets) {
        return convert(encodeTarget(source), charsets);
    }

    /**
     * Decodes an "encoded" Object and returns a "decoded" Object. Note that the implementation of this interface will
     * try to cast the Object parameter to the specific type expected by a particular Decoder implementation. If a
     * {@link ClassCastException} occurs this decode method will throw a RuntimeException.
     *
     * @param source the object to decode
     * @return a 'decoded" object
     * @throws RuntimeException a decoder exception can be thrown for any number of reasons. Some good candidates are that the
     *                          parameter passed to this method is null, a param cannot be cast to the appropriate type for a
     *                          specific encoder.
     */
    @Override
    default Object decode(Object source) {
        if (source instanceof String string) {
            if (isSupportSame()) {
                return decodeString(string, Charsets.defaultCharsets());
            } else {
                return decodeToTarget(string, Charsets.defaultCharsets());
            }
        } else {
            if (isSupportSame()) {
                return decodeString(null, Charsets.defaultCharsets());
            } else {
                return decodeToTarget(null, Charsets.defaultCharsets());
            }
        }
    }

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * {@code byte[]} or {@code String}s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     * @throws RuntimeException An encoder exception is thrown if the encoder experiences a failure condition during the encoding
     *                          process.
     */
    @Override
    default Object encode(Object source) {
        if (source instanceof String string) {
            if (isSupportSame()) {
                return encodeString(string, Charsets.defaultCharsets());
            } else {
                return encodeToTarget(string, Charsets.defaultCharsets());
            }
        } else {
            if (isSupportSame()) {
                return decodeString(null, Charsets.defaultCharsets());
            } else {
                return decodeToTarget(null, Charsets.defaultCharsets());
            }
        }
    }
}
