package pxf.tl.bloomfilter.filter;


import pxf.tl.util.ToolHash;

public class SDBMFilter extends FuncFilter {
    private static final long serialVersionUID = 1L;

    public SDBMFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public SDBMFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, ToolHash::sdbmHash);
    }
}
