package jpp.wuetunes.io.files;

import jpp.wuetunes.io.files.SongsFileImportResult;
import jpp.wuetunes.io.files.id3.*;
import jpp.wuetunes.model.Song;
import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.model.metadata.Metadata;
import jpp.wuetunes.util.Validate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SongsFileImporter
{
    private ID3TagToMetadataConverter converter;
    private ArrayList<String> paths = new ArrayList<String>();

    public SongsFileImporter(Collection<Genre> genres)
    {
        this.converter = new ID3TagToMetadataConverter(genres);
    }

    public SongsFileImportResult importSongsFromFolder(Path folderpath) throws IOException
    {
        Validate.requireNonNull(folderpath);
        Validate.requireFileExists(folderpath);
        Set<Song> songs = new HashSet<Song>();
        Set<SongFileImportFailure> failures = new HashSet<SongFileImportFailure>();
        walkFiles(folderpath.toString());
        for(String s : paths)
        {
            Path p = Paths.get(s);

            try
            {
                ID3Tag tag = ID3TagReader.read(p);;
                Metadata metadata = converter.convert(tag);
                Song song = new Song(p, metadata);
                songs.add(song);
            }
            catch (ID3TagReaderException e)
            {
                e.printStackTrace();
                failures.add(new SongFileImportFailure(p, e.getMessage()));
            }

        }
        paths = new ArrayList<String>();
        return new SongsFileImportResult(songs, failures);


    }

    private void walkFiles(String path)
    {
        File root = new File(path);
        File[] list = root.listFiles();

        if(list == null)
        {
            return;
        }

        for(File f : list)
        {
            if(f.isDirectory())
            {
                walkFiles(f.getAbsolutePath());
            }
            else
            {
                if(f.getAbsolutePath().endsWith(".mp3"))
                {
                    paths.add(f.getAbsolutePath());
                    //System.out.println(f.getAbsolutePath());
                }
            }
        }
    }
}
