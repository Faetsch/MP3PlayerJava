package jpp.wuetunes.model;

import jpp.wuetunes.util.Validate;

import java.util.*;

public class Playlist
{
    private List<Song> songs = new ArrayList<Song>();
    private boolean shuffle;
    private Song currentSong;
    private int currentSongIndex;
    private Random random;

    public Playlist()
    {
        shuffle = false;
    }

    public void add(Song song)
    {
        Validate.requireNonNull(song);
        songs.add(song);
    }

    public List<Song> getSongs()
    {
        return songs;
    }

    public void addAt(int index, Song song)
    {
        Validate.requireBetween(index, 0, songs.size());
        Validate.requireNonNull(song);
        songs.add(index, song);
        if(currentSongIndex >= index)
        {
            currentSongIndex++;
        }
    }

    public void removeAt(int index)
    {
        Validate.requireBetween(index, 0, songs.size()-1);
        if(currentSongIndex == index)
        {
            setCurrent(-1);
        }

        else if(currentSongIndex >= index)
        {
            currentSongIndex--;
        }
        songs.remove(index);
    }

    public void setCurrent(int index)
    {
        Validate.requireBetween(index, -1, songs.size()-1);
        if(index == -1)
        {
            currentSong = null;
        }

        else
        {
            currentSongIndex = index;
            currentSong = songs.get(index);
        }
    }

    public Optional<Integer> getCurrent()
    {
        if(songs.isEmpty() || currentSong == null)
        {
            return Optional.empty();
        }

        else
        {
            return Optional.of(currentSongIndex);
        }
    }

    public Optional<Song> getCurrentSong()
    {
        if(songs.isEmpty() || currentSong == null)
        {
            return Optional.empty();
        }

        else
        {
            return Optional.of(currentSong);
        }
    }

    public void setRandom(Random random)
    {
        Validate.requireNonNull(random);
        this.random = random;
    }

    public Optional<Integer> getRandom()
    {
        if(songs.isEmpty())
        {
            return Optional.empty();
        }

        else
        {
            if(random == null)
            {
                setRandom(new Random());
            }
            return Optional.of(random.nextInt(songs.size()));
        }
    }

    public void setShuffle(boolean value)
    {
        this.shuffle = value;
    }

    public Optional<Integer> next()
    {
        if(songs.isEmpty())
        {
            return Optional.empty();
        }

        if(!shuffle)
        {
            if(!getCurrentSong().isPresent() || getCurrent().get() == songs.size()-1)
            {
                setCurrent(0);
                return Optional.of(0);
            }
            else
            {
                setCurrent(getCurrent().get() + 1);
                return getCurrent();
            }
        }

        else
        {
            int pos = getRandom().get();
            setCurrent(pos);
            return Optional.of(pos);
        }
    }

    public Optional<Integer> previous()
    {
        if(songs.isEmpty())
        {
            return Optional.empty();
        }

        if(!shuffle)
        {
            if(!getCurrentSong().isPresent() || getCurrent().get() == 0)
            {
                setCurrent(songs.size()-1);
                return Optional.of(songs.size()-1);
            }
            else
            {
                setCurrent(getCurrent().get() - 1);
                return Optional.of(getCurrent().get());
            }
        }

        else
        {
            int pos = getRandom().get();
            setCurrent(pos);
            return Optional.of(pos);
        }
    }
}
