package pxf.tl.bloomfilter.filter;


import pxf.tl.util.ToolHash;

public class FNVFilter extends FuncFilter {
    private static final long serialVersionUID = 1L;

    public FNVFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public FNVFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, ToolHash::fnvHash);
    }
}
