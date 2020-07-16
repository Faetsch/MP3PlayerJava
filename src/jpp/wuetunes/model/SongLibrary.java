package jpp.wuetunes.model;

import jpp.wuetunes.util.Validate;

import java.util.*;
import java.util.function.Predicate;

public class SongLibrary
{
    private Collection<Song> songs;

    public SongLibrary()
    {
        songs = new ArrayList<Song>();
    }

    public SongLibrary(Collection<Song> songs)
    {
        Validate.requireNonNull(songs);
        Collection<Song> temp = new ArrayList<Song>();
        for(Song song : songs)
        {
            temp.add(song);
        }
        this.songs = temp;
    }

    public void add(Song song)
    {
        Validate.requireNonNull(song);
        if(!songs.contains(song))
        {
            songs.add(song);
        }
    }

    public void addAll(Collection<Song> songs)
    {
        Validate.requireNonNullNotEmpty(songs);
        for(Song s : songs)
        {
            if(!this.songs.contains(s))
            {
                this.add(s);
            }
        }
    }

    public boolean remove(Song song)
    {
        Validate.requireNonNull(song);
        if(songs.contains(song))
        {
            songs.remove(song);
            return true;
        }
        return false;
    }

    public boolean removeAll(Collection<Song> songs)
    {
        Validate.requireNonNullNotEmpty(songs);
        boolean removed = false;
        for(Song s : songs)
        {
            if(this.songs.contains(s))
            {
                removed = true;
                this.songs.remove(s);
            }
        }
        return removed;
    }

    public Set<Song> getSongs()
    {
        Set<Song> songSet = new HashSet<Song>();
        songSet.addAll(songs);
        return songSet;
    }

    public Set<Song> getSongs(Predicate<Song> filter)
    {
        Validate.requireNonNull(filter);
        Set<Song> songSet = new HashSet<Song>();
        for(Song s : songs)
        {
            if(filter.test(s))
            {
                songSet.add(s);
            }
        }
        return songSet;
    }
}
