package jpp.wuetunes.io.database;

import jpp.wuetunes.model.Song;
import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.model.metadata.GenreManager;
import jpp.wuetunes.model.metadata.Metadata;
import jpp.wuetunes.model.metadata.MetadataPicture;
import jpp.wuetunes.util.Validate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

public class DatabaseConnection implements AutoCloseable
{
    private Path path;
    private Connection connection;
    public DatabaseConnection(Path path)
    {
        Validate.requireNonNull(path);
        Validate.requireFileExists(path);
        this.path = path;
    }

    public DatabaseConnection()
    {
        Path p = Paths.get("src/main/resources/database/MediaLibrary.db");
        Validate.requireNonNull(p);
        Validate.requireFileExists(p);
        this.path = p;
    }

    public String getURL()
    {
        return "jdbc:sqlite:" + path.toString();
    }

    public String toString()
    {
        return "DatabaseConnection to " + getURL();
    }

    public void open()
    {
        try
        {
            if(connection != null)
            {
                if(!connection.isClosed())
                {
                    throw new IllegalStateException("Database is already open");
                }
            }

            String url = getURL();
            connection = DriverManager.getConnection(url);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseOpenException(e.getMessage());
        }
    }

    public void writeSong(Song song)
    {
        Validate.requireNonNull(song);
        Metadata songMeta = song.getMetadata();
        String selectSql = "SELECT filepath FROM songs WHERE filepath = ?";
        String updateSql = "UPDATE songs SET songtitle = ? , artist = ? , genreId = ? , albumTitle = ? , rating = ? , playCounter = ? , date = ? , trackNumber = ? , copyrightInfo = ? , webpage = ? , pictureMime = ? , pictureDesc = ? , picture = ? WHERE filepath = ?";
        String insertSql = "INSERT INTO songs(filepath, songtitle, artist, genreId, albumTitle, rating, playCounter, date, trackNumber, copyrightInfo, webpage, pictureMime, pictureDesc, picture) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSqlGenres = "SELECT id FROM genres WHERE id = ?";
        String insertSqlGenre = "INSERT INTO genres(id, genreName) VALUES(?, ?)";

        if(!isOpen())
        {
            throw new IllegalStateException("Database is already closed");
        }

        try
        {
            //if new genre, insert new genre into genres
            PreparedStatement genreStatement = connection.prepareStatement(selectSqlGenres);
            if(songMeta.getGenre().isPresent())
            {
                genreStatement.setInt(1, songMeta.getGenre().get().getId());
                ResultSet genreResults = genreStatement.executeQuery();

                if(!genreResults.next())
                {
                    genreStatement = connection.prepareStatement(insertSqlGenre);
                    genreStatement.setInt(1, songMeta.getGenre().get().getId());
                    genreStatement.setString(2, songMeta.getGenre().get().getName());
                    genreStatement.execute();
                }
            }


            //insert or update song
            PreparedStatement statement = connection.prepareStatement(selectSql);
            statement.setString(1, song.getFilePath().toString());
            ResultSet selectResult = statement.executeQuery();


            //if song is already in db, update
            if(selectResult.next())
            {
                statement = connection.prepareStatement(updateSql);
                int indexCounter = 1;

                if(songMeta.getSongTitle().isPresent())
                {
                    statement.setString(1, songMeta.getSongTitle().get());
                }
                else
                {
                    statement.setNull(1, Types.VARCHAR);
                }

                if(songMeta.getArtist().isPresent())
                {
                    statement.setString(2, songMeta.getArtist().get());
                }
                else
                {
                    statement.setNull(2, Types.VARCHAR);
                }

                if(songMeta.getGenre().isPresent())
                {
                    statement.setInt(3, songMeta.getGenre().get().getId());
                }
                else
                {
                    statement.setNull(3, Types.INTEGER);
                }

                if(songMeta.getAlbumTitle().isPresent())
                {
                    statement.setString(4, songMeta.getAlbumTitle().get());
                }
                else
                {
                    statement.setNull(4, Types.VARCHAR);
                }

                if(songMeta.getRating().isPresent())
                {
                    statement.setInt(5, songMeta.getRating().get());
                }
                else
                {
                    statement.setNull(5, Types.INTEGER);
                }

                if(songMeta.getPlayCounter().isPresent())
                {
                    statement.setInt(6, songMeta.getPlayCounter().get());
                }
                else
                {
                    statement.setNull(6, Types.INTEGER);
                }

                if(songMeta.getDate().isPresent())
                {
                    statement.setString(7, songMeta.getDate().get().toString());
                }
                else
                {
                    statement.setNull(7, Types.VARCHAR);
                }

                if(songMeta.getTrackNumber().isPresent())
                {
                    statement.setInt(8, songMeta.getTrackNumber().get());
                }
                else
                {
                    statement.setNull(8, Types.INTEGER);
                }

                if(songMeta.getCopyrightInformation().isPresent())
                {
                    statement.setString(9, songMeta.getCopyrightInformation().get().toString());
                }
                else
                {
                    statement.setNull(9, Types.VARCHAR);
                }

                if(songMeta.getPublisherWebpage().isPresent())
                {
                    statement.setString(10, songMeta.getPublisherWebpage().get().toString());
                }
                else
                {
                    statement.setNull(10, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    statement.setString(11, songMeta.getPicture().get().getMimeType());
                }
                else
                {
                    statement.setNull(11, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    statement.setString(12, songMeta.getPicture().get().getDescription());
                }
                else
                {
                    statement.setNull(12, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    //Blob pictureBlob = connection.createBlob();
                    //pictureBlob.setBytes(1, songMeta.getPicture().get().getData());
                    statement.setBytes(13, songMeta.getPicture().get().getData());
                }
                else
                {
                    statement.setNull(13, Types.BLOB);
                }

                statement.setString(14, song.getFilePath().toString());
                statement.executeUpdate();


            }

            //is song is not already in db, insert
            else
            {
                statement = connection.prepareStatement(insertSql);
                statement.setString(1, song.getFilePath().toString());

                if(songMeta.getSongTitle().isPresent())
                {
                    statement.setString(2, songMeta.getSongTitle().get());
                }
                else
                {
                    statement.setNull(2, Types.VARCHAR);
                }

                if(songMeta.getArtist().isPresent())
                {
                    statement.setString(3, songMeta.getArtist().get());
                }
                else
                {
                    statement.setNull(3, Types.VARCHAR);
                }

                if(songMeta.getGenre().isPresent())
                {
                    statement.setInt(4, songMeta.getGenre().get().getId());
                }
                else
                {
                    statement.setNull(4, Types.INTEGER);
                }

                if(songMeta.getAlbumTitle().isPresent())
                {
                    statement.setString(5, songMeta.getAlbumTitle().get());
                }
                else
                {
                    statement.setNull(5, Types.VARCHAR);
                }

                if(songMeta.getRating().isPresent())
                {
                    statement.setInt(6, songMeta.getRating().get());
                }
                else
                {
                    statement.setNull(6, Types.INTEGER);
                }

                if(songMeta.getPlayCounter().isPresent())
                {
                    statement.setInt(7, songMeta.getPlayCounter().get());
                }
                else
                {
                    statement.setNull(7, Types.INTEGER);
                }

                if(songMeta.getDate().isPresent())
                {
                    statement.setString(8, songMeta.getDate().get().toString());
                }
                else
                {
                    statement.setNull(8, Types.VARCHAR);
                }

                if(songMeta.getTrackNumber().isPresent())
                {
                    statement.setInt(9, songMeta.getTrackNumber().get());
                }
                else
                {
                    statement.setNull(9, Types.INTEGER);
                }

                if(songMeta.getCopyrightInformation().isPresent())
                {
                    statement.setString(10, songMeta.getCopyrightInformation().get().toString());
                }
                else
                {
                    statement.setNull(10, Types.VARCHAR);
                }

                if(songMeta.getPublisherWebpage().isPresent())
                {
                    statement.setString(11, songMeta.getPublisherWebpage().get().toString());
                }
                else
                {
                    statement.setNull(11, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    statement.setString(12, songMeta.getPicture().get().getMimeType());
                }
                else
                {
                    statement.setNull(12, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    statement.setString(13, songMeta.getPicture().get().getDescription());
                }
                else
                {
                    statement.setNull(13, Types.VARCHAR);
                }

                if(songMeta.getPicture().isPresent())
                {
                    //Blob pictureBlob = connection.createBlob();
                    //pictureBlob.setBytes(1, songMeta.getPicture().get().getData());
                    statement.setBytes(14, songMeta.getPicture().get().getData());
                }
                else
                {
                    statement.setNull(14, Types.BLOB);
                }
                statement.execute();

            }
        }

        catch (SQLException e)
        {
            e.printStackTrace();
            throw new DatabaseWriteException("couldnt write to database");
        }
    }

    public void writeSongs(Set<Song> songs)
    {
        Validate.requireNonNullNotEmpty(songs);
        for(Song s : songs)
        {
            writeSong(s);
        }
    }

    public void delete()
    {
        if(!isOpen())
        {
            throw new IllegalStateException("couldn't delete because database is already closed");
        }

        else
        {
            String sqlDeleteSongs = "DELETE FROM songs";
            String sqlDeleteGenres = "DELETE FROM genres";
            try
            {
                PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteSongs);
                deleteStatement.execute();
                deleteStatement = connection.prepareStatement(sqlDeleteGenres);
                deleteStatement.execute();
            }

            catch (SQLException e)
            {
                e.printStackTrace();
                throw new DatabaseWriteException("couldn't delete");
            }
        }
    }

    public List<Genre> loadGenres()
    {
        List<Genre> resultList = new ArrayList<Genre>();
        String sqlSelectGenres = "SELECT id, genreName FROM genres ORDER BY id ASC";
        if(isOpen())
        {
            try
            {
                PreparedStatement genresStatement = connection.prepareStatement(sqlSelectGenres);
                ResultSet results = genresStatement.executeQuery();
                while(results.next())
                {
                    Genre g = new Genre(results.getInt("id"), results.getString("genreName"));
                    resultList.add(g);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                throw new DatabaseReadException("couldn't load genres");
            }
        }
        return resultList;
    }

    public Set<Song> loadSongs()
    {
        Set<Song> resultList = new HashSet<Song>();
        String sqlSelectSongs = "SELECT filepath, songtitle, artist, genreId, albumTitle, rating, playCounter, date, trackNumber, copyrightInfo, webpage, pictureMime, pictureDesc, picture FROM songs";
        List<Genre> genreList = loadGenres();
        GenreManager genreManager = new GenreManager(genreList);

        if(isOpen())
        {
            try
            {
                PreparedStatement songsStatement = connection.prepareStatement(sqlSelectSongs);
                ResultSet results = songsStatement.executeQuery();
                while(results.next())
                {
                    Path filepath;
                    Metadata metadata = new Metadata();
                    String filepathString = results.getString("filepath");
                    String songtitle = results.getString("songtitle");
                    String artist = results.getString("artist");
                    Integer genreId = (Integer) results.getObject("genreId");
                    String albumTitle = results.getString("albumTitle");
                    Integer rating = (Integer) results.getObject("rating");
                    Integer playCounter = (Integer) results.getObject("playCounter");
                    String date = results.getString("date");
                    Integer trackNumber = (Integer) results.getObject("trackNumber");
                    String copyrightInfo = results.getString("copyrightInfo");
                    String webpage = results.getString("webpage");
                    String pictureMime = results.getString("pictureMime");
                    String pictureDesc = results.getString("pictureDesc");
                    byte[] picture = results.getBytes("picture");

                    filepath = Paths.get(filepathString);
                    Validate.requireFileExists(filepath);

                    if(songtitle != null)
                    {
                        metadata.setSongTitle(results.getString("songtitle"));
                    }
                    if(artist != null)
                    {
                        metadata.setArtist(results.getString("artist"));
                    }


                    if(genreId != null)
                    {
                        Optional<Genre> optionalGenre = genreManager.getGenreById(genreId);
                        if(optionalGenre.isPresent())
                        {
                            metadata.setGenre(optionalGenre.get());
                        }
                        else
                        {
                            throw new InconsistentDatabaseException("genre ist nicht vorhanden: " + genreId);
                        }
                    }

                    if(albumTitle != null)
                    {
                        metadata.setAlbumTitle(results.getString("albumTitle"));
                    }
                    if(rating != null)
                    {
                        metadata.setRating(results.getInt("rating"));
                    }
                    if(playCounter != null)
                    {
                        metadata.setPlayCounter(results.getInt("playCounter"));
                    }

                    if(date != null)
                    {
                        if (date.matches("\\d{4}-\\d{2}-\\d{2}[a-zA-Z]{1}\\d{2}:\\d{2}:\\d{2}"))
                        {
                            metadata.setDate(LocalDateTime.parse(date));
                        }

                        else if (date.matches("\\d{4}-\\d{2}-\\d{2}"))
                        {
                            metadata.setDate(LocalDate.parse(date));
                        }

                        else if (date.matches("\\d{4}-\\d{2}"))
                        {
                            metadata.setDate(YearMonth.parse(date));
                        }

                        else if (date.matches("\\d{4}"))
                        {
                            metadata.setDate(Year.parse(date));
                        }
                    }
                    if(trackNumber != null)
                    {
                        metadata.setTrackNumber(results.getInt("trackNumber"));
                    }

                    try
                    {
                        if(copyrightInfo != null)
                        {
                            URL copyrightinfo = new URL(results.getString("copyrightInfo"));
                            metadata.setCopyrightInformation(copyrightinfo);
                        }
                    }
                    catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                        throw new InconsistentDatabaseException("incorrectly formatted url: " + results.getString("copyrightInfo"));
                    }

                    try
                    {
                        if(webpage != null)
                        {
                            URL webpageurl = new URL(results.getString("webpage"));
                            metadata.setPublisherWebpage(webpageurl);
                        }
                    }
                    catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                        throw new InconsistentDatabaseException("incorrectly formatted webpage url: " + results.getString("webpage"));
                    }

                    if(pictureMime != null && pictureDesc != null && picture != null)
                    {
                        metadata.setPicture(new MetadataPicture(pictureMime, pictureDesc, picture));
                    }

                    Song song = new Song(filepath, metadata);
                   // System.out.println(song.toString());
                    resultList.add(song);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                throw new DatabaseReadException("couldn't load songs");
            }
        }
        return resultList;
    }

    public boolean isOpen()
    {
        if(connection != null)
        {
            try
            {
                return !connection.isClosed();
            }

            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        else
        {
            return false;
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseConnection that = (DatabaseConnection) o;

        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode()
    {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public void close()
    {
        try
        {
            if(connection != null)
            {
                if(!connection.isClosed())
                {
                    connection.close();
                }
            }
        }
        catch (SQLException ex) {            ex.printStackTrace();}
    }
}
