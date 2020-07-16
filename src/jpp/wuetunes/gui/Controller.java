package jpp.wuetunes.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;
import jpp.wuetunes.io.database.DatabaseConnection;
import jpp.wuetunes.io.files.SongsFileImportResult;
import jpp.wuetunes.io.files.SongsFileImporter;
import jpp.wuetunes.model.Playlist;
import jpp.wuetunes.model.Song;
import jpp.wuetunes.model.SongFilterFactory;
import jpp.wuetunes.model.SongLibrary;
import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.model.metadata.GenreManager;
import jpp.wuetunes.model.metadata.Metadata;
import jpp.wuetunes.model.metadata.MetadataPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller
{
    private SongLibrary songLibrary = new SongLibrary();
    private Playlist playList = new Playlist();
    private GenreManager genreManager = new GenreManager();
    private DatabaseConnection databaseConnection = new DatabaseConnection(Paths.get("C:\\test\\medialibrary_default_genres (1).db"));
    private ObservableList<Song> listSongLibrary = FXCollections.observableArrayList();
    private ObservableList<Song> listPlayList = FXCollections.observableArrayList();
    private ObservableList<String> ratingValues = FXCollections.observableArrayList();
    private DirectoryChooser fileChooser = new DirectoryChooser();
    boolean isOnPlay = false;
    boolean isOnShuffle = false;
    boolean isStopped = false;
    MediaPlayer mediaPlayer;


    public Controller()
    {
        //init();
    }

    @FXML TableView<Song> tableSongLibrary;
    @FXML TableColumn colLibImg;
    @FXML TableColumn colLibTitle;
    @FXML TableColumn colLibArtist;
    @FXML TableColumn colLibAlbum;
    @FXML TableColumn colLibTrack;
    @FXML TableColumn colLibGenre;
    @FXML TableColumn colLibYear;
    @FXML TableColumn colLibRating;
    @FXML TableColumn colLibCounter;
    @FXML TableColumn colLibWebpage;
    @FXML TableColumn colLibCopyright;

    @FXML TableView<Song> tablePlayList;
    @FXML TableColumn colPlayTitle;
    @FXML TableColumn colPlayArtist;
    @FXML TableColumn colPlayAlbum;
    @FXML TableColumn colPlayNum;

    @FXML TextField txtArtistFilter;
    @FXML TextField txtGenreFilter;
    @FXML TextField txtYearFilter;
    @FXML TextField txtAlbumFilter;
    @FXML TextField txtRatingFilter;

    @FXML Button btnImport;
    @FXML Button btnApplyFilter;
    @FXML Button btnShuffle;
    @FXML Button btnPrevious;
    @FXML Button btnPlay;
    @FXML Button btnNext;

    @FXML Slider sliderSongDuration;
    @FXML Slider sliderVolume;

    @FXML Label lblCurrSong;

    @FXML ImageView imgviewCurrSong;

    @FXML
    private void initialize()
    {
        //Image img = new Image(new ByteArrayInputStream(buffer));

        ratingValues.add("---");
        ratingValues.add("*");
        ratingValues.add("**");
        ratingValues.add("***");
        ratingValues.add("****");
        ratingValues.add("*****");

        sliderVolume.valueProperty().addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                mediaPlayer.setVolume(sliderVolume.getValue() / 100);
            }
        });

      //  sliderSongDuration.valueProperty().addListener(new InvalidationListener() {
      //      public void invalidated(Observable ov) {
      //          if (sliderSongDuration.isValueChanging()) {
      //              // multiply duration by percentage calculated by slider position
      //              if(duration!=null) {
      //                  mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
      //              }
      //              updateValues();
      //          }
      //      }
      //  });

        colLibTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getSongTitle().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getSongTitle().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });

        colLibArtist.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getArtist().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getArtist().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });


        colLibAlbum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getAlbumTitle().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getAlbumTitle().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });


        colLibGenre.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getGenre().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getGenre().get().getName());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });

        colLibYear.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getDate().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getDate().get().toString().substring(0, 4));

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });

        colLibRating.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Integer>, ObservableValue<Integer>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, Integer> p)
            {
               if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getRating().isPresent())
                        //return new SimpleStringProperty("ahhhh");
                        return new SimpleIntegerProperty(p.getValue().getMetadata().getRating().get());

                    else
                        return new SimpleIntegerProperty(0);
                }
                return new SimpleIntegerProperty(0);
            }
        });


        //colLibRating.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        tableSongLibrary.setRowFactory( tv ->
        {
            TableRow<Song> row = new TableRow<Song>();
            row.setOnMouseClicked(event ->
            {
                if (event.getClickCount() == 2 && (! row.isEmpty()) )
                {
                    Song rowData = row.getItem();
                    ChoiceDialog<String> dialog = new ChoiceDialog<String>("---", ratingValues);
                    dialog.setHeaderText("Rate this song:\n" + rowData.getMetadata().getSongTitle().get());
                    Optional<String> result = dialog.showAndWait();
                    if(result.isPresent())
                    {
                        rowData.getMetadata().setRating(ratingValues.indexOf(result.get()));
                        System.out.println(result.get());
                        tableSongLibrary.refresh();
                        databaseConnection.open();
                        databaseConnection.writeSong(rowData);
                        databaseConnection.close();
                    }

                }
            });
            return row ;
        });


        colLibRating.setEditable(true);

        colLibCounter.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Integer>, ObservableValue<Integer>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, Integer> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getPlayCounter().isPresent())
                        return new SimpleIntegerProperty(p.getValue().getMetadata().getPlayCounter().get());

                    else
                        return new SimpleIntegerProperty();
                }
                return  new SimpleIntegerProperty();
            }
        });

        colLibTrack.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Integer>, ObservableValue<Integer>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, Integer> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getTrackNumber().isPresent())
                        return new SimpleIntegerProperty(p.getValue().getMetadata().getTrackNumber().get());

                    else
                        return new SimpleIntegerProperty();
                }
                return  new SimpleIntegerProperty();
            }
        });

        colLibWebpage.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getPublisherWebpage().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getPublisherWebpage().get().toString());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });


        colLibCopyright.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getCopyrightInformation().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getCopyrightInformation().get().toString());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });

        tableSongLibrary.setItems(listSongLibrary);

        colPlayTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getSongTitle().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getSongTitle().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });
        colPlayAlbum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getAlbumTitle().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getAlbumTitle().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });
        colPlayArtist.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, String> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getArtist().isPresent())
                        return new SimpleStringProperty(p.getValue().getMetadata().getArtist().get());

                    else
                        return new SimpleStringProperty("");
                }
                return  new SimpleStringProperty("");
            }
        });
        colPlayNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, Integer>, ObservableValue<Integer>>()
        {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Song, Integer> p)
            {
                if(p.getValue() != null)
                {
                    if(p.getValue().getMetadata().getTrackNumber().isPresent())
                        return new SimpleIntegerProperty(p.getValue().getMetadata().getTrackNumber().get());

                    else
                        return new SimpleIntegerProperty();
                }
                return  new SimpleIntegerProperty();
            }
        });

        tablePlayList.setItems(listPlayList);

        databaseConnection.open();
        for(Song s : databaseConnection.loadSongs())
        {
            //listSongLibrary.add(s);
            songLibrary.add(s);
        }
        listSongLibrary.removeAll(listSongLibrary);
        for(Song s :songLibrary.getSongs())
        {
            listSongLibrary.add(s);
        }

        databaseConnection.close();
    }

    @FXML
    public void importSongs(ActionEvent e)
    {
        databaseConnection.open();
        File selectedDirectory = fileChooser.showDialog(tableSongLibrary.getScene().getWindow());
        SongsFileImporter songsFileImporter = new SongsFileImporter(databaseConnection.loadGenres());
        try
        {
            if(selectedDirectory != null)
            {
                SongsFileImportResult importResult = songsFileImporter.importSongsFromFolder(selectedDirectory.toPath());
                databaseConnection.writeSongs(importResult.getSongs());
                for(Song s : importResult.getSongs())
                {
                    //listSongLibrary.add(s);
                    songLibrary.add(s);
                }
                listSongLibrary.removeAll(listSongLibrary);
                for(Song s :songLibrary.getSongs())
                {
                    listSongLibrary.add(s);
                }


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Import Results");
                alert.setHeaderText("Import finished");
                String contentText = String.format("Successful imports: %d\nFailed imports: %d", importResult.getSongs().size(), importResult.getFailures().size());
                alert.setContentText(contentText);
                alert.show();
            }
        }

        catch (IOException e1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText("Couldn't import songs");
            alert.show();
        }
        databaseConnection.close();
    }

    @FXML
    public void addToPlaylist(ActionEvent e)
    {
        Song currSong = tableSongLibrary.getSelectionModel().getSelectedItem();
        if(currSong != null)
        {
            playList.add(currSong);
            listPlayList.add(currSong);
        }
    }

    @FXML
    public void removeFromPlaylist(ActionEvent e)
    {
        int currIndex = tablePlayList.getSelectionModel().getSelectedIndex();
        if(currIndex > -1)
        {
            playList.removeAt(currIndex);
            listPlayList.remove(currIndex);
        }
    }

    @FXML
    public void moveUpOnPlaylist(ActionEvent e)
    {
        int currIndex = tablePlayList.getSelectionModel().getSelectedIndex();
        if(currIndex > 0)
        {
            Song currSong = tablePlayList.getSelectionModel().getSelectedItem();
            playList.removeAt(currIndex);
            listPlayList.remove(currIndex);
            playList.addAt(currIndex-1, currSong);
            listPlayList.add(currIndex-1, currSong);
            tablePlayList.getSelectionModel().select(currIndex-1);
            //System.out.println(tablePlayList.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    public void moveDownOnPlaylist(ActionEvent e)
    {
        int currIndex = tablePlayList.getSelectionModel().getSelectedIndex();
        if(currIndex != tablePlayList.getItems().size()-1)
        {
            Song currSong = tablePlayList.getSelectionModel().getSelectedItem();
            playList.removeAt(currIndex);
            listPlayList.remove(currIndex);
            playList.addAt(currIndex+1, currSong);
            listPlayList.add(currIndex+1, currSong);
            tablePlayList.getSelectionModel().select(currIndex+1);

        }
    }

    @FXML
    public void applyFilters(ActionEvent e)
    {
        ArrayList<Predicate<Song>> predicates = new ArrayList<Predicate<Song>>();
        if(!txtAlbumFilter.getText().equals(""))
        {
            predicates.add(SongFilterFactory.getAlbumTitleFilter(txtAlbumFilter.getText()));
        }

        if(!txtYearFilter.getText().equals("") && isInteger(txtYearFilter.getText()))
        {
            predicates.add(SongFilterFactory.getYearFilter(Integer.parseInt(txtYearFilter.getText())));
        }

        if(!txtRatingFilter.getText().equals("") && isInteger(txtRatingFilter.getText()))
        {
            predicates.add(SongFilterFactory.getMinRatingFilter((Integer.parseInt(txtRatingFilter.getText()))));
        }

        if(!txtArtistFilter.getText().equals(""))
        {
            predicates.add(SongFilterFactory.getArtistFilter(txtArtistFilter.getText()));
        }

        if(!txtGenreFilter.getText().equals(""))
        {
            databaseConnection.open();
            GenreManager genreManager = new GenreManager(databaseConnection.loadGenres());
            databaseConnection.close();
            Optional<Genre> genre = genreManager.getGenreByName(txtGenreFilter.getText());
            if(genre.isPresent())
            {
                predicates.add(SongFilterFactory.getGenreFilter(genre.get()));
            }
        }

        Predicate<Song> filter;
        if(predicates.size() > 0)
        {
            filter = SongFilterFactory.combineAnd(predicates);
        }
        else
        {
            filter = new Predicate<Song>() {
                @Override
                public boolean test(Song song) {
                    return true;
                }
            };
        }
        tableSongLibrary.setItems(listSongLibrary.filtered(filter));
    }

    private boolean isInteger(String s)
    {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    @FXML
    public void playPause(ActionEvent e)
    {
        //Optional<Song> currSong = playList.getCurrentSong();
        //pause
        if(isOnPlay)
        {
            if(mediaPlayer != null)
            {
                mediaPlayer.pause();
            }
            btnPlay.setText("Play");
            isOnPlay = false;
        }

        //play
        else
        {
            if(mediaPlayer != null)
            {
                Optional<Song> currSong = playList.getCurrentSong();
                if(currSong.isPresent())
                {
                    Media currMedia = new Media(currSong.get().getFilePath().toString());
                    mediaPlayer = new MediaPlayer(currMedia);
                    mediaPlayer.play();
                }
            }
            btnPlay.setText("Pause");
            isOnPlay = true;
        }

    }

    @FXML
    public void stop(ActionEvent e)
    {
        isStopped = true;
        if(mediaPlayer != null)
        {
            mediaPlayer.pause();
        }
        btnPlay.setText("Play");
        isOnPlay = false;
    }

    @FXML
    public void shuffle(ActionEvent e)
    {
        if(isOnShuffle)
        {
            btnShuffle.setText("Shuffle: Off");
            playList.setShuffle(false);
            isOnShuffle = false;
        }

        else
        {
            btnShuffle.setText("Shuffle: On");
            playList.setShuffle(true);
            isOnShuffle = true;
        }
    }

    @FXML
    public void next(ActionEvent e)
    {
        Optional<Integer> currSong = playList.next();
        if(currSong.isPresent())
        {
            if(mediaPlayer!= null)
            {
                mediaPlayer.stop();
                Media currMedia = new Media(playList.getCurrentSong().get().getFilePath().toString());
                mediaPlayer = new MediaPlayer(currMedia);
                mediaPlayer.play();
            }
        }
    }

    public void previous(ActionEvent e)
    {
        Optional<Integer> currSong = playList.previous();
        if(currSong.isPresent())
        {
            if(mediaPlayer != null)
            {
                mediaPlayer.stop();
                Media currMedia = new Media(playList.getCurrentSong().get().getFilePath().toString());
                mediaPlayer = new MediaPlayer(currMedia);
                mediaPlayer.play();
            }
        }
    }

    private Image convertToJavaFXImage(byte[] raw, final int width, final int height) {
        WritableImage image = new WritableImage(width, height);
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(raw);
            BufferedImage read = ImageIO.read(bis);
            image = SwingFXUtils.toFXImage(read, null);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }
}
