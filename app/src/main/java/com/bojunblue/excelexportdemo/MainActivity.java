package com.bojunblue.excelexportdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity {

    //权限
    private static final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private final String excelFilePath = FileUtil.getFilePath();
    private final String excelFileName = "Excel导出测试.xlsx";
    private final String sheetName1 = "表1";
    private final String sheetName2 = "表2";
    private final String[] colNames = new String[]{"ID", "姓名", "性别", "民族", "生日", "国籍"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_export).setOnClickListener(v -> {
            requestPermissions(2);
        });

        requestPermissions(1);
    }

    /**
     * 获取权限
     */
    private void requestPermissions(int type) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(permissions).subscribe(new io.reactivex.Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean isHasPermission) {
                if (!isHasPermission) {
                    ToastUtils.showLong(R.string.string_permissions_open_tips);
                } else {
                    if (type == 1) {
                        new Thread(() -> {
                            ExcelUtil.initExcel(MainActivity.this, 0, excelFilePath, excelFileName, sheetName1, colNames);
                            ExcelUtil.initExcel(MainActivity.this, 1, excelFilePath, excelFileName, sheetName2, colNames);
                        }).start();
                    } else if (type == 2) {
                        new Thread(() -> {
                            ExcelUtil.writeExcel(MainActivity.this, 0, excelFilePath, excelFileName, getTestData(), (isSucceed, filePath) -> {
                                if (isSucceed) {
                                    ToastUtils.showLong("导出成功，文件路径：" + filePath);
                                } else {
                                    ToastUtils.showShort("导出失败！");
                                }
                            });
                            ExcelUtil.writeExcel(MainActivity.this, 1, excelFilePath, excelFileName, getTestData(), (isSucceed, filePath) -> {
                                if (isSucceed) {
                                    ToastUtils.showLong("导出成功，文件路径：" + filePath);
                                } else {
                                    ToastUtils.showShort("导出失败！");
                                }
                            });
                        }).start();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 测试数据
     *
     * @return
     */
    private List<List<String>> getTestData() {
        String s = "data_";
        List<List<String>> parentLists = new ArrayList<>();
        List<String> lists;
        for (int i = 0; i < 5; i++) {
            lists = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                lists.add(s + j);
            }
            parentLists.add(lists);
        }
        return parentLists;
    }
}