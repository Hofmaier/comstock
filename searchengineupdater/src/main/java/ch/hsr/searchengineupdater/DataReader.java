package ch.hsr.searchengineupdater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public class DataReader {
	
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
				e.printStackTrace();
				return new MultiValueMap();
			}		
	}

}
