package com.loucas.funnyUtils.controller;

import com.loucas.funnyUtils.common.CommonResult;
import com.loucas.funnyUtils.service.BBCService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value =  "/bbc")
public class BBCDownloaderController {

    private final BBCService bbcService;

    public BBCDownloaderController(BBCService bbcService) {
        this.bbcService = bbcService;
    }

    @RequestMapping(value = "/download")
    public CommonResult<Object> download() {
        try {
            bbcService.download();
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success("downloadSuccess");
    }
}
