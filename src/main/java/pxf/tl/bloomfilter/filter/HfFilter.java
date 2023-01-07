package pxf.tl.bloomfilter.filter;


import pxf.tl.util.ToolHash;

public class HfFilter extends FuncFilter {
    private static final long serialVersionUID = 1L;

    public HfFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public HfFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, ToolHash::hfHash);
    }
}
