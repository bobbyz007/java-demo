package com.example.client;

import java.io.File;

public final class Util {
    // 防止本地多个应用共享 .dubbo目录导致问题
    public static void setDubboCacheDir(File appHome) {
        String dirPath = appHome.getParentFile().toString();
        String filePath = dirPath + File.separator + ".dubbo";
        System.setProperty("dubbo.meta.cache.filePath", filePath);
        System.setProperty("dubbo.mapping.cache.filePath",filePath);
    }
}
