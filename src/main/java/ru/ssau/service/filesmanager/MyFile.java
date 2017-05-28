package ru.ssau.service.filesmanager;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class MyFile{
    private byte[]      bytes;
    private HttpHeaders headers;

    MyFile( byte[] bytes ){
        this.bytes = bytes;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.valueOf( "image/png" ) );
        this.headers = headers;
    }

    public byte[] getBytes(){
        return bytes;
    }

    public HttpHeaders getHeaders(){
        return headers;
    }
}
