package top.sharehome.share_study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.sharehome.share_study.mapper.CommentMapper;
import top.sharehome.share_study.model.entity.Comment;
import top.sharehome.share_study.service.CommentService;

/**
 * 评论交流ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
