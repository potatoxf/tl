package pxf.tl.text.capitulation;

/**
 * 序号处理器
 *
 * @author potatoxf
 */
public interface SerialNumberProcessor {

    /**
     * 处理序号
     *
     * @param serialNumber 序号
     * @return 返回不同样式的序号
     */
    String handle(int serialNumber);
}
