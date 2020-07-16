package jpp.wuetunes.io.files;

import jpp.wuetunes.util.Validate;

import java.nio.file.Path;

public class SongFileImportFailure
{
    private Path filePath;
    private String message;
    public SongFileImportFailure(Path filePath, String message)
    {
        Validate.requireNonNull(filePath);
        Validate.requireNonNullNotEmpty(message);
        this.filePath = filePath;
        this.message = message;
    }

    public Path getFilePath()
    {
        return filePath;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongFileImportFailure that = (SongFileImportFailure) o;

        if (filePath != null ? !filePath.equals(that.filePath) : that.filePath != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode()
    {
        int result = filePath != null ? filePath.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
