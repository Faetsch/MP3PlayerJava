package jpp.wuetunes.io.database;

public class InconsistentDatabaseException extends IllegalStateException
{
    public InconsistentDatabaseException(String s)
    {
        super(s);
    }

    public InconsistentDatabaseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
