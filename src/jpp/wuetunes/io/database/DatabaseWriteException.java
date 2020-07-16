package jpp.wuetunes.io.database;

public class DatabaseWriteException extends IllegalStateException
{
    public DatabaseWriteException(String s)
    {
        super(s);
    }

    public DatabaseWriteException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
