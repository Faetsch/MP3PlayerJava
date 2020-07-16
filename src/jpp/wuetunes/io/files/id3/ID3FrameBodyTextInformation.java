package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

public class ID3FrameBodyTextInformation implements ID3FrameBody
{
    private String text;

    ID3FrameBodyTextInformation(String text)
    {
        Validate.requireNonNull(text);
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String dataToString()
    {
        return String.format("[ID3FrameBody Text \"%s\"]", getText());
    }
}
