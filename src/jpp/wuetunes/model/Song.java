package jpp.wuetunes.model;

import jpp.wuetunes.model.metadata.Metadata;
import jpp.wuetunes.util.Validate;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

public class Song implements Comparable<Song>
{
    private Metadata metadata;
    private Path path;

    public Song(Path filePath, Metadata metadata)
    {
        Validate.requireNonNull(metadata);
        this.metadata = metadata;
        Path p = Validate.requireNonNull(filePath);
        if(!p.toString().endsWith(".mp3"))
        {
            throw new IllegalArgumentException(String.format("%s doesn't end with .mp3 .", p.toString()));
        }
        else
        {
            this.path = p;
        }
    }

    public Path getFilePath()
    {
        return path;
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    @Override
    public String toString()
    {
        String result = "Filename: " + path.getFileName() + "\n";
        result += metadata.toString();
        result.trim();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        return path.equals(song.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public int compareTo(Song o)
    {
        //artist, albumtitle, tracknumber, song title

        //artist

        if(!(this.metadata.getArtist().equals(o.getMetadata().getArtist())))
        {
            return this.metadata.getArtist().orElse("").compareTo(o.getMetadata().getArtist().orElse(""));
        }

        //album title

        if(!(this.metadata.getAlbumTitle().equals(o.getMetadata().getAlbumTitle())))
        {
            return this.metadata.getAlbumTitle().orElse("").compareTo(o.getMetadata().getAlbumTitle().orElse(""));
        }

        //track number

        if(!(this.metadata.getTrackNumber().equals(o.getMetadata().getTrackNumber())))
        {
            return this.metadata.getTrackNumber().orElse(0).compareTo(o.getMetadata().getTrackNumber().orElse(0));
        }

        //song title

        if(!(this.metadata.getSongTitle().equals(o.getMetadata().getSongTitle())))
        {
            return this.metadata.getSongTitle().orElse("").compareTo(o.getMetadata().getSongTitle().orElse(""));
        }

        return 0;
    }
}
