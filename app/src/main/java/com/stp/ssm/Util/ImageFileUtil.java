package com.stp.ssm.Util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.graphics.Bitmap.CompressFormat;
import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.graphics.Bitmap.Config;
import static android.graphics.Bitmap.Config.RGB_565;
import static android.graphics.BitmapFactory.Options;
import static android.graphics.BitmapFactory.decodeFile;
import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;
import static android.util.Log.i;
import static java.lang.System.currentTimeMillis;
import static java.security.MessageDigest.getInstance;


public class ImageFileUtil {

    public static final String PATH_GALLERY_SCAN = "/DCIM/ssm_gallery_scan/";
    public static final String PATH_GALLERY = "/DCIM/ssm_gallery/";
    private static final String IMG_EXT = ".jpg";


    public static String getStringImage(String imagenpath) {

        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = RGB_565;
        options.inDither = true;


        Bitmap bitmap = decodeFile(imagenpath, options);
        i("Largo", bitmap.getWidth() + "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap.getWidth() > 1280) {
            bitmap.compress(JPEG, 50, baos);
        } else {
            bitmap.compress(JPEG, 90, baos);
        }

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = encodeToString(imageBytes, DEFAULT);
        return encodedImage;
    }

    public static String encodeFileToBase64Binary(String fileName) {
        File file = new File(fileName);
        byte[] bytes = fileToByte(file);
        String encodedString = encodeToString(bytes, DEFAULT);
        return encodedString;
    }


    private static byte[] fileToByte(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            return bFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean fileExist(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }


    public static void comprimir(String[] files, String zipFile) {
        try {
            BufferedInputStream origin = null;
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            byte data[] = new byte[1024];
            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, 1024);
                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
            origin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String bitmapToFile(Bitmap bitmap, String cedula, int nro) {
        File folder = new File(getExternalStorageDirectory() + PATH_GALLERY_SCAN);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File imagen = new File(folder, currentTimeMillis() + "SCAN" + IMG_EXT);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(imagen);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return imagen.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNameImagen(String path) {
        int indice = path.lastIndexOf("/");
        return path.substring(indice + 1);
    }


    public static String copyImagen(String pathimagen) {
        File source = new File(pathimagen);
        File folder = new File(getExternalStorageDirectory() + PATH_GALLERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File destino = new File(folder, currentTimeMillis() + IMG_EXT);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(source).getChannel();
            outChannel = new FileOutputStream(destino).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destino.getAbsolutePath();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(DATA);
            if (cursor.moveToFirst()) {
                String result = cursor.getString(column_index);
                if (result == null) {
                    result = contentUri.getPath();
                    String id = result.split(":")[1];
                    String sel = _ID + "=?";
                    cursor = context.getContentResolver().query(EXTERNAL_CONTENT_URI, proj, sel, new String[]{id}, null);
                    column_index = cursor.getColumnIndex(proj[0]);
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(column_index);
                    }
                }
                return result;
            } else {
                return "";
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String getHashOfFile(File file) {
        try {
            MessageDigest messageDigest = getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }
            fis.close();
            byte[] bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<String> getListOfFile(String path) {
        List<String> arr_files = new ArrayList<>();

        File folder = new File(path);
        if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    arr_files.add(listOfFiles[i].getAbsolutePath());
                }
            }
        }

        return arr_files;
    }

    public static String getDateOfFile(File file) {
        Date lastModDate = new Date(file.lastModified());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(lastModDate);
    }
}