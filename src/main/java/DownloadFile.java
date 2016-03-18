import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "DownloadFile", urlPatterns = {"/DownloadFile"})

public class DownloadFile extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

		HttpSession session = request.getSession();

		String filename = "output.wav";
		response.setContentType("audio/wav"); 
		response.setHeader("Content-Disposition","attachment; filename=\""+filename+"\"");  

		InputStream speech = (InputStream)session.getAttribute("input");
		OutputStream output = response.getOutputStream();

		//create a downloadable file
		byte[] buf = new byte[2046];
		int len;

		while ((len = speech.read(buf)) > 0){
			output.write(buf, 0, len);
		}
									
		output.flush();  
		output.close();  

	}
}