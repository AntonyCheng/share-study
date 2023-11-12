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
import java.util.Objects;

/**
 * 资料标签接口过滤器
 *
 * @author AntonyCheng
 * @since 2023/6/21 23:16:54
 */
@WebFilter(filterName = "TagApiFilter", urlPatterns = "/*")
@Slf4j
public class TagApiFilter implements Filter {
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        TeacherLoginDto adminLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);

        String requestUri = request.getRequestURI();
        boolean matchTagResult = ANT_PATH_MATCHER.match("/api/tag/**", requestUri);
        String normalUserRequestList = "/api/tag/list";

        if (!matchTagResult
                || ANT_PATH_MATCHER.match(normalUserRequestList, requestUri)) {
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
