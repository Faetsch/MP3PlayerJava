package jpp.wuetunes.util;

import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate
{
    public static int requireNonNegative(int n)
    {
        if(n < 0)
        {
            throw new IllegalArgumentException("negative: " + String.valueOf(n));
        }
        return n;
    }

    public static int requireBetween(int n, int min, int max)
    {
        if(!(min <= n && max >= n))
        {
            throw new IllegalArgumentException("not between: " + String.valueOf(n) + ", " +  String.valueOf(min)  + ", " +   String.valueOf(max));
        }
        return n;
    }

    public static int requireLowerThan(int n, int max)
    {
        if(n >= max)
        {
            throw new IllegalArgumentException(String.valueOf(n) + " not less than " + String.valueOf(max));
        }
        return n;
    }

    public static <O> O requireNonNull(O o)
    {
        if(o == null)
        {
            throw new IllegalArgumentException("is null");
        }
        return o;
    }

    public static String requireNonNullNotEmpty(String s)
    {
        if(s == null)
        {
            throw new IllegalArgumentException("null String");
        }

        else
        {
            if(s.equals(""))
            {
                throw new IllegalArgumentException("empty String");
            }
            return s;
        }
    }

    public static String requireLength(String s, int length)
    {
        requireNonNullNotEmpty(s);
        if(s.length() != length)
        {
            throw new IllegalArgumentException("not required length");
        }
        return s;
    }

    public static Path requireFileExists(Path path)
    {
        requireNonNull(path);
        if(!path.toFile().exists())
        {
            throw new IllegalArgumentException("path is not a file");
        }
        return path;
    }

    public static byte[] requireNonNullNotEmpty(byte[] bytes)
    {
        requireNonNull(bytes);
        if(bytes.length == 0)
        {
            throw new IllegalArgumentException("empty byte array");
        }
        return bytes;
    }

    public static byte[] requireLength(byte[] bytes, int length)
    {
        requireNonNullNotEmpty(bytes);
        if(bytes.length != length)
        {
            throw new IllegalArgumentException("not required length");
        }
        return bytes;
    }

    public static <C extends Collection> C requireNonNullNotEmpty(C c)
    {
        requireNonNull(c);
        if(c.isEmpty())
        {
            throw new IllegalArgumentException("is empty");
        }
        return c;
    }

    public static String requireValidImageMimeType(String mimeType)
    {
        requireNonNullNotEmpty(mimeType);
        Pattern p = Pattern.compile("image/([a-z0-9.-]{1,127})");
        Matcher m = p.matcher(mimeType);
        //System.out.println(mimeType);
        if(!m.matches())
        {
            throw new IllegalArgumentException("no match");
        }
        return mimeType;
    }

}
