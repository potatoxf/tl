package pxf.tl.bloomfilter.filter;


import pxf.tl.util.ToolHash;

public class TianlFilter extends FuncFilter {
    private static final long serialVersionUID = 1L;

    public TianlFilter(long maxValue) {
        this(maxValue, DEFAULT_MACHINE_NUM);
    }

    public TianlFilter(long maxValue, int machineNum) {
        super(maxValue, machineNum, ToolHash::tianlHash);
    }
}
