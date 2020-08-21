package com.zzti.fengyongge.imagepicker.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public final class FileUtils
{
    public static String SDCARD_PAHT ;// SD卡路径
    public static String LOCAL_PATH ;// 本地路径,即/data/data/目录下的程序私有目录,emulate/0
    public static String CURRENT_PATH = "";// 当前的路径,如果有SD卡的时候当前路径为SD卡，如果没有的话则为程序的私有目录

    static
    {
        init();
    }

    public static void init()
    {
        SDCARD_PAHT = Environment.getExternalStorageDirectory().getPath();// SD卡路径
        LOCAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();// 本地路径,即/data/data/目录下的程序私有目录

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            CURRENT_PATH = SDCARD_PAHT;
        }
        else
        {
            CURRENT_PATH = LOCAL_PATH;
        }
    }
    /**
     * 得到与当前存储路径相反的路径(当前为/data/data目录，则返回/sdcard目录;当前为/sdcard，则返回/data/data目录)
     * @return
     */
    public static String getDiffPath()
    {
        if(CURRENT_PATH.equals(SDCARD_PAHT))
        {
            return LOCAL_PATH;
        }
        return SDCARD_PAHT;
    }


    public static String getDiffPath(String pathIn)
    {
        return pathIn.replace(CURRENT_PATH, getDiffPath());
    }



    /**
     * 将数据写入一个文件
     *
     * @param destFilePath
     *            要创建的文件的路径
     * @param data
     *            待写入的文件数据
     * @param startPos
     *            起始偏移量
     * @param length
     *            要写入的数据长度
     * @return 成功写入文件返回true,失败返回false
     */
    public static boolean writeFile(String destFilePath, byte[] data, int startPos, int length)
    {
        try
        {
            if (!createFile(destFilePath))
            {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(destFilePath);
            fos.write(data, startPos, length);
            fos.flush();
            if (null != fos)
            {
                fos.close();
                fos = null;
            }
            return true;

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从一个输入流里写文件
     *
     * @param destFilePath
     *            要创建的文件的路径
     * @param in
     *            要读取的输入流
     * @return 写入成功返回true,写入失败返回false
     */
    public static boolean writeFile(String destFilePath, InputStream in)
    {
        try
        {
            if (!createFile(destFilePath))
            {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(destFilePath);
            int readCount = 0;
            int len = 1024;
            byte[] buffer = new byte[len];
            while ((readCount = in.read(buffer)) != -1)
            {
                fos.write(buffer, 0, readCount);
            }
            fos.flush();
            if (null != fos)
            {
                fos.close();
                fos = null;
            }
            if (null != in)
            {
                in.close();
                in = null;
            }
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean appendFile(String filename,byte[]data,int datapos,int datalength)
    {
        try {

            createFile(filename);

            RandomAccessFile rf= new RandomAccessFile(filename, "rw");
            rf.seek(rf.length());
            rf.write(data, datapos, datalength);
            if(rf!=null)
            {
                rf.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 读取文件，返回以byte数组形式的数据
     *
     * @param filePath
     *            要读取的文件路径名
     * @return
     */
    public static byte[] readFile(String filePath)
    {
        try
        {
            if (isFileExist(filePath))
            {
                FileInputStream fi = new FileInputStream(filePath);
                return readInputStream(fi);
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从一个数量流里读取数据,返回以byte数组形式的数据。
     * </br></br>
     * 需要注意的是，如果这个方法用在从本地文件读取数据时，一般不会遇到问题，但如果是用于网络操作，就经常会遇到一些麻烦(available()方法的问题)。所以如果是网络流不应该使用这个方法。
     * @param in
     *            要读取的输入流
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream in)
    {
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] b = new byte[in.available()];
            int length = 0;
            while ((length = in.read(b)) != -1)
            {
                os.write(b, 0, length);
            }

            b = os.toByteArray();

            in.close();
            in = null;

            os.close();
            os = null;

            return b;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取网络流
     * @param in
     * @return
     */
    public static byte[] readNetWorkInputStream(InputStream in)
    {
        ByteArrayOutputStream os=null;
        try
        {
            os = new ByteArrayOutputStream();

            int readCount = 0;
            int len = 1024;
            byte[] buffer = new byte[len];
            while ((readCount = in.read(buffer)) != -1)
            {
                os.write(buffer, 0, readCount);
            }

            in.close();
            in = null;

            return os.toByteArray();

        } catch (IOException e)
        {
            e.printStackTrace();
        }finally{
            if(null!=os)
            {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                os = null;
            }
        }
        return null;
    }

    /**
     * 将一个文件拷贝到另外一个地方
     * @param sourceFile 源文件地址
     * @param destFile 目的地址
     * @param shouldOverlay 是否覆盖
     * @return
     */
    public static boolean copyFiles(String sourceFile, String destFile,boolean shouldOverlay)
    {
        try
        {
            if(shouldOverlay)
            {
                deleteFile(destFile);
            }
            FileInputStream fi = new FileInputStream(sourceFile);
            writeFile(destFile, fi);
            return true;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     *            路径名
     * @return
     */
    public static boolean isFileExist(String filePath)
    {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 创建一个文件，创建成功返回true
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath)
    {
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                if (!file.getParentFile().exists())
                {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }



    /**
     * 删除 directoryPath目录下的所有文件，包括删除删除文件夹
     * @param dir
     */
    public static void deleteDirectory(File dir)
    {
        if (dir.isDirectory())
        {
            File[] listFiles = dir.listFiles();
            for (int i = 0; i < listFiles.length ; i++)
            {
                deleteDirectory(listFiles[i]);
            }
        }
        dir.delete();
    }

    /**
     * 字符串转流
     * @param str
     * @return
     */
    public static InputStream String2InputStream(String str)
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }

    /**
     * 流转字符串
     * @param is
     * @return
     */
    public static String inputStream2String(InputStream is)
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";

        try
        {
            while ((line = in.readLine()) != null)
            {
                buffer.append(line);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return buffer.toString();
    }



    //-------------------------------------------------------------------------------------------------

    /**
     * 删除文件路径
     * @param sPath
     */
    public static void DeleteFolder(String sPath) {
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (file.exists()) { // 不存在返回 false
            // 判断是否为文件
            if (file.isFile()) { // 为文件时调用删除文件方法
                deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                deleteDirectory(sPath);
            }
        }
    }
    /**
     * 删除单个文件
     *
     * @param sPath
     *            被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        File file = new File(sPath);
        Log.i("fyg","sPath:"+sPath);

        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return false;
    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath
     *            被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static void deleteDirectory(String sPath) {
        File dirFile = new File(sPath);
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath());
            } // 删除子目录
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        // 删除当前目录
        if (dirFile.delete()) {
        } else {
        }
    }




    /**
     * 给文件路径写入图片
     * @param bitmap
     * @param destPath
     * @param quality
     */
    public static void writeImage(Bitmap bitmap,String destPath,int quality)
    {
        try {
            FileUtils.deleteFile(destPath);
            if (FileUtils.createFile(destPath))
            {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG,quality, out))
                {
                    out.flush();
                    out.close();
                    out = null;
                }

                bitmap.recycle();
                bitmap=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
