package pxf.tl.bloomfilter.filter;


import pxf.tl.util.ToolHash;

public class HfIpFilter extends FuncFilter {
    private static final long serialVersionUID = 1L;

    public HfIpFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public HfIpFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, ToolHash::hfIpHash);
    }
}
