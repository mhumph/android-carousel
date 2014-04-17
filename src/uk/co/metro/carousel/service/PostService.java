package uk.co.metro.carousel.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.util.UrlLoader;

public class PostService implements DataListener {
	private List<Post> postList = null;
	private DataListener listener = null;
	private int numImagesLoaded = 0;
	
	public PostService(DataListener listener) {
		this.listener = listener;
	}
	
	/** Loads example data */
	public List<Post> loadExamplePosts() {
		List<Post> list = new ArrayList<Post>();
		list.add(new Post(18, "Miley Cyrus blah"));
		list.add(new Post(4, "Wayne Rooney bling"));
		list.add(new Post(73, "Bobbly Dob goo"));
		return list;
	}
	
	public void loadPostsInBackground() {
		FeedLoadTask loader = new FeedLoadTask(this, "http://d20o19rp6m6brr.cloudfront.net/news-feed/?number=15");
		loader.execute();
	}
	
	@Override
	public void onAllPostsLoaded(List<Post> postsParam) {
		postList = postsParam;
		listener.onAllPostsLoaded(postList);
		
//		Post postToLoadImageFor = postList.get(numImagesLoaded);
//		NetImageLoadTask task = new NetImageLoadTask();
//		task.execute(postToLoadImageFor);
	}
	
	@Override
	public void onException(Throwable ex) {
		listener.onException(ex);
	}
	
//	@Override
//	public void onImageLoaded(int postId) {
//		listener.onImageLoaded(postId);
//	}
	
//	public Post getPost(int pos) {
//		if (postList == null) {
//			throw new NullPointerException("postList is null");
//		}
//		return postList.get(pos);
//	}
	
	// XXX: Delegate to image service
	private void processImage(Bitmap bmp) {
		// TODO: 
	}
	
	class NetImageLoadTask extends AsyncTask<Post, Integer, Bitmap> {
		
		/** Returns null if there was an exception */
	    protected Bitmap doInBackground(Post... posts) {
	    	UrlLoader loader = new UrlLoader();
	        try {
				return loader.loadBitmapFromUrl(posts[0].getImageUrl());
			} catch (SocketTimeoutException e) {
				onException(e);
			} catch (IOException e) {
				onException(e);
			}
	        return null;
	    }

	    /** Returns null if there was an exception */
	    protected void onPostExecute(Bitmap bmp) {
	    	processImage(bmp);
	    }
	}
}
