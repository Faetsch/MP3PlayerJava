package jpp.wuetunes.model.metadata;

import jpp.wuetunes.util.Validate;

import java.util.*;

public class GenreManager
{
    private Collection<Genre> genres;

    public GenreManager()
    {
        genres = new ArrayList<Genre>();
    }

    public GenreManager(Collection<Genre> genres)
    {
        Collection<Genre> g = Validate.requireNonNull(genres);
        for(Genre compared : g)
        {
            for(Genre comparing : g)
            {
                if(comparing != compared)
                {
                    if(comparing.compareTo(compared) == 0 || comparing.getName().equals(compared.getName()))
                    {
                        throw new IllegalArgumentException("redundant genre");
                    }
                }
            }
        }
        Collection<Genre> temp = new ArrayList<Genre>();
        for(Genre toAdd : genres)
        {
            temp.add(toAdd);
        }
        this.genres = temp;
    }

    public Genre add(Genre genre)
    {
        Genre g = Validate.requireNonNull(genre);
        for(Genre currGenre : genres)
        {
            if(currGenre.getName().equals(genre.getName()) || currGenre.getId() == genre.getId())
            {
                throw new IllegalArgumentException(String.format("genre manager already has element: (%s, %d), can't add (%s, %d)", currGenre.getName(), currGenre.getId(), genre.getName(), genre.getId()));
            }
        }

        genres.add(g);
        //System.out.println(genres);
        return g;
    }

    public Genre add(String name)
    {
        String n = Validate.requireNonNullNotEmpty(name);
        int freeID = Validate.requireNonNegative(nextFreeID());
        Optional<Genre> old = getGenreByName(name);
        if(old.isPresent())
        {
            return old.get();
        }
        Genre g = new Genre(freeID, name);
        add(g);
        return g;
    }

    public Optional<Genre> getGenreById(int id)
    {
        Validate.requireNonNegative(id);
        Optional<Genre> result = Optional.empty();
        for(Genre g : genres)
        {
            if(g.getId() == id)
            {
                result = Optional.of(g);
            }
        }
        return result;
    }

    public Optional<Genre> getGenreByName(String name)
    {
        Validate.requireNonNullNotEmpty(name);
        Optional<Genre> result = Optional.empty();
        for(Genre g : genres)
        {
            if(g.getName().equals(name))
            {
                result = Optional.of(g);
            }
        }
        return result;
    }

    public List<Genre> getGenres()
    {
        List<Genre> result = new ArrayList<Genre>();
        for(Genre g : genres)
        {
            result.add(g);
        }

        //Sort
        result.sort(new Comparator<Genre>()
        {
            @Override
            public int compare(Genre o1, Genre o2)
            {
                return o1.compareTo(o2);
            }
        });
        return result;

    }

    private int nextFreeID()
    {
        List<Genre> list = getGenres();
        if(list.size() == 0)
        {
            return 0;
        }

        boolean found = false;
        int checkingId = 0;
        while(!found)
        {
            for(int i = 0; i < list.size(); i++)
            {
                if(list.get(i).getId() == checkingId)
                {
                    break;
                }

                if(i == list.size()-1)
                {
                    found = true;
                    //System.out.println("new free id is " + checkingId);
                    return checkingId;
                }
            }
            checkingId++;
        }
        //System.out.println("new free id is (why)" + checkingId);
        return checkingId;
    }
}
