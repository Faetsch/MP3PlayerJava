package jpp.wuetunes.model.metadata;

import jpp.wuetunes.util.Validate;

import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Optional;

public class Metadata
{
    private Optional<String> songTitle = Optional.empty();
    private Optional<String> artist = Optional.empty();
    private Optional<String> albumTitle = Optional.empty();
    private Optional<Integer> trackNumber = Optional.empty(), rating = Optional.empty(), playCounter = Optional.empty();
    private Optional<Genre> genre = Optional.empty();
    private Optional<Temporal> date = Optional.empty();
    private Optional<MetadataPicture> picture = Optional.empty();
    private Optional<URL> copyrightInformation = Optional.empty(), publisherWebpage = Optional.empty();

    public Optional<String> getSongTitle()
    {
        return songTitle;
    }

    public void setSongTitle(String songTitle)
    {
        this.songTitle = Optional.ofNullable(Validate.requireNonNullNotEmpty(songTitle));
    }

    public Optional<String> getArtist()
    {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = Optional.ofNullable(Validate.requireNonNullNotEmpty(artist));
    }

    public Optional<String> getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = Optional.ofNullable(Validate.requireNonNullNotEmpty(albumTitle));
    }

    public Optional<Integer> getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = Optional.ofNullable(Validate.requireNonNegative(trackNumber));
    }

    public Optional<Integer> getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Optional.ofNullable(Validate.requireBetween(rating, 0, 5));
    }

    public Optional<Integer> getPlayCounter() {
        return playCounter;
    }

    public void setPlayCounter(int playCounter) {
        this.playCounter = Optional.ofNullable(Validate.requireNonNegative(playCounter));
    }

    public Optional<Genre> getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = Optional.ofNullable(Validate.requireNonNull(genre));
    }

    public Optional<Temporal> getDate() {
        return date;
    }

    public void setDate(Temporal date) {
        this.date = Optional.ofNullable(Validate.requireNonNull(date));
    }

    public Optional<MetadataPicture> getPicture() {
        return picture;
    }

    public void setPicture(MetadataPicture picture) {
        this.picture = Optional.ofNullable(Validate.requireNonNull(picture));
    }

    public Optional<URL> getCopyrightInformation() {
        return copyrightInformation;
    }

    public void setCopyrightInformation(URL copyrightInformation) {
        this.copyrightInformation = Optional.ofNullable(Validate.requireNonNull(copyrightInformation));
    }

    public Optional<URL> getPublisherWebpage() {
        return publisherWebpage;
    }

    public void setPublisherWebpage(URL publisherWebpage)
    {
        this.publisherWebpage = Optional.ofNullable(Validate.requireNonNull(publisherWebpage));
    }


    @Override
    public String toString()
    {
        String result = "";
        String ratingString = "";
        if(rating.isPresent())
        {
            for(int i = 1; i <= rating.get(); i++)
            {
                ratingString += "*";
            }
        }

        StringBuilder b = new StringBuilder(result);
        b.append(createLine("Song title", getSongTitle()));
        b.append(createLine("Artist", getArtist()));
        b.append(createLine("Album title", getAlbumTitle()));
        b.append(createLine("Track number", getTrackNumber()));
        b.append(createLine("Genre", getGenre()));
        b.append(createLine("Date", getDate()));
        b.append(createLine("Picture", getPicture()));
        b.append(createLine("Copyright information", getCopyrightInformation()));
        b.append(createLine("Publisher webpage", getPublisherWebpage()));
        if(rating.isPresent())
        {
            b.append(createLine("Rating", Optional.of(ratingString)));
        }
        b.append(createLine("Play counter", getPlayCounter()));
        result = b.toString();
        if(result.endsWith("\n"))
        {
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    //@Override
   // public boolean equalz(Object obj)
   // {
   //     if(!(obj instanceof Metadata))
   //     {
   //         return false;
    //    }
    //    Metadata other = (Metadata) obj;
    //    return getSongTitle().equals(other.getSongTitle()) &&
    //            getArtist().equals(other.getSongTitle()) &&
    //            getAlbumTitle().equals(other.getAlbumTitle()) &&
    //            getTrackNumber().equals(other.getTrackNumber()) &&
    //            getGenre().equals(other.getGenre()) &&
   //             getDate().equals(other.getDate()) &&
    //            getPicture().equals(other.getPicture()) &&
   //             getCopyrightInformation().equals(other.getCopyrightInformation()) &&
   //             getPublisherWebpage().equals(other.getPublisherWebpage()) &&
   //             getRating().equals(other.getRating()) &&
    //            getPlayCounter().equals(other.getPlayCounter());
  //  }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        if (songTitle != null ? !songTitle.equals(metadata.songTitle) : metadata.songTitle != null) return false;
        if (artist != null ? !artist.equals(metadata.artist) : metadata.artist != null) return false;
        if (albumTitle != null ? !albumTitle.equals(metadata.albumTitle) : metadata.albumTitle != null) return false;
        if (trackNumber != null ? !trackNumber.equals(metadata.trackNumber) : metadata.trackNumber != null)
            return false;
        if (rating != null ? !rating.equals(metadata.rating) : metadata.rating != null) return false;
        if (playCounter != null ? !playCounter.equals(metadata.playCounter) : metadata.playCounter != null)
            return false;
        if (genre != null ? !genre.equals(metadata.genre) : metadata.genre != null) return false;
        if (date != null ? !date.equals(metadata.date) : metadata.date != null) return false;
        if (picture != null ? !picture.equals(metadata.picture) : metadata.picture != null) return false;
        if (copyrightInformation != null ? !copyrightInformation.equals(metadata.copyrightInformation) : metadata.copyrightInformation != null)
            return false;
        return publisherWebpage != null ? publisherWebpage.equals(metadata.publisherWebpage) : metadata.publisherWebpage == null;
    }

    @Override
    public int hashCode() {
        int result = songTitle != null ? songTitle.hashCode() : 0;
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (albumTitle != null ? albumTitle.hashCode() : 0);
        result = 31 * result + (trackNumber != null ? trackNumber.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (playCounter != null ? playCounter.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (copyrightInformation != null ? copyrightInformation.hashCode() : 0);
        result = 31 * result + (publisherWebpage != null ? publisherWebpage.hashCode() : 0);
        return result;
    }

    private String createLine(String prefix, Optional suffix)
    {
        String line = "";
        if(suffix.isPresent())
        {
            if(suffix.get() instanceof  Temporal)
            {
                line = String.format("%s: %s\n", prefix, suffix.get().toString().substring(0, 4));
            }
            else
            {
                line = String.format("%s: %s\n", prefix, suffix.get().toString());
            }
        }
        return line;
    }
}
