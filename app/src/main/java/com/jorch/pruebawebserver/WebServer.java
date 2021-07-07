package com.jorch.pruebawebserver;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoFileUpload;
import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
    public static final String TAG = "WebServer";
    private Context context;

    public WebServer(int port, Context context) {
        super(8000);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        AssetManager assetManager = context.getAssets();
        String mimetype = "text/html";
        InputStream inputStream = null;
        if(session.getMethod() == Method.POST){
            try {
                /*
                session.parseBody(new HashMap<>());

                if(session.getParameters().get("origin").get(0).equals("prueba1")){
                    return newFixedLengthResponse("-->POST: "+
                            session.getQueryParameterString()+" HEADER --> "+
                            session.getHeaders().get("referer").substring(session.getHeaders().get("referer").lastIndexOf("/")+1)
                    );
                }*/
                //else if(session.getParameters().get("origin").get(0).equals("prueba2")){
                    NanoFileUpload uploader = new NanoFileUpload(new DiskFileItemFactory());
                    Map<String,List<FileItem>> files = new HashMap<String, List<FileItem>>();
                    List<FileItem> parseRequest = uploader.parseRequest(session);

                    File file = new File(context.getFilesDir().getAbsolutePath()+"prueba.pdf");
                    DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
                    stream.write(parseRequest.get(0).get());
                    stream.close();

                    //PROBAR A GUARDAR TXT Y LEERLO
                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT,
                            "file name: " +parseRequest.get(0).getName()+" | "+
                            "input file name: " + parseRequest.get(0).getFieldName()+" | " +
                                    "content type: "+parseRequest.get(0).getContentType()+" | " +
                                    "lenght-bytes: "+parseRequest.get(0).get().length+" | " +
                                "inputstream: "+(parseRequest.get(0).getInputStream() != null)+" | "+
                                "file size: "+parseRequest.get(0).getSize()+" | " +"RUTA ACTUAL: ->");
               // }
            } catch (Exception e) {
                e.printStackTrace();
            } /*catch (ResponseException e) {
                e.printStackTrace();
            } catch (FileUploadException e) {
                e.printStackTrace();
            }*/
        }
        try{
            if(uri.equals("/favicon.ico")){
                inputStream = assetManager.open("web/favicon.ico");
                mimetype = "image/x-icon";
            }
            else if (uri.equals("/")){
                inputStream = assetManager.open("web/index.html");
                mimetype = "text/html";
            }
            else{
                uri = uri.substring(1);
                if(uri.contains(".html") || uri.contains(".htm")){
                    mimetype = "text/html";
                    if(uri.equals("index.html")) {
                        inputStream = assetManager.open("web/index.html");
                    }
                    else if(uri.equals("formularios.html")) {
                        inputStream = assetManager.open("web/formularios.html");
                    }
                    else if(uri.equals("galeria.html")){
                        inputStream = assetManager.open("web/galeria.html");
                    }
                    //AÑADIR AQUI NUEVOS HTML's
                }
                else{
                    inputStream = assetManager.open("web/"+uri);
                    if(uri.contains(".js")){
                        mimetype = "text/javascript";
                    }
                    else if(uri.contains(".css")){
                        mimetype = "text/css";
                    }
                    else if(uri.contains(".gif")){
                        mimetype = "text/gif";
                    }
                    else if(uri.contains(".jpeg") || uri.contains(".jpg")){
                        mimetype = "text/jpeg";
                    }
                    else if(uri.contains(".png")){
                        mimetype = "image/png";
                    }
                }
            }

            if (inputStream == null)
                throw new IOException();

            return newFixedLengthResponse(Response.Status.OK, mimetype, inputStream, inputStream.available());
        }catch (IOException e){
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, mimetype, "");
        }
    }
}
