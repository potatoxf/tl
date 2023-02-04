package pxf.tlx.basesystem;

import pxf.tl.api.Literal;

/**
 * @author potatoxf
 */
public enum TaskStatus implements Literal<TaskStatus> {
    TASK_OK(200),
    TASK_FAIL(500),
    TASK_INIT(600),
    TASK_NO_EXIST(1001),
    TASK_PARAM_EMPTY(6001),
    TASK_FROZEN(10001),
    TASK_UNFROZEN(10002),
    TASK_RUN_NOW_FAIL(7001),
    TASK_HTTP(10003),
    TASK_KAFKA(10004),
    TASK_UPDATE_FAIL(1002),
    TASK_NO_DATA(1003);

    private final int code;

    TaskStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
