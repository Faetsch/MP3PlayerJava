package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

public class ID3Utils
{

    static String byteToBitString(byte b)
    {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    static String intToBitString(int value)
    {
        return String.format("%32s", Integer.toBinaryString(value & 0xFFFFFFFF)).replace(' ', '0');
    }

    static boolean readFlag(byte value, int flagPosition)
    {
        Validate.requireBetween(flagPosition, 0, 7);
        return ((value >> flagPosition)  & 1) == 1;
    }

    static int read32BitSynchsafeInteger(byte[] data)
    {
        Validate.requireNonNullNotEmpty(data);
        Validate.requireLength(data, 4);
        int b0 = (data[0] & 0x7f) << 21;
        int b1 = (data[1] & 0x7f) << 14;
        int b2 = (data[2] & 0x7f) << 7;
        int b3 = data[3] & 0x7f;
        return b0 + b1 + b2 + b3;
    }

    static byte[] synchronize(byte[] bytes)
    {
        // synchronisation is replacing instances of:
        // 11111111 00000000 111xxxxx with 11111111 111xxxxx and
        // 11111111 00000000 00000000 with 11111111 00000000
        int count = sizeSynchronisationWouldSubtract(bytes);
        if (count == 0) return bytes;
        byte[] newBuffer = new byte[bytes.length - count];
        int i = 0;
        for (int j = 0; j < newBuffer.length - 1; j++) {
            newBuffer[j] = bytes[i];
            if (bytes[i] == (byte) 0xff && bytes[i + 1] == 0 && ((bytes[i + 2] & (byte) 0xe0) == (byte) 0xe0 || bytes[i + 2] == 0)) {
                i++;
            }
            i++;
        }
        newBuffer[newBuffer.length - 1] = bytes[i];
        return newBuffer;
    }

    private static int sizeSynchronisationWouldSubtract(byte[] bytes)
    {
        int count = 0;
        for (int i = 0; i < bytes.length - 2; i++) {
            if (bytes[i] == (byte) 0xff && bytes[i + 1] == 0 && ((bytes[i + 2] & (byte) 0xe0) == (byte) 0xe0 || bytes[i + 2] == 0)) {
                count++;
            }
        }
        if (bytes.length > 1 && bytes[bytes.length - 2] == (byte) 0xff && bytes[bytes.length - 1] == 0) count++;
        return count;
    }
}