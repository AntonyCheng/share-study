package top.sharehome.ggkt.vod.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import top.sharehome.ggkt.model.vod.Subject;
import top.sharehome.ggkt.vo.vod.SubjectEeVo;
import top.sharehome.ggkt.vod.mapper.SubjectMapper;

import javax.annotation.Resource;

/**
 * EasyExcel监听类
 *
 * @author AntonyCheng
 */
@Component
public class SubjectListener extends AnalysisEventListener<SubjectEeVo> {
    @Resource
    private SubjectMapper subjectMapper;

    @Override
    public void invoke(SubjectEeVo subjectEeVo, AnalysisContext analysisContext) {
        Subject subject = new Subject();
        BeanUtils.copyProperties(subjectEeVo, subject);
        subjectMapper.insert(subject);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
