package jpp.wuetunes.io.database;

public class DatabaseReadException extends IllegalStateException
{
    public DatabaseReadException(String s)
    {
        super(s);
    }

    public DatabaseReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
