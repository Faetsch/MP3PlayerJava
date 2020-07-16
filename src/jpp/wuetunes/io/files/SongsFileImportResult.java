package jpp.wuetunes.io.files;

import jpp.wuetunes.model.Song;
import jpp.wuetunes.util.Validate;

import java.util.Set;

public class SongsFileImportResult
{
    private Set<SongFileImportFailure> failures;
    private Set<Song> songs;


    public SongsFileImportResult(Set<Song> songs, Set<SongFileImportFailure> failures)
    {
        Validate.requireNonNull(songs);
        Validate.requireNonNull(failures);
        this.failures = failures;
        this.songs = songs;
    }

    public Set<Song> getSongs()
    {
        return songs;
    }

    public Set<SongFileImportFailure> getFailures()
    {
        return failures;
    }
}
