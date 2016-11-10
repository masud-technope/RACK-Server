package rack.server.app;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import core.APIToken;
import core.CodeTokenProvider;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class RACKServer_appServlet extends HttpServlet {

	public RACKServer_appServlet() {
		// default constructor
	}

	public void init(ServletConfig config) throws ServletException {
		// initialize if something is needed.
	}

	protected String collectValidTerms(String query, MaxentTagger tagger) {
		String tagged = tagger.tagString(query);
		String[] words = tagged.split("\\s+");
		String temp = new String();
		for (String word : words) {
			String[] parts = word.split("_");
			if (parts[1].contains("NN") || parts[1].contains("VB")) {
				temp += parts[0] + " ";
			}
		}
		return temp.trim();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.setCharacterEncoding("UTF-8");
		// extracting the parameters
		try {
			String query = req.getParameter("query").toString();
			if (!query.isEmpty()) {
				ServletContext context = req.getServletContext();

				// collecting the POS tagger
				MaxentTagger tagger = (MaxentTagger) context
						.getAttribute("tagger");
				String validTerms = collectValidTerms(query, tagger);
				if (tagger != null) {
					System.out.println("POS Tagger loaded successfully!");
				}

				// collecting the stop words
				@SuppressWarnings("unchecked")
				ArrayList<String> stopwords = (ArrayList<String>) context
						.getAttribute("stopwords");
				if (stopwords != null) {
					System.out.println("Stop words loaded successfully!");
				}

				CodeTokenProvider recommender = new CodeTokenProvider(
						validTerms, stopwords);
				ArrayList<APIToken> recommended = recommender
						.recommendRelevantAPIs(true);
				// now write the response
				resp.setContentType("text/json");
				if (!recommended.isEmpty()) {
					String recommendedAPIs = convert2JSON(recommended);
					resp.getWriter().print(recommendedAPIs);
				} else {
					resp.getWriter().print(
							"Sorry! Failed to collect relevant APIs");
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	protected String convert2JSON(ArrayList<APIToken> result) {
		// convert the data to JSON
		JSONArray jarray = new JSONArray();
		for (APIToken atoken : result) {
			JSONObject jobj = new JSONObject();
			jobj.put("token", atoken.token);
			jobj.put("KACScore", atoken.KACScore);
			jobj.put("KKCScore", atoken.KKCScore);
			jobj.put("totalScore", atoken.totalScore);
			jarray.add(jobj);
		}
		return jarray.toJSONString();
	}
}
