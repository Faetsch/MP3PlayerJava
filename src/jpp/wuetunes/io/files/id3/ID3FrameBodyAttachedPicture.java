package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

import java.util.Arrays;

public class ID3FrameBodyAttachedPicture implements ID3FrameBody
{
    private String mimeType;
    private int imageType;
    private String description;
    private byte[] data;

    ID3FrameBodyAttachedPicture(String mimeType, int imageType, String description, byte[] data)
    {
        Validate.requireValidImageMimeType(mimeType);
        Validate.requireBetween(imageType, 0, 20);
        Validate.requireNonNullNotEmpty(data);
        Validate.requireNonNull(description);
        this.mimeType = mimeType;
        this.imageType = imageType;
        this.description = description;
        this.data = Arrays.copyOf(data, data.length);
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public int getImageType()
    {
        return imageType;
    }

    public String getDescription()
    {
        return description;
    }

    public byte[] getPictureData()
    {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public String dataToString()
    {
        return String.format("[ID3FrameBody Picture MIME type: \"%s\", image type: %d, \"%s\", %d bytes, picture hash: %d]", getMimeType(), getImageType(), getDescription(), getPictureData().length, Arrays.hashCode(getPictureData()));
    }
}
