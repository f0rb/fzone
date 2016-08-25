package win.doyto.web;


import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description goes here.
 *
 * @author yuanzhen
 * @version 1.0, 2012-02-09
 */
public class WebContextFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        WebContext.init(req, res);
        try {
            chain.doFilter(request, response);
        } finally {
            WebContext.cleanup();
        }
    }

    @Override
    public void destroy() {
    }
}
