package top.sharehome.share_study.filter;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.TeacherLoginDto;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 高校接口过滤器
 *
 * @author AntonyCheng
 */
@WebFilter(filterName = "CollegeApiFilter", urlPatterns = "/*")
@Slf4j
public class CollegeApiFilter implements Filter {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        boolean matchResult = ANT_PATH_MATCHER.match("/api/college/**", requestUri);

        if (!matchResult) {
            filterChain.doFilter(request, response);
            return;
        }

        TeacherLoginDto adminLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (adminLoginDto == null) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(R.failure(RCodeEnum.NOT_LOGIN)));
            return;
        }

        if (!Objects.equals(adminLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED)));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
