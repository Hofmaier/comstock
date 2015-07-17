package ch.hsr.searchengineupdater;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReader {
	Logger log = LoggerFactory.getLogger(DataReader.class);
	
	 public MultiMap movieId2tags(String filepath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			MultiMap movieId2tags = new MultiValueMap();
			String line;
			while((line = br.readLine()) != null){
				String[] str = line.split(",");
				if (str[0].equals("userId")){
					continue;
					}
				movieId2tags.put(Integer.parseInt(str[1]), str[2]);
			}
			br.close();
			return movieId2tags;
		}
			catch(IOException e){
				log.error(e.getMessage());
				return new MultiValueMap();
			}		
	}
	 
		public Map<Long, Movie> readMovies(String filepath){
			try {
			String line;
			Map<Long, Movie> id2movie = new HashMap<Long, Movie>();
			BufferedReader tbr = new BufferedReader(new FileReader(filepath));
			while((line = tbr.readLine()) != null){
				String[] str = line.split(",");
				if (str[0].equals("movieId")){
					continue;
					}
				long id = Long.parseLong(str[0]);
				Movie m = new Movie(id, str[1]);
				id2movie.put(id, m);
			}
			tbr.close();
			return id2movie;


			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

}
