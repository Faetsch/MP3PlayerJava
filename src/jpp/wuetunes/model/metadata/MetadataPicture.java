package jpp.wuetunes.model.metadata;

import jpp.wuetunes.util.Validate;

import java.util.Arrays;

public class MetadataPicture
{
    private String mimeType;
    private String description;
    private byte[] data;

    public MetadataPicture(String mimeType, String description, byte[] data)
    {
        this.mimeType = Validate.requireValidImageMimeType(mimeType);
        this.description = Validate.requireNonNull(description);
        this.data = Arrays.copyOf(Validate.requireNonNullNotEmpty(data), data.length);
    }

    @Override
    public String toString()
    {
        return String.format("MIME type: \"%s\", \"%s\", %d bytes", getMimeType(), getDescription(), getData().length);
    }

    //@Override
    //public boolean equalz(Object obj)
   // {
   //     if(obj instanceof MetadataPicture)
   //     {
   //         MetadataPicture other = (MetadataPicture) obj;
   //         if(getMimeType().equals(other.getMimeType()))
   //         {
    //            byte[] otherData = other.getData();
    //            if(otherData.length != data.length)
    //            {
   //                 return false;
   //             }
   //             for(int i = 0; i < getData().length; i++)
    //            {
   //                 if(getData()[i] != otherData[i])
   //                 {
   //                     return false;
    //                }
    //            }
    //            return true;
   //         }

    //        else
   //         {
   //             return false;
   //         }
   //     }
//
   //     else
    //    {
    //        return false;
    //    }
   // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataPicture that = (MetadataPicture) o;

        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return Arrays.equals(data, that.data);
    }


//@Override
    //public int hashCode()
   // {
    //    return mimeType.hashCode() + Arrays.hashCode(data) + description.hashCode();
   // }


    @Override
    public int hashCode()
    {
        int result = mimeType != null ? mimeType.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    public String getMimeType() {
        return mimeType;
    }


    public String getDescription() {
        return description;
    }


    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }
}
