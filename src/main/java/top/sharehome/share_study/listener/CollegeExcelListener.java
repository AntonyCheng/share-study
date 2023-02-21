package top.sharehome.share_study.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.model.entity.College;

import javax.annotation.Resource;
import java.awt.*;

/**
 * EasyExcel监听类
 *
 * @author AntonyCheng
 */
@Component
public class CollegeExcelListener extends AnalysisEventListener<College> {
    @Resource
    private CollegeMapper collegeMapper;

    @Override
    public void invoke(College college, AnalysisContext analysisContext) {
        college.setId(RandomUtils.nextLong());
        collegeMapper.insert(college);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
