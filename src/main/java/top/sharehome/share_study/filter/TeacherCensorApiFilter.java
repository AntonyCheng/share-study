package top.sharehome.share_study.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注册申请审核接口过滤器
 *
 * @author AntonyCheng
 */
@WebFilter(filterName = "TeacherCensorApiFilter", urlPatterns = "/*")
@Slf4j
public class TeacherCensorApiFilter implements Filter {
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        TeacherLoginDto adminLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);

        String requestUri = request.getRequestURI();
        String needHandleRequest = "/api/teacher_censor/**";
        if (!ANT_PATH_MATCHER.match(needHandleRequest, requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (adminLoginDto == null) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(R.failure(RCodeEnum.NOT_LOGIN)));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
