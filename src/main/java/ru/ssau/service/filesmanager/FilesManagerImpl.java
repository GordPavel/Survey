package ru.ssau.service.filesmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;

@Service
public class FilesManagerImpl implements FilesManager{

    @Autowired
    private Environment environment;

    @Override
    public MyFile getFile( String location ) throws IOException{
        return new MyFile( readAllBytes( get( environment.getProperty( "filesStorage" ) + location ) ) );
    }

    @Override
    public boolean saveFile( byte[] file, String location ) throws IOException{
        deleteFile( location );
        write( createFile( get( environment.getProperty( "filesStorage" ) + location + ".png" ) ), file );
        return true;
    }

    @Override
    public boolean deleteFile( String location ) throws IOException{
        Path path = Paths.get( environment.getProperty( "filesStorage" ) + location + ".png" );
        if( Files.exists( path ) )
            Files.delete( path );
        return true;
    }
}
