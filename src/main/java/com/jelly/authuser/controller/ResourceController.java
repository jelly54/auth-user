package com.jelly.authuser.controller;

import com.jelly.authuser.entity.AuthResource;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.service.AuthResourceService;
import com.jelly.authuser.util.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guodongzhang
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private AuthResourceService resourceService;


    /**
     * "增加API"
     */
    @PostMapping("")
    public CommonResponse createResource(@RequestParam(value = "res_name") String resName,
                                         @RequestParam(value = "res_type") Integer resType,
                                         @RequestParam(value = "parent_id") Integer parentId,
                                         @RequestParam(value = "url", defaultValue = "") String url,
                                         @RequestParam(value = "method", defaultValue = "") String method) {

        LOG.info("Resource resource will be created. resName:{}, resType:{}, parentId:{}, url:{}, method:{}.",
                resName, resType, parentId, url, method);

        boolean flag = resourceService.createResource(resName, resType, parentId, url, method);

        if (!flag) {
            LOG.warn("Create resource failed. resName:{}, resType:{},parentId:{}, url:{}, method:{}.", resName, resType,
                    parentId, url, method);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Create resource failed!").build();
        }

        AuthResource resource = resourceService.getByResourceName(resName);

        LOG.info("Resource resource create successfully. resName:{}, resType:{}, parentId:{}, url:{}, method:{}.",
                resName, resType, parentId, url, method);
        return CommonResponse.ok().data(resource).build();
    }

    /**
     * "修改API"
     */
    @PatchMapping("")
    public CommonResponse updateResource(@RequestParam(value = "id") Integer id,
                                         @RequestParam(value = "res_name", defaultValue = "") String resName,
                                         @RequestParam(value = "res_type", defaultValue = "") Integer resType,
                                         @RequestParam(value = "parent_id", defaultValue = "") Integer parentId,
                                         @RequestParam(value = "url", defaultValue = "") String url,
                                         @RequestParam(value = "method", defaultValue = "") String method,
                                         @RequestParam(value = "status", defaultValue = "") Integer status) {

        LOG.info("Resource resource will be updated. id:{}, resName:{}, resType:{}, parentId:{}, url:{}, method:{}.",
                id, resName, resType, parentId, url, method);

        boolean flag = resourceService.updateByResourceId(id, resName, resType, parentId, url, method);

        if (!flag) {
            LOG.warn("Update resource failed. id:{}, resName:{}, resType:{},parentId:{}, url:{}, method:{}, status:{}.",
                    id, resName, resType, parentId, url, method, status);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "update resource fail").build();
        }

        AuthResource resource = resourceService.getByResourceName(resName);
        LOG.info("Resource api updated successfully. resource:{}.", resource.toString());

        return CommonResponse.ok().data(resource).build();
    }

    /**
     * "删除API 根据API_ID删除API"
     */
    @DeleteMapping("/{resource_id}")
    public CommonResponse deleteResourceByApiId(@PathVariable(value = "resource_id") Integer resourceId) {
        LOG.info("Resource api will be deleted. resourceId:{}.", resourceId);
        boolean flag = resourceService.deleteByResourceId(resourceId);

        if (!flag) {
            LOG.warn("Delete resource failed. resource_id:{}.", resourceId);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "delete api fail").build();
        }

        LOG.info("Resource api deleted successfully. resourceId:{}.", resourceId);
        return CommonResponse.ok().data("delete", true).build();
    }

    /**
     * 查询API
     */
    @GetMapping("/{resource_id}")
    public CommonResponse getResource(@PathVariable(value = "resource_id") Integer resourceId) {
        LOG.info("Get resource. resourceId:{}.", resourceId);

        AuthResource resource = resourceService.getByResourceId(resourceId);

        return CommonResponse.ok(resource).build();
    }

    /**
     * 获取API list 需要分页
     */
    @GetMapping("/{parent_id}/type/{res_type}/{start}/{size}")
    public CommonResponse getResourceList(@PathVariable(value = "parent_id") Integer parentId,
                                          @PathVariable(value = "res_type") Integer resType,
                                          @PathVariable(value = "start") Integer start,
                                          @PathVariable(value = "size") Integer size) {
        LOG.info("Get api resource. parentId:{}. resType:{}. start:{}. size:{}.", parentId, resType, start, size);


        List<AuthResource> resources = resourceService.listByParentId(parentId, resType, start, size);

        return CommonResponse.ok(resources).build();
    }


    //"增加菜单"

    //"删除菜单 根据菜单ID删除菜单

    //"修改菜单"

    //获取用户被授权菜单  通过uid获取对应用户被授权的菜单列表,获取完整菜单树形结构


}
