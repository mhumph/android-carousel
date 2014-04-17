package uk.co.metro.carousel.images;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.metro.carousel.domain.Post;
import android.graphics.Bitmap;

/** 
 * Keeps 3 images (typically current, next and prev).  
 * XXX: Improve thread safety.
 */
public class ImageMemoryCache implements ImageListener {
	private ImageListener listener = null;
	private int numToEagerLoad = 3;
	private Map<Integer, Bitmap> bitmapsByPostId = new Hashtable<Integer, Bitmap>();
	private Set<Integer> requestsInProgress = new HashSet<Integer>();
	private int currentPosition = 0;
	
	public ImageMemoryCache(ImageListener listener) {
		this.listener = listener;
	}
	
	/** Eager load some images */
	public void init(List<Post> postList) {
		for (int i = 0; ((i < numToEagerLoad) && (i < postList.size())); i++) {
			Post post = postList.get(i);
			requestImageLoad(post);
		}
		// When third request comes back we'll start pre-fetching for the disk cache
	}
	
	/** Call this when user moves to a new post */
	public void onMove(int currentPosition, List<Post> postList) {
		this.currentPosition = currentPosition;
	}
	
	private void requestImageLoad(Post post) {
		Bitmap bmp = bitmapsByPostId.get(post.getId());
		if (bmp != null) {
			listener.onImageReady(bmp, post);
		} else {
			if (!requestsInProgress.contains(post.getId())) {
				// TODO: Disk cache
				//diskCache.loadImageInBackground(post);
				
				// TODO: Use ImageService
				ImageNetworkLoadTask task = new ImageNetworkLoadTask(post.getImageUrl(), post, this);
				task.execute(post);
			}
		}
	}
	
	@Override
	public void onImageReady(Bitmap bmp, Post post) {
		bitmapsByPostId.put(post.getId(), bmp);
		requestsInProgress.remove(post.getId());
		listener.onImageReady(bmp, post);
		if (bitmapsByPostId.size() >= this.numToEagerLoad) {
			// TODO: diskCache.startPrefetching(postList)
		}
	}

	@Override
	public void onImageException(Throwable ex) {
		listener.onImageException(ex);
	}
}
