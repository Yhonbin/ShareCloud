package com.firefly.sharemount.controller;

import com.firefly.sharemount.exception.FileAlreadyExistsException;
import com.firefly.sharemount.exception.FileNotExistsException;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.dto.MkdirRequestDTO;
import com.firefly.sharemount.pojo.dto.ListFilesResponseDTO;
import com.firefly.sharemount.pojo.dto.SingleFileRequestDTO;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;

@Api(tags = "文件管理接口")
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @GetMapping("/ls")
    public Result<ListFilesResponseDTO> listFiles(@RequestBody SingleFileRequestDTO file, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        if (file.getRoot() == null) file.setRoot(userId);
        FileBO dir = fileService.findFileBO(file.getRoot(), file.getPath());
        // TODO 鉴权
        ListFilesResponseDTO ret = new ListFilesResponseDTO();
        System.out.printf("[%s] File operation: ls %s%n", new Date(), dir.toString());
        ret.setDir(fileService.getStat(dir));
        ret.setChildren(fileService.listDir(dir, dir.getStorage() == null ? null : dir.getStorage().getId()));
        return Result.success(ret);
    }

    @PostMapping("/mkdir")
    public Result<Object> makeDir(@RequestBody MkdirRequestDTO dirPath, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        if (dirPath.getRoot() == null) dirPath.setRoot(userId);
        FileBO dir = fileService.findFileBO(dirPath.getRoot(),dirPath.getPath());
        // TODO 鉴权
        System.out.printf("[%s] File operation: mkdir %s%n", new Date(), dir.toString());
        try {
            fileService.mkdir(dir, dirPath.getVirtual());
        } catch (FileAlreadyExistsException e) {
            return Result.error(403, "File already exists");
        }
        return Result.success();
    }

    @DeleteMapping
    public Result<Object> delete(@RequestBody SingleFileRequestDTO fileDto, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        if (fileDto.getRoot() == null) fileDto.setRoot(userId);
        FileBO file = fileService.findFileBO(fileDto.getRoot(), fileDto.getPath());
        // TODO 鉴权
        System.out.printf("[%s] File operation: rm %s%n", new Date(), file.toString());
        try {
            fileService.delete(file);
        } catch (FileNotExistsException e) {
            return Result.error(404, "File not exists");
        }
        return Result.success();
    }
}
