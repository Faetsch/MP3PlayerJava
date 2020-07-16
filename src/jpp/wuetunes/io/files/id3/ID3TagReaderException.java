package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.util.Validate;

import java.io.IOException;

public class ID3TagReaderException extends IOException
{
    private String message;
    private Throwable cause;
    ID3TagReaderException(String message)
    {
        Validate.requireNonNullNotEmpty(message);
        this.message = message;
    }

    ID3TagReaderException(String message, Throwable cause)
    {
        Validate.requireNonNullNotEmpty(message);
        Validate.requireNonNull(cause);
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public Throwable getCause()
    {
        return cause;
    }
}
