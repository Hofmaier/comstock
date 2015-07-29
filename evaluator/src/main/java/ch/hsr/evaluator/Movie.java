package ch.hsr.evaluator;

import java.util.List;

public class Movie {
	
	public Movie(long id, String title){
		this.title = title;
		this.id = id;
	}
	
	public String title;
	public long id;
	public List<String> tags;

}
