package com.firefly.sharemount.controller;

import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.pojo.data.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @PutMapping("/allocated")
    public Result<Object> updateAllocation(@RequestParam Integer newAllocation) {
        applicationConfiguration.loadConfig();
        applicationConfiguration.updateNestedConfig("cloud-drive.default-allocation",newAllocation);
        return Result.success();
    }

}
