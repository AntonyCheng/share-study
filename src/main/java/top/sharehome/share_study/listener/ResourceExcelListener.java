package top.sharehome.share_study.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.stereotype.Component;
import top.sharehome.share_study.mapper.ResourceMapper;
import top.sharehome.share_study.model.entity.Resource;


/**
 * EasyExcel Resource 监听类
 *
 * @author AntonyCheng
 */
@Component
public class ResourceExcelListener extends AnalysisEventListener<Resource> {
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @Override
    public void invoke(Resource resource, AnalysisContext analysisContext) {
        resourceMapper.insert(resource);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
