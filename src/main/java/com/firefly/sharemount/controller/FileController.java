package com.firefly.sharemount.controller;

import com.firefly.sharemount.component.RedisTemplateComponent;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.dto.CurDirPathRequestDTO;
import com.firefly.sharemount.pojo.dto.FileStatDTO;
import com.firefly.sharemount.pojo.dto.ListFilesResponse;
import com.firefly.sharemount.pojo.dto.SingleFileRequestDTO;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigInteger;
import java.util.Dictionary;

@Api(tags = "文件管理接口")
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @GetMapping("/ls")
    public Result<ListFilesResponse> listFiles(@RequestBody SingleFileRequestDTO file, HttpServletRequest request) {
        HttpSession session = request.getSession();
        BigInteger userId = userService.getUserId(session);
        if (file.getRoot() == null) file.setRoot(userId);
        // 鉴权
        ListFilesResponse ret = new ListFilesResponse();
        FileBO dir = fileService.findFileBO(file.getRoot(), file.getPath());
        ret.setChildren(fileService.listDir(dir));
        ret.setDir(fileService.getStat(dir));
        return Result.success(ret);
    }

    @PostMapping("/mkdir")
    public Result<Object> makeDir(@RequestBody CurDirPathRequestDTO dirPath, HttpServletRequest request) {
        HttpSession session = request.getSession();
        BigInteger userId = userService.getUserId(session);
        if (dirPath.getRoot() == null) dirPath.setRoot(userId);
        FileBO dir = fileService.findFileBO(dirPath.getRoot(),dirPath.getPath());
        fileService.mkdir(dir,dirPath.getVirtual());
        return Result.success();
    }

}
