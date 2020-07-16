package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

public class ID3Frame<B extends ID3FrameBody>
{
    private ID3FrameHeader header;
    private B body;

    ID3Frame(ID3FrameHeader header, B body)
    {
        Validate.requireNonNull(header);
        Validate.requireNonNull(body);
        this.header = header;
        this.body = body;
    }

    public ID3FrameHeader getHeader()
    {
        return header;
    }

    public B getBody()
    {
        return body;
    }

    @Override
    public String toString()
    {
        return header.toString() + body.dataToString();
    }
}
