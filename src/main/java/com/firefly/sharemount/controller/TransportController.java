package com.firefly.sharemount.controller;

import com.firefly.sharemount.exception.BadConnectionToStorageException;
import com.firefly.sharemount.exception.WriteToVirtualFolderNotAllowedException;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;

@Api(tags = "文件传输接口")
@RestController
@RequestMapping("/api/file")
public class TransportController {
    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @Resource
    private StorageService storageService;

    @GetMapping("/method")
    public Result<String> getUploadMethod(@RequestParam String path, @RequestParam(required = false) BigInteger root, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        root = root == null ? userId : root;
        FileBO dir = fileService.findFileBO(root, path);
        // TODO 鉴权，无权限返回"none"
        if (dir.getStorage() == null) return Result.success("none");
        if (storageService.isAllowMultipartUpload(dir.getStorage().getId())) return Result.success("multipart");
        return Result.success("direct");
    }

    @PostMapping("/upload/multipart")
    public void uploadMultipart(HttpServletRequest request, HttpServletResponse response) {

    }

    @PostMapping("/upload/direct")
    public Result<Object> uploadDirectly(MultipartFile file, BigInteger root, String path, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        root = root == null ? userId : root;
        FileBO dir = fileService.findFileBO(root, path);
        // TODO 鉴权
        try {
            fileService.upload(dir, file);
        } catch (WriteToVirtualFolderNotAllowedException e) {
            return Result.error(403, "Write to virtual folder is not allowed.");
        } catch (FileAlreadyExistsException e) {
            return Result.error(404, "File not found.");
        } catch (IOException e) {
            return Result.error(405, "Unknown error during file IO.");
        } catch (BadConnectionToStorageException e) {
            return Result.error(405, "Failed to connect to storage.");
        }
        return Result.success();
    }

    @GetMapping("/download")
    public void download(BigInteger root, String path, Boolean preview, HttpServletRequest request, HttpServletResponse response) {
        BigInteger userId = userService.getUserId(request);
        root = root == null ? userId : root;
        FileBO dir = fileService.findFileBO(root, path);
        // TODO 鉴权
        try {
            if (dir.getStorageRestPath().isEmpty()) throw new FileNotFoundException();
            String filename = dir.getStorageRestPath().peekLast();
            assert filename != null;
            MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
            String mimeType = fileTypeMap.getContentType(filename);
            response.setContentType(mimeType);
            if (!preview) {
                String contentDisposition = request.getHeader("User-Agent").contains("Firefox")
                        ? "attachment; fileName==?UTF-8?B?" + new BASE64Encoder().encode(filename.getBytes(StandardCharsets.UTF_8))
                        : "attachment; fileName=" + URLEncoder.encode(filename, "UTF-8");
                response.setHeader("Content-Disposition", contentDisposition);
            }
            fileService.download(dir, response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (BadConnectionToStorageException | IOException e) {
            response.setStatus(405);
        }
    }
}
