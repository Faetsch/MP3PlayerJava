package jpp.wuetunes.model;

import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.util.Validate;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public class SongFilterFactory
{
    public static Predicate<Song> getArtistFilter(String artist)
    {
        Validate.requireNonNullNotEmpty(artist);
        return song -> song.getMetadata().getArtist().orElse("").equals(artist);
    }

    public static Predicate<Song> getAlbumTitleFilter(String albumTitle)
    {
        Validate.requireNonNullNotEmpty(albumTitle);
        return song -> song.getMetadata().getAlbumTitle().orElse("").equals(albumTitle);
    }

    public static Predicate<Song> getGenreFilter(Genre genre)
    {
        Validate.requireNonNull(genre);
        Predicate<Song> p = song ->
        {
            if(song.getMetadata().getGenre().isPresent()) return song.getMetadata().getGenre().get().equals(genre);
            else return Optional.empty().equals(genre);
        };
        return p;
    }

    public static Predicate<Song> getYearFilter(int year)
    {
        Predicate<Song> p = song ->
        {
            if(song.getMetadata().getDate().isPresent()) return Integer.parseInt(song.getMetadata().getDate().get().toString().substring(0, 4)) == year;
            else return false;
        };
        return p;
    }

    public static Predicate<Song> getMinRatingFilter(int rating)
    {
        Validate.requireBetween(rating, 0, 5);
        return song -> song.getMetadata().getRating().orElse(0) >= rating;
    }

    public static Predicate<Song> combineAnd(Collection<Predicate<Song>> filters)
    {
        Validate.requireNonNullNotEmpty(filters);
        Iterator<Predicate<Song>> iterator = filters.iterator();
        Predicate<Song> lastFilter = iterator.next();
        while(iterator.hasNext())
        {
            lastFilter = lastFilter.and(iterator.next());
        }

        return lastFilter;
    }

    public static Predicate<Song> combineOr(Collection<Predicate<Song>> filters)
    {
        Validate.requireNonNullNotEmpty(filters);
        Iterator<Predicate<Song>> iterator = filters.iterator();
        Predicate<Song> lastFilter = iterator.next();
        while(iterator.hasNext())
        {
            lastFilter = lastFilter.or(iterator.next());
        }
        return lastFilter;
    }
}
