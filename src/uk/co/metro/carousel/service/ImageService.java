package uk.co.metro.carousel.service;

import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.images.ImageConfig;
import uk.co.metro.carousel.images.ImageListener;
import uk.co.metro.carousel.images.ImageMemoryCache;
import uk.co.metro.carousel.images.ImageNetworkLoadTask;
import uk.co.metro.carousel.images.ImageQuality;

public class ImageService {
	private ImageConfig config = null;
	//private ImageMemoryCache memoryCache = null;
	
	public ImageService(int width, int height, ImageMemoryCache memoryCache) {
		this.config = new ImageConfig(width, height);
		//this.memoryCache = memoryCache;
	}
	
	public void setImageQuality(ImageQuality quality) {
		this.config.setQuality(quality);
	}
	
//	public void loadImages(List<Post> postList) {
//		memoryCache.init(postList);
//	}
	
	public void loadImage(Post post, ImageListener listener) {
		String url = getResizeImageUrl(post.getImageUrl());
		ImageNetworkLoadTask task = new ImageNetworkLoadTask(url, post, listener);
		task.execute();
	}

	/** this will add the WordPress.com Photo url to the beginning as well reduce the height by a factor depending on quality enum */
	String getResizeImageUrl(String url) {
		int reduceBy = 4;

		if (config.getQuality() == ImageQuality.Medium) {
			reduceBy = 2;
		} else if (config.getQuality() == ImageQuality.High) {
			reduceBy = 1;
		}

		String resizeUrl = getRawUrlWithoutParams(url).replaceAll("http://", "http://i0.wp.com/");
		return resizeUrl + "?resize=" + (config.getDeviceWidth() / reduceBy) + "," + (config.getDeviceHeight() / reduceBy);
	}
	
	String getRawUrlWithoutParams(String url) {
		String[] strs = url.split("\\?");
		url = strs[0];
		url = url.replaceAll("i\\d\\.wp\\.com/", "");
		return url;
	}
}
