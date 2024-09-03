package com.firefly.sharemount.controller;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.pojo.data.FileBO;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.dto.*;
import com.firefly.sharemount.service.FileService;
import com.firefly.sharemount.service.ParticipationService;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "介质管理接口")
@RestController
@RequestMapping("/api/storage")
public class StorageController {
    @Resource
    private StorageService storageService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    @GetMapping("/ls")
    public Result<Object> listStorage(@RequestParam("userId") BigInteger userId) {
        List<BigInteger> participated = participationService.getParticipatedGroups(userId)
                .stream()
                .filter(p -> (participationService.getPrivilegeById(userId, p) & 2) > 0)
                .collect(Collectors.toCollection(LinkedList::new));
        participated.add(0, userId);
        StorageListResponseDTO ret = new StorageListResponseDTO(participated.stream().map(id -> {
            StorageListResponseDTO.StorageListOfOneUserDTO dto = new StorageListResponseDTO.StorageListOfOneUserDTO();
            dto.setUser(userService.getUserDTO(id));
            dto.setStorages(storageService.listStorage(id)
                    .stream()
                    .map(s -> storageService.getStorageStat(s))
                    .peek(s -> s.setOwner(null))
                    .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList()));
        return Result.success(ret);
    }

    // 添加介质
    @PostMapping("/upload")
    public Result<Object> uploadStorage(@RequestBody StorageDTO storageDto, HttpServletRequest request) {

        BigInteger owner = userService.getUserId(request);
        storageDto.setOwner(owner);
        storageService.uploadStorage(storageDto);
        return Result.success();
    }

    @DeleteMapping
    public Result<Object> deleteStorage(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        BigInteger owner = jsonObject.getBigInteger("owner");
        BigInteger storageId = jsonObject.getBigInteger("storageId");

        BigInteger userId = userService.getUserId(request);

        owner = owner == null ? userId : owner;
        if (!owner.equals(userId)) {
            Integer privilege = participationService.getPrivilegeById(userId, owner);
            if (privilege == null) return Result.error(404, "您未加入该用户组，无法删除该介质");
            if ((privilege & 2) == 0) return Result.error(403, "您无权限删除该介质");
        }
        storageService.deleteStorage(storageId);


        return Result.success();
    }

    @PostMapping("/mount")
    public Result<Object> makeDir(@RequestBody MountRequestDTO dirPath, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        if (dirPath.getRoot() == null) dirPath.setRoot(userId);
        FileBO dir = fileService.findFileBO(dirPath.getRoot(), dirPath.getPath());
        // TODO 鉴权
        System.out.printf("[%s] Storage operation: mount %s on %s%n", new Date(), dirPath.getStorage(), dir.toString());
        fileService.mountOn(dir, dirPath.getStorage());
        return Result.success();
    }

    @PostMapping("/unmount")
    public Result<Object> makeDir(@RequestBody SingleFileRequestDTO dirPath, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        if (dirPath.getRoot() == null) dirPath.setRoot(userId);
        FileBO dir = fileService.findFileBO(dirPath.getRoot(), dirPath.getPath());
        // TODO 鉴权
        System.out.printf("[%s] Storage operation: unmount on %s%n", new Date(), dir.toString());
        try {
            fileService.unmountOn(dir);
        } catch (Exception e) {
            Result.error(404, "该文件夹没有挂载介质");
        }

        return Result.success();
    }

    @PutMapping("/transfer")
    public Result<Object> transferStorage(@RequestBody StorageTransferDTO transferDTO, HttpServletRequest request) {
        BigInteger userId = userService.getUserId(request);
        BigInteger storageId = transferDTO.getStorageId();
        BigInteger srcId = transferDTO.getSrcId();
        BigInteger dstId = transferDTO.getDstId();
        srcId = srcId == null ? userId : srcId;
        if (srcId.equals(userId)) {
            if (!srcId.equals(storageService.getOwnerById(storageId))) {
                return Result.error(403, "您无权限转移他人介质");
            }
            if (userService.isGroup(dstId)) {
                Integer privilege = participationService.getPrivilegeById(srcId, dstId);
                if (privilege == null) return Result.error(404, "您未加入介质的所在用户组，无法转移介质");
                if ((privilege & 2) == 0) {
                    return Result.error(403, "您无权限转移介质到目标用户组");
                }
            }
        } else {
            Integer dstPrivilege = participationService.getPrivilegeById(userId, dstId);
            if (dstPrivilege == null) return Result.error(404, "您未加入目标用户组，无法转移介质");
            Integer privilege = participationService.getPrivilegeById(userId, srcId);
            if ((privilege & 2) == 0 || (dstPrivilege & 2) == 0) return Result.error(403, "您无权转移介质到目标用户组");
        }
        storageService.transfer(srcId, dstId);
        return Result.success();
    }


}
