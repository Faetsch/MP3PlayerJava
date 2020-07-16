package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ID3TagReader
{
    private static ID3Encoding readEncoding(byte encoding, byte bom0, byte bom1) throws ID3TagReaderException
    {
        //System.out.printf("enc: %d, bom0: %d, bom1: %d", encoding ,bom0, bom1);
        switch(encoding)
        {
            case 0x00:
                return new ID3Encoding(StandardCharsets.ISO_8859_1, false, 1);

            case 0x01:
                Charset charset;
                if(bom0 == -2 && bom1 == -1)
                {
                    charset = StandardCharsets.UTF_16BE;
                }
                else if(bom0 == -1 && bom1 == -2)
                {
                    charset = StandardCharsets.UTF_16LE;
                }

                else
                {
                    throw new ID3TagReaderException(String.format("Keine passende Kodierung: enc: %d, bom0: %d, bom1: %d", encoding ,bom0, bom1));
                }
                return new ID3Encoding(charset, true, 2);

            case 0x02:
                return new ID3Encoding(StandardCharsets.UTF_16BE, false, 2);

            case 0x03:
                return new ID3Encoding(StandardCharsets.UTF_8, false, 1);

            default:
                throw new ID3TagReaderException("Keine passende Kodierung");
        }
    }

    private static ID3TagHeader readTagHeader(byte[] data) throws ID3TagReaderException
    {
        Validate.requireNonNullNotEmpty(data);
        Validate.requireLength(data, 10);
        if(data[0] != 0x49 || data[1] != 0x44 || data[2] != 0x33)
        {
            throw new ID3TagReaderException("Ungültiger Anfang des ID3Tag-Headers");
        }

        byte[] tsData = {data[6], data[7], data[8], data[9]};

        int majorVersion = data[3];
        int revision = data[4];
        boolean flagUnsynchronisation = ID3Utils.readFlag(data[5], 7);
        boolean flagExtendedHeader = ID3Utils.readFlag(data[5], 6);
        boolean flagExperimentalIndicator = ID3Utils.readFlag(data[5], 5);
        boolean flagFooterPresent = ID3Utils.readFlag(data[5], 4);
        int tagSize = ID3Utils.read32BitSynchsafeInteger(tsData);
        return new ID3TagHeader(majorVersion, revision, flagUnsynchronisation, flagExtendedHeader, flagExperimentalIndicator, flagFooterPresent, tagSize);

    }

    public static ID3Tag read(Path path) throws ID3TagReaderException
    {
        ID3Tag result;
        byte[] fileData;
        List<ID3Frame> frames = new ArrayList<ID3Frame>();
        try
        {
           fileData = Files.readAllBytes(path);
        }
        catch (IOException e)
        {
            throw new ID3TagReaderException(String.format("%s zeigt auf keine Datei", path.toString()));
        }

        //check for version
        ID3TagHeader tagHeader = readTagHeader(Arrays.copyOfRange(fileData, 0, 10));
        if(!(tagHeader.getMajorVersion() == 4 && tagHeader.getRevision() == 0))
        {
            result = new ID3Tag(tagHeader, frames);
            return result;
        }

        //read all frames until 4 bytes in a row are nullbytes or tagsize reached
        int tagSize = tagHeader.getTagSize();
        byte[] framesData = Arrays.copyOfRange(fileData, 10, tagSize);
        int offset = 0;
        while(offset+10 < tagSize)
        {
            if(offset+3 < tagSize)
            {
                if((framesData[offset] == 0x00 && framesData[offset+1] == 0x00 && framesData[offset+2] == 0x00 && framesData[offset+3] == 0x00))
                {
                    result = new ID3Tag(tagHeader, frames);
                    return result;
                }
            }
            //System.out.println("offset : " + offset);
            //System.out.println("tagsize : " + tagSize);
            offset = readFrame(framesData, offset, frames);
        }

        result = new ID3Tag(tagHeader, frames);
        return result;
    }


    private static int readFrame(byte[] framesData, int offset, List<ID3Frame> frames) throws ID3TagReaderException
    {
        Validate.requireNonNull(framesData);
        byte[] headerData;
        if(framesData.length > offset + 9)
        {
            headerData = Arrays.copyOfRange(framesData, offset, offset + 10);
        }
        else
        {
            if(offset >= framesData.length)
            {
                return framesData.length;
            }
            System.out.println(framesData.length);
            System.out.println(offset);
            headerData = Arrays.copyOfRange(framesData, offset, framesData.length);
        }
        ID3FrameHeader header = readFrameHeader(headerData);

        if(header.getFrameType() == null)
        {
            return offset + 10 + header.getSize();
        }

        if(!ID3FrameType.getByIdentifier(header.getFrameType().toString()).isPresent())
        {
            return offset + 10 + header.getSize();
        }

        byte[] bodyData = Arrays.copyOfRange(framesData, offset + 10, header.getSize() + offset + 10);
        Optional<ID3FrameBody> bodyOpt = readFrameBody(header, bodyData);

        ID3FrameBody body;

        if(bodyOpt.isPresent())
        {
            body = bodyOpt.get();
        }
        else
        {
            throw new ID3TagReaderException("why is this empty");
        }
        ID3Frame frame = new ID3Frame(header, body);
        frames.add(frame);
        return offset + 10 + header.getSize();

    }

    private static ID3FrameHeader readFrameHeader(byte[] header) throws ID3TagReaderException
    {
        Validate.requireNonNullNotEmpty(header);
        Validate.requireLength(header, 10);
        byte[] typeData = {header[0], header[1], header[2], header[3]};
        byte[] sizeData = {header[4], header[5], header[6], header[7]};
        int size = ID3Utils.read32BitSynchsafeInteger(sizeData);
        String typeIdentifier = new String(typeData, StandardCharsets.UTF_8);
        Optional<ID3FrameType> type = ID3FrameType.getByIdentifier(typeIdentifier);

        if(type.isPresent())
        {
            return new ID3FrameHeader(type.get(), size, header[8], header[9]);
        }
        else
        {
            return new ID3FrameHeader(null, size, header[8], header[9]);
        }

    }

    //TODO doesnt work when synchronizing flag is set
    private static Optional<ID3FrameBody> readFrameBody(ID3FrameHeader header, byte[] bodyData) throws ID3TagReaderException
    {
        ID3FrameType frameType = header.getFrameType();
        if(frameType == null)
        {
            return Optional.empty();
        }
        //if(!ID3FrameType.getByIdentifier(frameType.name()).isPresent())
        //{
        //    return Optional.empty();
        //}

        switch(frameType.getContentType())
        {
            case TEXT_INFORMATION:
                byte encodingData = bodyData[0];
                byte[] textData;
                try
                {
                    ID3Encoding encoding =  readEncoding(encodingData, bodyData[1], bodyData[2]);
                    int nullBytes = encoding.getNumNullBytes();
                    if(encoding.isWithBOM())
                    {
                        textData = Arrays.copyOfRange(bodyData, 3, bodyData.length - nullBytes);
                    }
                    else
                    {
                        textData = Arrays.copyOfRange(bodyData, 1, bodyData.length - nullBytes);
                    }
                    String text = new String(textData, encoding.getCharset());
                    return Optional.of(new ID3FrameBodyTextInformation(text));
                }
                catch (ID3TagReaderException e)
                {
                    return Optional.empty();
                }

            case URL_LINK:
                URL url;
                byte[] urlData = Arrays.copyOfRange(bodyData, 0, bodyData.length);
                try
                {
                   url = new URL(new String(urlData, StandardCharsets.ISO_8859_1));
                }

                catch (MalformedURLException e)
                {
                    throw new ID3TagReaderException("malformed url");
                }
                return Optional.of(new ID3FrameBodyURLLink(url));


            case ATTACHED_PICTURE:
                byte[] picBodyData;
                boolean dataLengthFlag = header.isFlagDataLengthIndicator();
                boolean synchFlag = header.isFlagUnsynchronization();


                //offset for datalength indicator
                if(dataLengthFlag)
                {
                    picBodyData = Arrays.copyOfRange(bodyData, 4, bodyData.length);
                }
                else
                {
                    picBodyData = Arrays.copyOfRange(bodyData, 0, bodyData.length);
                }

                byte encodingByte = picBodyData[0];
                ArrayList<Byte> mimeTypeData = new ArrayList<Byte>();
                int picTypeData;
                boolean isWithBom = false;
                ArrayList<Byte> descriptionData = new ArrayList<>();
                int descriptionNullBytes = 0;
                byte[] picData;

                int currIndex = 1;
                byte currByte = picBodyData[currIndex];

                //iterate through mimeTypeData
                while(currByte != 0x00)
                {
                    mimeTypeData.add(currByte);
                    currIndex++;
                    currByte = picBodyData[currIndex];
                }

                //get pic type data
                currIndex++;
                picTypeData = picBodyData[currIndex];
                currIndex++;

                //get description
                //currIndex++;
                ID3Encoding picEncoding = readEncoding(encodingByte, picBodyData[currIndex], picBodyData[currIndex+1]);
                //System.out.println(picEncoding.getCharset());
                isWithBom = picEncoding.isWithBOM();

                if(isWithBom)
                {
                    //
                    //System.out.println("is with Bom");
                    currIndex += 2;
                }
                descriptionNullBytes = picEncoding.getNumNullBytes();
                //System.out.println(descriptionNullBytes);

                //case 1 nullbyte
                currByte = picBodyData[currIndex];
                descriptionNullBytes = picEncoding.getNumNullBytes();
                //System.out.println(currByte); correct position so far

                if(descriptionNullBytes == 1)
                {
                    while(currByte != 0x00)
                    {
                       // System.out.println("running");
                        descriptionData.add(currByte);
                        currIndex++;
                        currByte = picBodyData[currIndex];
                    }
                    //currIndex++;
                }

                //case 2 null bytes
                if(descriptionNullBytes == 2)
                {
                   // System.out.println("hier2");
                    while(!(currByte == 0x00 && picBodyData[currIndex+1] == 0x00))
                    {
                     //   System.out.println("running");
                        descriptionData.add(currByte);
                        descriptionData.add(picBodyData[currIndex+1]);
                        currIndex += 2;
                        currByte = picBodyData[currIndex];
                    }
                    currIndex++;
                }
              //  System.out.println("done reading description");

                currIndex++;

                //get pic data

                picData = Arrays.copyOfRange(picBodyData, currIndex, header.getSize());
                if(synchFlag)
                {
                    picData = Arrays.copyOfRange(picBodyData, currIndex, header.getSize()-4);
                    System.out.println("synching");
                    picData = ID3Utils.synchronize(picData);
                }

                //assemble data

                byte[] tempMimeData = new byte[mimeTypeData.size()];
                for(int i = 0; i < mimeTypeData.size(); i++)
                {
                    tempMimeData[i] = mimeTypeData.get(i);
                }

                byte[] tempDescriptionData = new byte[descriptionData.size()];
                for(int i = 0; i < descriptionData.size(); i++)
                {
                    tempDescriptionData[i] = descriptionData.get(i);
                }

                String mimeTypeString = new String(tempMimeData, StandardCharsets.ISO_8859_1);
                String descriptionString = new String(tempDescriptionData, picEncoding.getCharset());
                return Optional.of(new ID3FrameBodyAttachedPicture(mimeTypeString, picTypeData, descriptionString, picData));

             default:
                 return Optional.empty();
        }

    }
}
