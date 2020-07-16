package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

public class ID3FrameHeader
{
    private ID3FrameType type;
    private int size;
    private byte statusFlags;
    private byte formatFlags;

    ID3FrameHeader(ID3FrameType type, int size, byte statusFlags, byte formatFlags)
    {
        //Validate.requireNonNull(type);
        Validate.requireNonNegative(size);
        this.type = type;
        this.size = size;
        this.statusFlags = statusFlags;
        this.formatFlags = formatFlags;
    }

    public boolean isFlagDataLengthIndicator()
    {
        return ID3Utils.readFlag(formatFlags, 0);
    }

    public boolean isFlagUnsynchronization()
    {
        return ID3Utils.readFlag(formatFlags, 1);
    }

    public ID3FrameType getFrameType()
    {
        return type;
    }

    public int getSize()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return String.format("[ID3FrameHeader %s, size: %d, status flags: %s, format flags: %s, data length indicator: %b, unsynchronization: %b]", type.toString(), getSize(), ID3Utils.byteToBitString(statusFlags), ID3Utils.byteToBitString(formatFlags), isFlagDataLengthIndicator(), isFlagUnsynchronization());
    }
}
