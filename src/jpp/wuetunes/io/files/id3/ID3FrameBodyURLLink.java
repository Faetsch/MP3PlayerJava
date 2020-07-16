package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

import java.net.URL;

public class ID3FrameBodyURLLink implements ID3FrameBody
{

    private URL url;

   ID3FrameBodyURLLink(URL url)
    {
        Validate.requireNonNull(url);
        this.url = url;
    }

    public URL getUrl()
    {
        return url;
    }

    @Override
    public String dataToString()
    {
        return String.format("[ID3FrameBody URL \"%s\"]", getUrl().toString());
    }
}
