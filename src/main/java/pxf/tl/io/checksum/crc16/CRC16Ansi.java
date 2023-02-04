package pxf.tl.io.checksum.crc16;

import java.io.Serial;

/**
 * CRC16_ANSI
 *
 * @author potatoxf
 */
public class CRC16Ansi extends CRC16Checksum {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int WC_POLY = 0xa001;

    @Override
    public void reset() {
        this.wCRCin = 0xffff;
    }

    @Override
    public void update(int b) {
        int hi = wCRCin >> 8;
        hi ^= b;
        wCRCin = hi;

        for (int i = 0; i < 8; i++) {
            int flag = wCRCin & 0x0001;
            wCRCin = wCRCin >> 1;
            if (flag == 1) {
                wCRCin ^= WC_POLY;
            }
        }
    }
}
