package ch.hsr.searchengineupdater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.solr.common.SolrInputDocument;




public class SolrInputFactory {
	
	public Collection<SolrInputDocument> movies(){
		try {
		Reader in;
		
			in = new FileReader("/home/lukas/Downloads/ml-latest-small/movies.csv");
		
		BufferedReader br = new BufferedReader(in);
		String line;
		DataReader datareader = new DataReader();
		MultiMap movieId2tags = datareader.movieId2tags();
		ArrayList<SolrInputDocument> solrDocument = new ArrayList<SolrInputDocument>();
		while((line = br.readLine()) != null){
			String[] str = line.split(",");
			if (str[0].equals("movieId")){
				continue;
				}
			Integer movieid = Integer.parseInt(str[0]);
			String title = str[1];
			@SuppressWarnings("unchecked")
			List<String> tags = (List<String>) movieId2tags.get(movieid);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", movieid);

			doc.addField("title", title);
			StringBuilder tagsstr = new StringBuilder();
			if(tags != null){
			for(String tag : tags){
				tagsstr.append(tag + " ");
			}
			doc.addField("tags", tagsstr.toString());
			}
			solrDocument.add(doc);
		}
		br.close();
		return solrDocument;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
}
