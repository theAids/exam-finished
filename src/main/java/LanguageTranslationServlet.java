import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.service.WatsonService;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.util.ResponseUtil;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@WebServlet(name = "LanguageTranslationServlet", urlPatterns = {"/LanguageTranslationServlet"})
  
public class LanguageTranslationServlet extends HttpServlet {
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
			
			LanguageTranslatorConnector connector = new LanguageTranslatorConnector();
			LanguageTranslation languageTranslation = new LanguageTranslation();
				
			languageTranslation.setUsernameAndPassword(connector.getUsername(),connector.getPassword());

			//translate from spanish to english
            TranslationResult translated = languageTranslation.translate(request.getParameter("inputText"), "es", "en");

			String translatedText = translated.toString();
			
			//parse output
			String convertedStr = "";
			try{

				//get the raw json output
				JSONParser parser = new JSONParser();
				String[] jsonStr = translatedText.split(" ",2);
				JSONObject obj = (JSONObject)parser.parse((String)jsonStr[1]);
				
				JSONArray objArr = (JSONArray) obj.get("translations");
				convertedStr = (String) ((JSONObject)objArr.get(0)).get("translation");
				
				//text-to-speech codes
				TexttoSpeechConnector connector2 = new TexttoSpeechConnector();      
				TextToSpeech service = new TextToSpeech();
				service.setUsernameAndPassword(connector2.getUsername(),connector2.getPassword());
				
				String format = "audio/wav";
				
				InputStream speech = service.synthesize(convertedStr, format);


				//redirect to convert.jsp
				HttpSession session = request.getSession();

				session.setAttribute("input", speech);

				response.setContentType("text/html");
				response.setStatus(200);
				response.sendRedirect("convert.jsp");

			} catch (Exception e) {
                e.printStackTrace(System.err);
            }
			
    }

}
