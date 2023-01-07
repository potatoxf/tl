package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;

import java.util.Currency;

/**
 * 货币{@link Currency} 转换器
 *
 * @author potatoxf
 */
public class CurrencyConverter extends AbstractConverter<Currency> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Currency convertInternal(Object value) {
        return Currency.getInstance(convertToStr(value));
    }
}
