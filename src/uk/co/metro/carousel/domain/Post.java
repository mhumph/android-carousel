package uk.co.metro.carousel.domain;

public class Post {
	private int id = 0;
	private String title = null;
	private String imageUrl = null;
	
	public Post(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
