package com.loucas.funnyUtils.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loucas.funnyUtils.common.CommonResult;
import com.loucas.funnyUtils.common.Utils.FileScannerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Value("${app.uuid.max-count}")
    private int maxUuidCount;
    @RequestMapping(value = "/getUUID/{num}", method = RequestMethod.GET)
    public CommonResult getUUID(@PathVariable int num) {
        // 参数校验
        if (num < 0) {
            return CommonResult.failed("num 必须为非负整数");
        }
        if (num > maxUuidCount) {
            return CommonResult.failed("每次最多生成 " + maxUuidCount + " 个 UUID");
        }

        List<String> list = new ArrayList<>(num); // 使用泛型并预分配容量
        for (int i = 0; i < num; i++) {
            list.add(java.util.UUID.randomUUID().toString());
        }
        return CommonResult.success(list);
    }

    @GetMapping("/video-files")
    public CommonResult<List<FileScannerUtil.VideoFile>> listVideoFiles(
            @RequestParam String path,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        logger.info("收到请求 - 扫描视频文件，路径: {}, 分页: {}/{}", path, pageNum, pageSize);

        try {
            List<FileScannerUtil.VideoFile> allVideos = FileScannerUtil.scanForVideoFiles(path);

            // 开启分页
            PageHelper.startPage(pageNum, pageSize);
            List<FileScannerUtil.VideoFile> pagedVideos = allVideos.subList(
                    Math.min((pageNum - 1) * pageSize, allVideos.size()),
                    Math.min(pageNum * pageSize, allVideos.size())
            );

            PageInfo<FileScannerUtil.VideoFile> pageInfo = new PageInfo<>(pagedVideos);
            logger.info("成功返回第 {} 页，共 {} 条结果", pageNum, pagedVideos.size());

            return CommonResult.success(pagedVideos);
        } catch (Exception e) {
            logger.error("扫描视频文件失败", e);
            return CommonResult.failed("扫描失败: " + e.getMessage());
        }
    }
}
