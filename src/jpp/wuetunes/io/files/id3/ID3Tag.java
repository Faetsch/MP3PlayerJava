package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

import javax.swing.text.html.Option;
import java.util.*;

public class ID3Tag
{
    private ID3TagHeader header;
    private List<ID3Frame> frames;

    ID3Tag(ID3TagHeader header, List<ID3Frame> frames)
    {
        Validate.requireNonNull(header);
        Validate.requireNonNull(frames);
        this.header = header;
        List<ID3Frame> temp = new ArrayList<ID3Frame>();
        for(ID3Frame f : frames)
        {
            temp.add(f);
        }
        this.frames = temp;
    }

    public ID3TagHeader getHeader()
    {
        return header;
    }

    public List<ID3Frame> getFrames()
    {
        List<ID3Frame> temp = new ArrayList<ID3Frame>();
        for(ID3Frame f : frames)
        {
            temp.add(f);
        }
        return temp;
    }

    public List<ID3Frame> getOrderedFrames(ID3FrameType type)
    {
        Validate.requireNonNull(type);
        List<ID3Frame> result = new ArrayList<ID3Frame>();
        for(ID3Frame frame : frames)
        {
            if(frame.getHeader().getFrameType() == type)
            {
                result.add(frame);
            }
        }
        return result;
    }

    public Optional<ID3Frame> getFrameByIdentifier(ID3FrameType type)
    {
        for(ID3Frame frame : frames)
        {
            if(frame.getHeader().getFrameType() == type)
            {
                return Optional.of(frame);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        String result = header.toString() + "\n";

        for(ID3Frame frame : frames)
        {
            result += frame.toString() + "\n";
        }

        return result.trim();
    }
}
