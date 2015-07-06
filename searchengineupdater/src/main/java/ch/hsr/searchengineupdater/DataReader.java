package ch.hsr.searchengineupdater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public class DataReader {
	
	 public MultiMap movieId2tags(){
		try {
			String line;
			Reader in = new FileReader("/home/lukas/Downloads/ml-latest-small/tags.csv");
			BufferedReader br = new BufferedReader(in);
			MultiMap movieId2tags = new MultiValueMap();
			while((line = br.readLine()) != null){
				
				String[] str = line.split(",");
				
				if (str[0].equals("userId")){
					continue;
					}
				Integer movieid = Integer.parseInt(str[1]);
				String tag = str[2];
				movieId2tags.put(movieid, tag);
		
			}
			br.close();
			return movieId2tags;
		}
			
			catch(IOException e){
				e.printStackTrace();
				return null;
			}		
	}

}
