package org.example;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;
import java.nio.file.Path;

public class Mp3Util {
    public static String getArtist(Path path) {
        try {
            Mp3File mp3File = new Mp3File(path);
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            return id3v2Tag != null ? id3v2Tag.getArtist() : "";
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            return "";
        }
    }
}
