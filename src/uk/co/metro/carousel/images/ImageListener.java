package uk.co.metro.carousel.images;

import uk.co.metro.carousel.domain.Post;
import android.graphics.Bitmap;

public interface ImageListener {
	void onImageReady(Bitmap bmp, Post post);
	void onImageException(Throwable ex);
}
