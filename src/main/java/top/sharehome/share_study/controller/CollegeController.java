package top.sharehome.share_study.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.share_study.service.CollegeService;

import javax.annotation.Resource;

/**
 * 高校相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/college")
@Api(tags = "高校相关接口")
@CrossOrigin
public class CollegeController {
    @Resource
    private CollegeService collegeService;


}
