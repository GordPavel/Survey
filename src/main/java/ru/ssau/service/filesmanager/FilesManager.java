package ru.ssau.service.filesmanager;

import java.io.IOException;

public interface FilesManager{
    MyFile getFile( String location ) throws IOException;

    boolean saveFile( byte[] file, String location ) throws IOException;

    boolean deleteFile( String location ) throws IOException;
}
