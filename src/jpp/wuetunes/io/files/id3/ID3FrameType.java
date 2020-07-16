package jpp.wuetunes.io.files.id3;


import java.util.Optional;

public enum ID3FrameType
{
    TPE1(ID3FrameContentType.TEXT_INFORMATION),
    TIT2(ID3FrameContentType.TEXT_INFORMATION),
    TALB(ID3FrameContentType.TEXT_INFORMATION),
    TCON(ID3FrameContentType.TEXT_INFORMATION),
    TDRC(ID3FrameContentType.TEXT_INFORMATION),
    TRCK(ID3FrameContentType.TEXT_INFORMATION),
    WCOP(ID3FrameContentType.URL_LINK),
    WPUB(ID3FrameContentType.URL_LINK),
    APIC(ID3FrameContentType.ATTACHED_PICTURE);

    private ID3FrameContentType type;

    ID3FrameType(ID3FrameContentType type)
    {
        this.type = type;
    }

    public ID3FrameContentType getContentType()
    {
        return this.type;
    }

    static Optional<ID3FrameType> getByIdentifier(String identifier)
    {
        switch (identifier)
        {
            case "TPE1":
                return Optional.of(TPE1);

            case "TIT2":
                return Optional.of(TIT2);

            case "TALB":
                return Optional.of(TALB);

            case "TCON":
                return Optional.of(TCON);

            case "TDRC":
                return Optional.of(TDRC);

            case "TRCK":
                return Optional.of(TRCK);

            case "WCOP":
                return Optional.of(WCOP);

            case "WPUB":
                return Optional.of(WPUB);

            case "APIC":
                return Optional.of(APIC);

            default:
                return Optional.empty();
        }
    }
}
