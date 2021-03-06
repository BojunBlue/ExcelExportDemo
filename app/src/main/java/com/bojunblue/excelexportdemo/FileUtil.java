package com.bojunblue.excelexportdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * App公司标识
     */
    public static String APP_COMPANY_TAG = "Alpha";
    /**
     * 文件的存储路径（外部）
     */
    public static final String SAVE_FILE_PATH_EXTERNAL = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_COMPANY_TAG + File.separator;
    /**
     * 文件的存储路径（内部）
     */
    public static final String SAVE_FILE_PATH_INTERNAL = MyApp.getInstance().getCacheDir() + File.separator + APP_COMPANY_TAG + File.separator;
    /**
     * 缓存文件夹
     */
    public static final String CACHE_PATH = "cache" + File.separator;
    /**
     * 文件的存储文件夹
     */
    public static final String FILE_PATH = "file" + File.separator;
    /**
     * apk的存储路径
     */
    public static final String APK_PATH = "apk" + File.separator;
    /**
     * 图片的存储路径
     */
    public static final String IMAGE_PATH = "image" + File.separator;
    /**
     * 视频的存储路径
     */
    public static final String VIDEO_PATH = "video" + File.separator;
    /**
     * 日志的存储路径
     */
    public static final String LOG_PATH = "log" + File.separator;

    /**
     * 获取文件格式名
     */
    public static String getFileFormat(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            // 去掉首尾的空格
            fileName = fileName.trim();
            String s[] = fileName.split("\\.");
            if (s.length >= 2) {
                return s[s.length - 1];
            }
        }
        return "";
    }

    /**
     * 获取UUID字符串
     */
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断SD卡的状态是否可用
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getCachePath() {
        if (checkSDCardAvailable()) {
            LogUtils.d(TAG, "checkSDCardAvailable：" + true);
            return SAVE_FILE_PATH_EXTERNAL + CACHE_PATH;
        } else {
            LogUtils.d(TAG, "checkSDCardAvailable：" + false);
            return SAVE_FILE_PATH_INTERNAL + CACHE_PATH;
        }
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String getFilePath() {
        return getCachePath() + FILE_PATH;
    }

    /**
     * 获取Apk路径
     *
     * @return
     */
    public static String getApkPath() {
        return getCachePath() + APK_PATH;
    }

    /**
     * 获取图片路径
     *
     * @return
     */
    public static String getImagePath() {
        return getCachePath() + IMAGE_PATH;
    }

    /**
     * 获取视频路径
     *
     * @return
     */
    public static String getVideoPath() {
        return getCachePath() + VIDEO_PATH;
    }

    /**
     * 获取Log路径
     *
     * @return
     */
    public static String getLogPath() {
        return getCachePath() + LOG_PATH;
    }

    /**
     * view完全没有显示在界面上，通过inflate 转化的view
     *
     * @param v
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createBitmap(View v, int width, int height) {
        // 测量使得view指定大小
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        // 调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param imageName
     * @return
     */
    public static String saveBitmapImage(Bitmap bitmap, String imageName) {
        try {
            String fileAbsolutePath = getImagePath() + imageName;
            File file = new File(fileAbsolutePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            if (bitmap.isRecycled()) return null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存File到文件
     *
     * @param mFile
     * @param imageName
     * @return
     */
    public static String saveFileImage(File mFile, String imageName) {
        try {
            String imagePath = getImagePath();
            if (!TextUtils.isEmpty(imagePath)) {
                LogUtils.d(TAG, "imagePath：" + imagePath);
                File appDir = new File(imagePath);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                File file = new File(appDir, imageName);
                if (copyFile(mFile, file)) {
                    return file.getAbsolutePath();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(File srcFile, File destFile) {
        return copyOrMoveFile(srcFile, destFile, false);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(File srcFile, File destFile, boolean isMove) {
        if (srcFile == null || destFile == null) return false;
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile()) return false;
        // 目标文件存在且是文件则返回false
        if (destFile.exists() && destFile.isFile()) return false;
        // 目标目录不存在返回false
        if (!createOrExistsDir(destFile.getParentFile())) return false;
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        if (file == null || is == null) return false;
        if (!createOrExistsFile(file)) return false;
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte data[] = new byte[1024];
            int len;
            while ((len = is.read(data, 0, 1024)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeIO(is, os);
        }
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
