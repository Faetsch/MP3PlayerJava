package jpp.wuetunes.io.database;

public class DatabaseOpenException  extends IllegalStateException
{
    public DatabaseOpenException(String s)
    {
        super(s);
    }

    public DatabaseOpenException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
