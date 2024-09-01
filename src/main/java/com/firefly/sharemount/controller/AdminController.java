package com.firefly.sharemount.controller;

import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.pojo.data.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @Resource
    private ApplicationConfiguration applicationConfiguration;

    @PutMapping("/allocated/{newAllocation}")
    public Result<Object> updateAllocation(@PathVariable Integer newAllocation) {
        applicationConfiguration.loadConfig();
        applicationConfiguration.updateNestedConfig("cloud-drive.default-allocation",newAllocation);
        return Result.success();
    }

}
