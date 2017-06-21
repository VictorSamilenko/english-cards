package ua.cards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ua.cards.model.User;
import ua.cards.service.UserService;
import ua.cards.util.Utils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorizationFilter implements Filter {

    private static Pattern[] patterns;

    @Autowired
    UserService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());

        String[] urlPatterns = {"/", "/words(/\\d*)?", "/groups(/\\d*)?", "/words/get", "/groups/get"};
        patterns = new Pattern[urlPatterns.length];
        int i = 0;
        for (String url : urlPatterns) {
            patterns[i++] = Pattern.compile(url);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String requestUrl = ((HttpServletRequest) servletRequest).getRequestURI();

        Matcher m = null;
        for (Pattern p : patterns) {
            m = p.matcher(requestUrl);
            if (m.matches())
                break;
        }
        if (m.matches()) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String hashCode = null, userAgent = request.getHeader("User-Agent");
            if (request.getCookies() != null)
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("HashCode")) {
                        hashCode = cookie.getValue();
                        break;
                    }
                }

            User user = service.getUserByHashCode(hashCode);
            if (user == null || !hashCode.equals(Utils.generateHashCode(userAgent, user))) {
                response.setHeader("Set-Cookie", "HashCode=0; Max-Age=0");
                filterChain.doFilter(servletRequest, response);
                return;
            }
            request.setAttribute("user", user);
            filterChain.doFilter(request, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
