package pungmul.pungmul.config;

//public class SessionCheckFilter extends HttpFilter {
//
//    @Override
//    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpSession session = request.getSession(false);
//        if (session != null || session.getAttribute(SessionConst.SESSION_USER) == null) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션이 만료되었거나 로그인 상태가 아닙니다.");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}
