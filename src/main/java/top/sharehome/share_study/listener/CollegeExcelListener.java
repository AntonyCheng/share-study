package top.sharehome.share_study.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.stereotype.Component;
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.model.entity.College;

import javax.annotation.Resource;

/**
 * EasyExcel College 监听类
 *
 * @author AntonyCheng
 */
@Component
public class CollegeExcelListener extends AnalysisEventListener<College> {
    @Resource
    private CollegeMapper collegeMapper;

    @Override
    public void invoke(College college, AnalysisContext analysisContext) {
        collegeMapper.insert(college);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
