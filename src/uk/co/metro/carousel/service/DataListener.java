package uk.co.metro.carousel.service;

import java.util.List;

import uk.co.metro.carousel.domain.Post;
import android.graphics.Bitmap;

public interface DataListener extends ExceptionListener {
	void onAllPostsLoaded(List<Post> posts);
	//void onImageLoaded(int postId);
}
