package navigeter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/pathfinding.do")
public class PathFindingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		float sx = Float.parseFloat(request.getParameter("sx"));
		float sy = Float.parseFloat(request.getParameter("sy"));
		float ex = Float.parseFloat(request.getParameter("ex"));
		float ey = Float.parseFloat(request.getParameter("ey"));
		
		OdsayApiKeySample odsay = new OdsayApiKeySample();
		String result = odsay.Search(sx, sy, ex, ey);
		
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(result);
		
		System.out.print(result);
	}
}