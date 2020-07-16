package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

public class ID3TagHeader
{
    private int majorVersion, revision, tagSize;
    private boolean flagUnsynchronisation, flagExtendedHeader, flagExperimentalIndicator, flagFooterPresent;

    ID3TagHeader(int majorVersion, int revision, boolean flagUnsynchronisation, boolean flagExtendedHeader, boolean flagExperimentalIndicator, boolean flagFooterPresent, int tagSize)
    {
        Validate.requireNonNegative(majorVersion);
        Validate.requireNonNegative(revision);
        Validate.requireNonNegative(tagSize);
        this.majorVersion = majorVersion;
        this.revision = revision;
        this.tagSize = tagSize;
        this.flagUnsynchronisation = flagUnsynchronisation;
        this.flagExtendedHeader = flagExtendedHeader;
        this.flagExperimentalIndicator = flagExperimentalIndicator;
        this.flagFooterPresent = flagFooterPresent;
    }

    public int getMajorVersion()
    {
        return majorVersion;
    }

    public int getRevision()
    {
        return revision;
    }

    public int getTagSize()
    {
        return tagSize;
    }

    public boolean isFlagUnsynchronisation()
    {
        return flagUnsynchronisation;
    }

    public boolean isFlagExperimentalIndicator()
    {
        return flagExperimentalIndicator;
    }

    public boolean isFlagExtendedHeader()
    {
        return flagExtendedHeader;
    }

    public boolean isFlagFooterPresent()
    {
        return flagFooterPresent;
    }

    public String toString()
    {
        return String.format("[ID3TagHeader version: ID3v2.%d.%d, unsynchronisation: %b, extended header: %b, experimental indicator: %b, footer present: %b, tag size: %d bytes]", getMajorVersion(), getRevision(), isFlagUnsynchronisation(), isFlagExtendedHeader(), isFlagExperimentalIndicator(), isFlagFooterPresent(), getTagSize());
    }
}
