package com.firefly.sharemount.controller;

import com.alibaba.fastjson.JSONObject;
import com.firefly.sharemount.component.RedisTemplateComponent;
import com.firefly.sharemount.pojo.data.Result;
import com.firefly.sharemount.pojo.dto.StorageDTO;
import com.firefly.sharemount.service.ParticipationService;
import com.firefly.sharemount.service.StorageService;
import com.firefly.sharemount.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;

@Api(tags = "介质管理接口")
@RestController
@RequestMapping("/api/storage")
public class StorageController {


    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    @Resource
    private StorageService storageService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private UserService userService;


    // 添加介质
    @PostMapping("/upload")
    public Result<Object> uploadStorage(@RequestBody StorageDTO storageDto, HttpServletRequest request) {

        BigInteger owner =  userService.getUserId(request);
        storageDto.setOwner(owner);
        storageService.uploadStorage(storageDto);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Object> deleteStorage(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        BigInteger owner = userService.getUserId(request);

        return Result.success();
    }




    @PutMapping("/transfer-group")
    public Result<Object> transferToGroup(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        BigInteger storageId = (BigInteger) jsonObject.get("storageId");
        BigInteger groupId = (BigInteger) jsonObject.get("groupId");
        BigInteger storageOwnerId = storageService.getOwnerById(storageId);
        if (storageOwnerId == null) {
            return Result.error(404, "找不到该存储介质");
        }

        BigInteger owner = userService.getUserId(request);
        Integer srcGroupPrivilege, dstGroupPrivilege;
        if (!userService.isGroup(storageOwnerId)) {
            if (!owner.equals(storageOwnerId)) {
                return Result.error(403, "你无权限转移他人介质");
            }
        } else {
            srcGroupPrivilege = participationService.getPrivilegeById(owner, storageOwnerId);
            if (srcGroupPrivilege == null) {
                return Result.error(404,"您未加入介质的所在用户组，无法转移介质");
            }
            if ((srcGroupPrivilege & 2) == 0) {
                return Result.error(403, "您无权限从源用户组转移介质");
            }
        }
        dstGroupPrivilege = participationService.getPrivilegeById(owner, groupId);
        if (dstGroupPrivilege == null) {
            return Result.error(404,"您未加入目标用户组，无法转移介质");
        }
        if ((dstGroupPrivilege & 2) > 0) {
           storageService.transferToGroup(owner,groupId);
        } else {
            return Result.error(403, "您无权限转移介质到目标用户组");
        }
        return Result.success();
    }

    @PutMapping("/transfer-user")
    public Result<Object> transferToUser(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        BigInteger storageId = (BigInteger) jsonObject.get("storageId");
        BigInteger groupId = (BigInteger) jsonObject.get("groupId");
        BigInteger storageOwnerId = storageService.getOwnerById(storageId);
        if (storageOwnerId == null) {
            return Result.error(404, "找不到该存储介质");
        }
        return Result.success();
    }

}
