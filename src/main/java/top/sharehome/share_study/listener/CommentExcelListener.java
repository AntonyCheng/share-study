package top.sharehome.share_study.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.stereotype.Component;
import top.sharehome.share_study.mapper.CommentMapper;
import top.sharehome.share_study.model.entity.Comment;

import javax.annotation.Resource;

/**
 * EasyExcel Comment 监听类
 *
 * @author AntonyCheng
 */
@Component
public class CommentExcelListener extends AnalysisEventListener<Comment> {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public void invoke(Comment comment, AnalysisContext analysisContext) {
        commentMapper.insert(comment);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
