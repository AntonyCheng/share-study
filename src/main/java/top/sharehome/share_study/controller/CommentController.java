package top.sharehome.share_study.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.share_study.service.CommentService;

import javax.annotation.Resource;

/**
 * 评论交流相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论交流相关接口")
@CrossOrigin
public class CommentController {
    @Resource
    private CommentService commentService;


}
