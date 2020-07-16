package jpp.wuetunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jpp.wuetunes.gui.Controller;
import jpp.wuetunes.io.database.DatabaseConnection;
import jpp.wuetunes.io.files.SongsFileImportResult;
import jpp.wuetunes.io.files.SongsFileImporter;
import jpp.wuetunes.io.files.id3.ID3Tag;
import jpp.wuetunes.io.files.id3.ID3TagReader;
import jpp.wuetunes.io.files.id3.ID3TagToMetadataConverter;
import jpp.wuetunes.io.files.id3.ID3Utils;
import jpp.wuetunes.model.Playlist;
import jpp.wuetunes.model.Song;
import jpp.wuetunes.model.SongFilterFactory;
import jpp.wuetunes.model.SongLibrary;
import jpp.wuetunes.model.metadata.Genre;
import jpp.wuetunes.model.metadata.GenreManager;

import javax.naming.ldap.Control;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Predicate;

public class WueTunes extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("gui/sample.fxml"));
        primaryStage.setTitle("JPP MP3-Player");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();

       // ID3Tag tag2 = ID3TagReader.read(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dyslexic_Fudgicle_-_Impossible_Doors\\Dyslexic_Fudgicle_-_10_-_BTS.mp3"));
        //System.out.println(tag);

      //  ID3Tag tag = ID3TagReader.read(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dee_Yan-Key_-_Fast_Swing_reloaded\\Dee_Yan-Key_-_01_-_Lucky_Day.mp3"));
       // ID3TagToMetadataConverter converter2 = new ID3TagToMetadataConverter();
        //SongLibrary lib = new SongLibrary();
        //Song s = new Song(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dee_Yan-Key_-_Fast_Swing_reloaded\\Dee_Yan-Key_-_01_-_Lucky_Day.mp3"), converter.convert(tag));
       // Song s2 = new Song(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dyslexic_Fudgicle_-_Impossible_Doors\\Dyslexic_Fudgicle_-_10_-_BTS.mp3"), converter2.convert(tag2));
      //  DatabaseConnection databaseConnection = new DatabaseConnection(Paths.get("C:\\Users\\fatih\\Downloads\\medialibrary_default_genres.db"));
       // System.out.println(databaseConnection.getURL());
       // databaseConnection.open();
       // System.out.println(databaseConnection.isOpen());
       // databaseConnection.writeSong(s);
        //databaseConnection.writeSong(s2);
        //databaseConnection.delete();
        //databaseConnection.close();
       // System.out.println(databaseConnection.isOpen());

        //ID3Tag tag2 = ID3TagReader.read(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dyslexic_Fudgicle_-_Impossible_Doors\\Dyslexic_Fudgicle_-_10_-_BTS.mp3"));
        //ID3TagToMetadataConverter converter2 = new ID3TagToMetadataConverter();
        //Song s2 = new Song(Paths.get("C:\\Users\\fatih\\Desktop\\mp3\\Dyslexic_Fudgicle_-_Impossible_Doors\\Dyslexic_Fudgicle_-_10_-_BTS.mp3"), converter2.convert(tag2));
        //System.out.println(s2);

      //  ID3TagToMetadataConverter converter = new ID3TagToMetadataConverter();
      //  SongsFileImporter importer = new SongsFileImporter(new ArrayList<Genre>());
      //  SongsFileImportResult result = importer.importSongsFromFolder(Paths.get("C:\\Users\\fatih\\Desktop\\mp3"));
      // for(Song s : result.getSongs())
      //  {
      //      System.out.println(s.toString());
      //      System.out.println();
     //   }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
