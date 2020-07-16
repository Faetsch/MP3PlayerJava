package jpp.wuetunes.model.metadata;

import jpp.wuetunes.util.Validate;

public class Genre implements Comparable<Genre>
{
    private int id;
    private String name;
    public Genre(int id, String name)
    {
        this.id = Validate.requireNonNegative(id);
        this.name = Validate.requireNonNullNotEmpty(name);

    }

    @Override
    public String toString()
    {
        return id + ". " + name;
    }

    @Override
    public int compareTo(Genre g)
    {
        if(this.id < g.getId())
        {
            return -1;
        }

        else if(this.id == g.getId())
        {
            return 0;
        }

        else
        {
            return 1;
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Genre)
        {
            if(compareTo((Genre)o) == 0)
            {
                return true;
            }
        }
        else
        {
            return false;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return getId();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
