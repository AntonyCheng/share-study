package top.sharehome.share_study.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.share_study.service.ResourceService;

import javax.annotation.Resource;

/**
 * 教学资料相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/resource")
@Api(tags = "教学资料相关接口")
@CrossOrigin
public class ResourceController {
    @Resource
    private ResourceService resourceService;


}
