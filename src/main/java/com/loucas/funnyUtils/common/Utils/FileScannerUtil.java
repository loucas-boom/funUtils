package com.loucas.funnyUtils.common.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScannerUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileScannerUtil.class);

    private static final String[] VIDEO_EXTENSIONS = {
        ".mp4", ".avi", ".mkv", ".mov", ".flv", ".wmv", ".mpeg", ".mpg"
    };

    public static List<VideoFile> scanForVideoFiles(String directoryPath) {
        List<VideoFile> videoFiles = new ArrayList<>();
        File rootDir = new File(directoryPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            logger.warn("无效目录路径: {}", directoryPath);
            throw new IllegalArgumentException("提供的路径不是一个有效的目录: " + directoryPath);
        }

        logger.info("开始扫描目录: {}", directoryPath);
        walkDirectory(rootDir, videoFiles);
        logger.info("共找到 {} 个视频文件", videoFiles.size());

        return videoFiles;
    }

    private static void walkDirectory(File dir, List<VideoFile> videoFiles) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    walkDirectory(file, videoFiles);
                } else {
                    if (isVideoFile(file.getName())) {
                        videoFiles.add(new VideoFile(file.getName(), file.getAbsolutePath()));
                    }
                }
            }
        }
    }

    private static boolean isVideoFile(String filename) {
        for (String ext : VIDEO_EXTENSIONS) {
            if (filename.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static class VideoFile {
        private String name;
        private String path;

        public VideoFile(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "VideoFile{" +
                   "name='" + name + '\'' +
                   ", path='" + path + '\'' +
                   '}';
        }
    }
}
