package uk.co.metro.carousel.images;

import java.io.IOException;
import java.net.SocketTimeoutException;

import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.util.UrlLoader;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/* Integer is for progress update, if we need it */
public class ImageNetworkLoadTask extends AsyncTask<Object, Integer, Bitmap> {
	private final static String LOG_CAT = "Images";
	private String url = null;
	private Post post = null;
	private ImageListener listener = null;

	public ImageNetworkLoadTask(String url, Post post, ImageListener listener) {
		this.url = url;
		this.post = post;
		this.listener = listener;
	}
	
	/**
	 * Returns null if there was an exception
	 */
    protected Bitmap doInBackground(Object... args) {
    	UrlLoader loader = new UrlLoader();
        try {
        	Log.i(LOG_CAT, "Loading " + url);
			return loader.loadBitmapFromUrl(url);
		} catch (SocketTimeoutException e) {
			if (listener != null) {
				listener.onImageException(e);
			}
		} catch (IOException e) {
			if (listener != null) {
				listener.onImageException(e);
			}
		}
        return null;
    }

    protected void onPostExecute(Bitmap bmp) {
    	if (listener != null) {
    		listener.onImageReady(bmp, post);
    	}
    }
}
