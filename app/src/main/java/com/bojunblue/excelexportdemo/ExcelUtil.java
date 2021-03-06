package com.bojunblue.excelexportdemo;

import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Excel导出工具
 */
public class ExcelUtil {

    private static final String TAG = ExcelUtil.class.getSimpleName();

    //表格式
    private static WritableCellFormat writableCellFormat = null;
    //字体的点大小
    private final static int pointSize = 12;
    //编码
    private final static String ENCODING_UTF8 = "UTF-8";
    //数据写入监听
    private static OnCallBackListener listener = null;

    public interface OnCallBackListener {
        void onResult(boolean isSucceed, String filePath);
    }

    /**
     * 初始化Excel格式
     */
    private static void initFormat() {
        try {
            WritableFont writableFont = new WritableFont(WritableFont.ARIAL, pointSize);
            writableCellFormat = new WritableCellFormat(writableFont);
            writableCellFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel文件并创建一张表
     *
     * @param sheetIndex 表索引（位置）
     * @param filePath   文件路径
     * @param fileName   文件名
     * @param sheetName  sheet名
     * @param colNames   各列的名字
     */
    private static void initExcel(int sheetIndex, String filePath, String fileName, String sheetName, String[] colNames) {
        initFormat();
        WritableWorkbook writebook = null;
        try {
            File file = new File(filePath, fileName);
            boolean isSucceed = true;
            if (!file.exists()) {
                isSucceed = file.createNewFile();
            }
            if (isSucceed) {
                LogUtils.d(TAG, "文件创建成功！");
            } else {
                LogUtils.e(TAG, "文件创建失败！");
            }
            if (isSucceed) {
                writebook = Workbook.createWorkbook(file);
                WritableSheet sheet = writebook.createSheet(sheetName, sheetIndex);
                sheet.addCell(new Label(0, 0, filePath, writableCellFormat));
                for (int col = 0; col < colNames.length; col++) {
                    sheet.addCell(new Label(col, 0, colNames[col], writableCellFormat));
                }
                writebook.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writebook != null) {
                try {
                    writebook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 插入表到Excel
     *
     * @param sheetIndex 表索引（位置）
     * @param filePath   文件路径
     * @param filePath   文件名
     * @param sheetName  sheet名
     * @param colNames   各列的名字
     */
    private static void insertSheetToExcel(int sheetIndex, String filePath, String fileName, String sheetName, String[] colNames) {
        if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(fileName)
                && !TextUtils.isEmpty(sheetName) && colNames != null && colNames.length > 0) {
            WritableWorkbook writebook = null;
            try {
                File file = new File(filePath, fileName);
                if (file.exists()) {
                    Workbook workbook = Workbook.getWorkbook(file);
                    writebook = Workbook.createWorkbook(file, workbook);
                    WritableSheet sheet = writebook.createSheet(sheetName, sheetIndex);
                    sheet.addCell(new Label(0, 0, filePath, writableCellFormat));
                    for (int col = 0; col < colNames.length; col++) {
                        sheet.addCell(new Label(col, 0, colNames[col], writableCellFormat));
                    }
                    writebook.write();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 将数据写入Excel表格
     *
     * @param mActivity  上下文
     * @param sheetIndex 表索引（位置）
     * @param filePath   文件路径
     * @param objList    要写的列表数据
     * @param <T>
     */
    private static <T> void writeObjListToExcel(AppCompatActivity mActivity, int sheetIndex, final String filePath, List<T> objList) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(ENCODING_UTF8);
                in = new FileInputStream(new File(filePath));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(filePath), workbook, setEncode);
                WritableSheet sheet = writebook.getSheet(sheetIndex);
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = (ArrayList<String>) objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), writableCellFormat));
                    }
                }
                writebook.write();
                if (mActivity != null && listener != null) {
                    mActivity.runOnUiThread(() -> listener.onResult(true, filePath));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mActivity != null && listener != null) {
                    mActivity.runOnUiThread(() -> listener.onResult(false, filePath));
                }
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (mActivity != null && listener != null) {
                mActivity.runOnUiThread(() -> listener.onResult(false, filePath));
            }
        }
    }

    /**
     * 将数据写入excel表格
     */
    public static void initExcel(AppCompatActivity mActivity, int sheetIndex, String excelFilePath
            , String excelFileName, String sheetName, String[] colNames) {
        if (mActivity != null && !TextUtils.isEmpty(excelFilePath) && !TextUtils.isEmpty(excelFileName)
                && !TextUtils.isEmpty(sheetName) && colNames != null && colNames.length > 0) {
            File excelFolder = new File(excelFilePath);
            File file = new File(excelFilePath, excelFileName);
            if (!excelFolder.exists()) {
                boolean isSucceed = excelFolder.mkdirs();
                if (isSucceed) {
                    LogUtils.d(TAG, "文件夹创建成功！");
                } else {
                    LogUtils.e(TAG, "文件夹创建失败！");
                }
            }
            if (!file.exists()) {
                initExcel(sheetIndex, excelFilePath, excelFileName, sheetName, colNames);
            } else {
                insertSheetToExcel(sheetIndex, excelFilePath, excelFileName, sheetName, colNames);
            }
        }
    }

    /**
     * 将数据写入excel表格
     */
    public static void writeExcel(AppCompatActivity mActivity, int sheetIndex, String excelFilePath, String excelFileName
            , List<List<String>> objList, OnCallBackListener mListener) {
        listener = mListener;
        if (mActivity != null && !TextUtils.isEmpty(excelFilePath) && !TextUtils.isEmpty(excelFileName) && objList != null && objList.size() > 0) {
            String excelFileFullPath = excelFilePath + excelFileName;
            writeObjListToExcel(mActivity, sheetIndex, excelFileFullPath, objList);
        }
    }

}