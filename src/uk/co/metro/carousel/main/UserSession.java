package uk.co.metro.carousel.main;

import java.util.List;

import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.service.PostService;

public class UserSession {
	private List<Post> postList = null;
	private PostService postService = null;
	
	public void setPostService(PostService postService) {
		this.postService = postService;
	}
	
	public void loadPostsInBackground() {
		postService.loadPostsInBackground();
	}

}
