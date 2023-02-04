package pxf.tlx.basesystem;

import pxf.tl.api.Literal;

/**
 * @author potatoxf
 */
public enum ResponseResultStatus implements Literal<ResponseResultStatus> {
    /**
     * 成功码
     */
    OK,
    /**
     * 失败码
     */
    FAIL;
    /**
     * 成功
     */
    public static final String MESSAGE_OK = "OK";
    /**
     * 失败
     */
    public static final String MESSAGE_FAIL = "FAIL";
    /**
     * 成功码
     */
    public static final int CODE_OK = 0;
    /**
     * 失败码
     */
    public static final int CODE_FAIL = 1;
}
