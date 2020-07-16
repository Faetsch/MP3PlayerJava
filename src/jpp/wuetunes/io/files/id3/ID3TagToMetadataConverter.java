package jpp.wuetunes.io.files.id3;

import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.model.metadata.GenreManager;
import jpp.wuetunes.model.metadata.Metadata;
import jpp.wuetunes.model.metadata.MetadataPicture;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Optional;

public class ID3TagToMetadataConverter
{
    private GenreManager genremanager;

    public ID3TagToMetadataConverter()
    {
        genremanager = new GenreManager();
    }

    public ID3TagToMetadataConverter(Collection<Genre> genres)
    {
        genremanager = new GenreManager(genres);
    }

    public Metadata convert(ID3Tag tag)
    {
        Metadata result = new Metadata();
        for(ID3FrameType type : ID3FrameType.values())
        {
            switch (type)
            {
                case TIT2:
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TIT2)
                        {
                            result.setSongTitle(((ID3FrameBodyTextInformation)frame.getBody()).getText());
                            break;
                        }
                    }
                    break;

                case TPE1:
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TPE1)
                        {
                            result.setArtist(((ID3FrameBodyTextInformation)frame.getBody()).getText());
                            break;
                        }
                    }
                    break;

                case TALB:
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TALB)
                        {
                            result.setAlbumTitle(((ID3FrameBodyTextInformation)frame.getBody()).getText());
                            break;
                        }
                    }
                    break;


                case WCOP:
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.WCOP)
                        {
                            result.setCopyrightInformation((((ID3FrameBodyURLLink)frame.getBody()).getUrl()));
                            break;
                        }
                    }
                    break;

                case WPUB:
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.WPUB)
                        {
                            result.setPublisherWebpage((((ID3FrameBodyURLLink)frame.getBody()).getUrl()));
                            break;
                        }
                    }
                    break;

                case APIC:
                    String mimeType;
                    String description;
                    byte[] data;
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.APIC)
                        {
                            ID3FrameBodyAttachedPicture currPic = (ID3FrameBodyAttachedPicture) frame.getBody();
                            if(currPic.getImageType() == 3)
                            {
                                mimeType = currPic.getMimeType();
                                description = currPic.getDescription();
                                data = currPic.getPictureData();
                                MetadataPicture metadataPicture = new MetadataPicture(mimeType, description, data);
                                result.setPicture(metadataPicture);
                                break;
                            }

                        }
                    }

                    if(!result.getPicture().isPresent())
                    {
                        for(ID3Frame frame : tag.getFrames())
                        {
                            if(frame.getHeader().getFrameType() == ID3FrameType.APIC)
                            {
                                ID3FrameBodyAttachedPicture currPic = (ID3FrameBodyAttachedPicture) frame.getBody();
                                mimeType = currPic.getMimeType();
                                description = currPic.getDescription();
                                data = currPic.getPictureData();
                                MetadataPicture metadataPicture = new MetadataPicture(mimeType, description, data);
                                result.setPicture(metadataPicture);
                                break;
                            }
                        }
                    }
                    break;

                case TCON:
                    ID3FrameBodyTextInformation currText;
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TCON)
                        {
                            currText = ((ID3FrameBodyTextInformation) frame.getBody());
                            if(isInteger(currText.getText()))
                            {
                                int genreNum = Integer.parseInt(currText.getText());
                                Optional<Genre> optGenre = genremanager.getGenreById(genreNum);
                                if(!optGenre.isPresent())
                                {
                                    Genre newGenre = new Genre(genreNum, "unknown genre " + genreNum);
                                    genremanager.add(newGenre);
                                    result.setGenre(newGenre);
                                }
                                else
                                {
                                    result.setGenre(optGenre.get());
                                }
                            }
                            else
                            {
                                Optional<Genre> optGenre = genremanager.getGenreByName(currText.getText());
                                if(!optGenre.isPresent())
                                {
                                    genremanager.add(currText.getText());
                                    result.setGenre(genremanager.getGenreByName(currText.getText()).get());
                                }
                                else
                                {
                                    result.setGenre(optGenre.get());
                                }
                            }
                            break;
                        }
                    }
                    break;


                case TRCK:
                    ID3FrameBodyTextInformation currTrack;
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TRCK)
                        {
                            currTrack = (ID3FrameBodyTextInformation) frame.getBody();
                            if(isInteger(currTrack.getText()))
                            {
                                int trackNum = Integer.parseInt(currTrack.getText());
                                if(trackNum >= 0)
                                {
                                    result.setTrackNumber(trackNum);
                                }
                                else
                                {
                                    result.setTrackNumber(0);
                                }
                            }
                            else
                            {
                                result.setTrackNumber(0);
                            }
                            break;
                        }
                    }

                case TDRC:
                    ID3FrameBodyTextInformation currRecordText;
                    String currDateText;
                    for(ID3Frame frame : tag.getFrames())
                    {
                        if(frame.getHeader().getFrameType() == ID3FrameType.TDRC)
                        {
                            currRecordText = (ID3FrameBodyTextInformation) frame.getBody();
                            currDateText = currRecordText.getText();

                            if (currDateText.matches("\\d{4}-\\d{2}-\\d{2}[a-zA-Z]{1}\\d{2}:\\d{2}:\\d{2}"))
                            {
                                result.setDate(LocalDateTime.parse(currDateText));
                            }

                            else if (currDateText.matches("\\d{4}-\\d{2}-\\d{2}"))
                            {
                                result.setDate(LocalDate.parse(currDateText));
                            }

                            else if (currDateText.matches("\\d{4}-\\d{2}"))
                            {
                                result.setDate(YearMonth.parse(currDateText));
                            }

                            else if (currDateText.matches("\\d{4}"))
                            {
                                result.setDate(Year.parse(currDateText));
                            }
                            break;
                        }
                    }
                    break;

            }
        }
        return result;

    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
