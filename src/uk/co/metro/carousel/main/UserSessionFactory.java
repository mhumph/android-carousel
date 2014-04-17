package uk.co.metro.carousel.main;

import uk.co.metro.carousel.service.DataListener;
import uk.co.metro.carousel.service.PostService;

public class UserSessionFactory {

	public UserSession newInstance(DataListener listener) {
		PostService postService = new PostService(listener);
		
		UserSession session = new UserSession();
		session.setPostService(postService);
		
		return session;
	}
}
