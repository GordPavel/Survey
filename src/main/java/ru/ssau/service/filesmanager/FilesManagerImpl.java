package ru.ssau.service.filesmanager;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;

@Service
public class FilesManagerImpl implements FilesManager{

    // TODO: 02.04.17 Прописать директорию с фалами в properties

    public static String filesDir = "/surveyProjectFiles/";

    @Override
    public MyFile getFile( String location ) throws IOException{
        return new MyFile( readAllBytes( get( filesDir + location + "." + getFileType( location ) ) ),

                           location.substring( location.lastIndexOf( "/" ) + 1 ),

                           getFileType( location ),

                           getFileMediaType( location ) );
    }

    @Override
    public boolean saveFile( byte[] file, String location ) throws IOException{
        write( Files.createFile( get( filesDir + location ) ), file );
        return true;
    }

    @Override
    public String getFilesDir(){
        return filesDir;
    }

    private MediaType getFileMediaType( String location ){
        String format = getFileType( location );
        String type   = "";
        switch( format ){
            case "jpeg":
            case "png":
            case "gif":
                type = "image";
                break;
            case "txt":
            case "doc":
            case "docx":
                type = "text";
        }
        return MediaType.parseMediaType( type + "/" + format );
    }

    private String getFileType( String location ){
        @SuppressWarnings( "ConstantConditions" ) String fileName = Arrays.stream( new File( filesDir )

                                                                                           .listFiles(
                                                                                                   ( dir, name ) -> name.startsWith(
                                                                                                           location ) ) ).findFirst()

                .orElseThrow( () -> new IllegalArgumentException( location ) ).getName();
        return fileName.substring( fileName.indexOf( '.' ) + 1 );
    }
}
