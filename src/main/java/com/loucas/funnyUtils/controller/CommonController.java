package com.loucas.funnyUtils.controller;

import com.loucas.funnyUtils.common.CommonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Value("${app.uuid.max-count}")
    private int maxUuidCount;
    @RequestMapping("/getUUID/{num}")
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

}
