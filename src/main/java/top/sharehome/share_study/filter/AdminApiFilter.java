package top.sharehome.share_study.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.sharehome.share_study.common.constant.CommonConstant;
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
 * 管理员接口过滤器
 *
 * @author AntonyCheng
 */
@WebFilter(filterName = "AdminApiFilter", urlPatterns = "/*")
@Slf4j
public class AdminApiFilter implements Filter {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        TeacherLoginDto adminLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);

        String needHandleRequest = "/api/admin/**";

        String excludeRequestLogin = "/api/admin/login";
        String excludeRequestLogout = "/api/admin/logout";
        if (!ANT_PATH_MATCHER.match(needHandleRequest, requestUri)
                || ANT_PATH_MATCHER.match(excludeRequestLogout, requestUri)
                || ANT_PATH_MATCHER.match(excludeRequestLogin, requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (adminLoginDto == null) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(R.failure(RCodeEnum.NOT_LOGIN)));
            return;
        }

        String normalAdminRequestGet = "/api/admin/getSelf/*";
        String normalAdminRequestUpdate = "/api/admin/updateSelf";
        Boolean excludeNormalAdminRequest = ANT_PATH_MATCHER.match(normalAdminRequestGet, requestUri)
                || ANT_PATH_MATCHER.match(normalAdminRequestUpdate, requestUri);
        Integer role = adminLoginDto.getRole();
        if (Objects.equals(role, CommonConstant.ADMIN_ROLE) && Boolean.FALSE.equals(excludeNormalAdminRequest)) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED)));
            return;
        }

        filterChain.doFilter(request, response);
    }
}