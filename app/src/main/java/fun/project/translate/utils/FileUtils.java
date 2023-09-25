package fun.project.translate.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileUtils {
    public static byte[] readApkAssetsFile(String apkPath,String assetPath){
        try {
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(apkPath)));
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().startsWith("assets/") && entry.getName().replace("assets/","").equals(assetPath)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                    return byteArrayOutputStream.toByteArray();
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public static void writeToFile(String File, String FileContent) {
        try {
            File parent = new File(File).getParentFile();
            if (!parent.exists()) parent.mkdirs();
            FileOutputStream fOut = new FileOutputStream(File);
            fOut.write(FileContent.getBytes(StandardCharsets.UTF_8));
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.d("FileUtils", "writeToFile: ", e);
        }
    }
    public static void compareDirectories(File dir1, File dir2) {
        if (!dir1.isDirectory() || !dir2.isDirectory()) {
            throw new IllegalArgumentException("Both parameters must be directories");
        }
        File[] files1 = dir1.listFiles();
        File[] files2 = dir2.listFiles();
        if (files1.length != files2.length) {
            throw new RuntimeException("文件数量校验失败");
        }
        for (int i = 0; i < files1.length; i++) {
            if (files1[i].isDirectory() && files2[i].isDirectory()) {
                compareDirectories(files1[i], files2[i]);
            } else if (files1[i].length() != files2[i].length()) {
                throw new RuntimeException("文件大小校验失败");
            }
        }
    }
    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source is not a directory");
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(targetDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
    public static String readFileStringMaxSize(String path, int size) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            int totalBytesRead = 0;

            while ((line = reader.readLine()) != null) {
                byte[] lineBytes = line.getBytes();
                totalBytesRead += lineBytes.length;
                if (totalBytesRead <= size) {
                    stringBuilder.append(line).append("\n");
                } else {
                    break;
                }
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                if(children==null)return 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                long size = file.length();
                return size;
            }
        } else {
            return 0;
        }
    }

    public static void writeToFile(String File, byte[] FileContent) {
        try {
            if (FileContent == null) return;
            File parent = new File(File).getParentFile();
            if (!parent.exists()) parent.mkdirs();
            FileOutputStream fOut = new FileOutputStream(File);
            fOut.write(FileContent);
            fOut.close();
        } catch (Exception e) { }
    }

    public static String readFileString(File f) {
        try {
            FileInputStream fInp = new FileInputStream(f);
            String Content = new String(DataUtils.readAllBytes(fInp), StandardCharsets.UTF_8);
            fInp.close();
            return Content;
        } catch (Exception e) {
            return null;
        }
    }
    public static boolean isPathWriteable(String path){
        File file = new File(path);
        if (file.isFile()){
            return false;
        }else if (!file.exists()){
            return file.mkdirs();
        }
        File testFile = new File(file,".write.checker");
        if (testFile.exists()){
            return testFile.delete();
        }
        try {
            if (!testFile.createNewFile())return false;
            return testFile.delete();
        } catch (IOException e) {
            return false;
        }
    }

    public static byte[] readFile(File f) {
        try {
            FileInputStream fInp = new FileInputStream(f);
            byte[] Content = DataUtils.readAllBytes(fInp);
            fInp.close();
            return Content;
        } catch (Exception e) {
            return null;
        }
    }

    public static String readFileString(String f) {
        return readFileString(new File(f));
    }

    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()){
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (files == null) return;
        //遍历该目录下的文件对象
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    public static void copy(String source, String dest) {

        try {
            if (!new File(source).exists()){
                return;
            }

            File f = new File(dest);
            f = f.getParentFile();
            if (!f.exists()) f.mkdirs();

            File aaa = new File(dest);
            if (aaa.exists()) aaa.delete();

            InputStream in = Files.newInputStream(new File(source).toPath());
            OutputStream out = Files.newOutputStream(new File(dest).toPath());
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception ignored) {
        }
    }
}
