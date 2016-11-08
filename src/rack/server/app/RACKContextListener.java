package rack.server.app;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import utility.ContentLoader;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class RACKContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("RACK context destroyed successfully!");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		MaxentTagger tagger = null;
		ServletContext context = sce.getServletContext();
		
		String modelPath = context.getRealPath("/models");
		String modelFileURL = modelPath + "/english-left3words-distsim.tagger";
		tagger = new MaxentTagger(modelFileURL);
		// store the tagger object
		context.setAttribute("tagger", tagger);
		
		String stopword=context.getRealPath("/stopword");
		String stopwordFile=stopword+"/en.txt";
		ArrayList<String> stops=new ArrayList<>(Arrays.asList(ContentLoader.getAllLines(stopwordFile)));
		context.setAttribute("stopwords", stops);
		
		System.out.println("RACK context initialized successfully!");
	}

}
