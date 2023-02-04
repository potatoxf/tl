package pxf.tl.text.capitulation;

/**
 * 包装序号处理器
 *
 * @author potatoxf
 */
public class WrapperSerialNumberProcessor implements SerialNumberProcessor {

    private final String prefix;

    private final String suffix;

    private final SerialNumberProcessor serialNumberProcessor;

    public WrapperSerialNumberProcessor(String prefix, String suffix) {

        this(prefix, suffix, null);
    }

    public WrapperSerialNumberProcessor(
            String prefix, String suffix, SerialNumberProcessor serialNumberProcessor) {

        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
        this.serialNumberProcessor = serialNumberProcessor;
    }

    @Override
    public String handle(int serialNumber) {
        if (serialNumber < 0) {
            throw new IllegalArgumentException("The serial number must be greater 0");
        }
        if (serialNumberProcessor == null) {
            return prefix + serialNumber + suffix;
        }
        String result = serialNumberProcessor.handle(serialNumber);
        return prefix + result + suffix;
    }
}
