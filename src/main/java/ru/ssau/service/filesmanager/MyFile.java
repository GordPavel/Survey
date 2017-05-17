package ru.ssau.service.filesmanager;

import org.springframework.http.MediaType;

public class MyFile{
    private byte[]    bytes;
    private String    name;
    private String    type;
    private MediaType mediaType;

    public MyFile( byte[] bytes, String name, String type, MediaType mediaType ){
        this.bytes = bytes;
        this.name = name;
        this.type = type;
        this.mediaType = mediaType;
    }

    public byte[] getBytes(){
        return bytes;
    }

    public void setBytes( byte[] bytes ){
        this.bytes = bytes;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public void setType( String type ){
        this.type = type;
    }

    public MediaType getMediaType(){
        return mediaType;
    }

    public void setMediaType( MediaType mediaType ){
        this.mediaType = mediaType;
    }
}
