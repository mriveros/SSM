package com.stp.ssm.http;

import com.stp.ssm.Model.SendData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static de.greenrobot.event.EventBus.getDefault;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;

public class HttpCliente {

    private URL url;
    private HttpURLConnection connection;
    protected EventBus eventBus;
    private static HttpCliente mInstance;


    public HttpCliente() {
        this.eventBus = getDefault();
    }

    public static HttpCliente getInstance() {
        if (mInstance == null) {
            mInstance = new HttpCliente();
        }
        return mInstance;
    }

    public ApiResponse sendDataPost(String url, SendData sendData, String token) {
        //String respuesta = httpConnetar(sendData.getParametros(),url,0,token);
        //return respuesta;
        return httpConnetar(sendData.getParametros(), url, 0, token);
    }

    public ApiResponse sendDataGet(String url, SendData sendData, String token) {
        //String respuesta = httpConnetar(sendData.getParametros(),url,1,token);
        //return respuesta;
        return httpConnetar(sendData.getParametros(), url, 1, token);
    }

    private ApiResponse httpConnetar(Map<String, String> parametros, String urlWebserver, int metodo, String token) {
        String response = "";
        int code = 0;
        try {
            url = new URL(urlWebserver);
            connection = (HttpURLConnection) url.openConnection();
            switch (metodo) {
                case 0:
                    connection.setRequestMethod("POST");
                    break;
                case 1:
                    connection.setRequestMethod("GET");
                    break;
            }
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(30000);
            String paramString = urlEncodeUTF8(parametros);
            connection.setRequestProperty("Content-Length", "" + Integer.toString(paramString.getBytes().length));
            connection.setRequestProperty("Authorization", "" + token);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(paramString);
            wr.flush();
            wr.close();

            code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                response = parseResponse(is);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse(response, code);
    }

    public ApiResponse httpUpFile(Map<String, String> parametros, String urlWebserver, File file, String token) {
        String response = "";
        int code = 0;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            url = new URL(urlWebserver);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Authorization", "" + token);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file", file.getName());

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            for (Map.Entry<String, String> entry : parametros.entrySet()) {
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                dos.writeBytes("Content-Length: " + entry.getValue().length() + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(entry.getValue());
                dos.writeBytes(lineEnd);
            }
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\"" + file.getName() + "\"" + lineEnd);
            dos.writeBytes("Content-Length: " + file.length() + lineEnd);
            dos.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 3072;
            int bufferSize = min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            dos.flush();

            code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                response = parseResponse(is);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse(response, code);
    }


    private String urlEncodeUTF8(Map<String, String> parametros) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : parametros.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                sb.append(format("%s=%s",
                        encode(entry.getKey().toString(), "UTF-8"),
                        encode(entry.getValue().toString(), "UTF-8")
                ));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String parseResponse(InputStream in) {
        StringBuilder sb = new StringBuilder();
        ;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}