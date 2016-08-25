package win.doyto.web;

import java.io.IOException;
import javax.servlet.*;

/**
 * Description goes here.
 *
 * @author yuanzhen
 * @version 1.0, 2012-02-09
 */
public class WebEncodingFilter implements Filter {

    /** The default character encoding to set for requests that pass through this filter. */
    private String encoding = "UTF-8";

    /** Should a character encoding specified by the client be ignored? */
    private boolean forceEncoding = false;

    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = filterConfig.getInitParameter("encoding");
        forceEncoding = Boolean.parseBoolean(filterConfig.getInitParameter("forceEncoding"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 使request编码与JSP页面保持一致
        if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
            request.setCharacterEncoding(this.encoding);
            if (this.forceEncoding) {
                response.setCharacterEncoding(this.encoding);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
