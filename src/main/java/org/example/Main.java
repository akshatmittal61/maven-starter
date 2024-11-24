package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Mp3Util mp3Util = new Mp3Util();
        String filePath = "./src/main/resources/rwylm.mp3";
        Path path = Paths.get(filePath);
        String artist = mp3Util.getArtist(path);
        System.out.println(artist);
    }
}