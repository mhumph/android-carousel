package uk.co.metro.carousel.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.util.UrlLoader;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class FeedLoadTask extends AsyncTask<String, String, JSONObject> {
	private static final String JSON_ROOT = "posts";
	private static final String JSON_TITLE = "title";
	private static final String JSON_URL = "URL";
	private static final String JSON_ID = "ID";
	private JSONArray wordPressJSON = null;
	private final DataListener listener;
	private String feedUrl = null;

	public FeedLoadTask(DataListener listener, String feedUrl) {
		this.listener = listener;
		this.feedUrl = feedUrl;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected JSONObject doInBackground(String... args) {
		// get data
		JSONObject json = null;
		try {
			UrlLoader jParser = new UrlLoader();
			json = jParser.getJSONFromUrl(feedUrl);
			Throwable ex = jParser.getLastException();
			if (ex != null) {
				listener.onException(ex);
			}
		} catch (/*XXX:MetroNetwork*/Exception ex) {
			listener.onException(ex);
		}
		return json;
	}

	// TODO: Filter out articles that have already been read
	@Override
	protected void onPostExecute(JSONObject json) {
		Throwable jsonEx = null;
		List<Post> postList = new ArrayList<Post>();
		
		if (json != null) {	
			ApiPostFactory apiPostFactory = new ApiPostFactory();
			try {
				// Getting JSON Array from URL
				wordPressJSON = json.getJSONArray(JSON_ROOT);

				// loop around array
				for (int i = 0; (i < wordPressJSON.length()); i++) {
					JSONObject c = wordPressJSON.getJSONObject(i);
					ApiPost post = apiPostFactory.newApiPost(c);

					if (!post.getImageUrl().equals("")) {
						// Retrieve article fields from JSON Object
						String title = Html.fromHtml(c.getString(JSON_TITLE)).toString();
						String url = c.getString(JSON_URL);
						int id = c.getInt(JSON_ID);

						// Article article = new Article(title, api.getImageUrl(), url, id, api.getChannel(), api.getSubChannel());
						Post postToAdd = new Post(id, title);
						postToAdd.setImageUrl(post.getImageUrl());
						postList.add(postToAdd);
					} else {
						Log.d("Feed", "Missing article image URL.. Skipping article..");
					}

				}
			} catch (JSONException e) {
				listener.onException(e);
			}
		}

		Log.i("PostService", "postList.size=" + postList.size());
		listener.onAllPostsLoaded(postList);
	}

	interface ApiPost {
		String getImageUrl();

		String getChannel();

		String getSubChannel();
	}

	abstract class BaseApiPost implements ApiPost {
		protected String imageUrl = null;
		protected String channel = null;
		protected String subChannel = null;

		@Override
		public String getImageUrl() {
			return imageUrl;
		}

		@Override
		public String getChannel() {
			return channel;
		}

		@Override
		public String getSubChannel() {
			return subChannel;
		}
	}

	class ApiPostFactory {
		ApiPost newApiPost(JSONObject jsonObj) throws JSONException {
			if (jsonObj.has(ApiV1Post.FEATURED_IMAGE)) {
				return new ApiV1Post(jsonObj);
			} else {
				return new ApiV0Post(jsonObj);
			}
		}
	}

	class ApiV0Post extends BaseApiPost {
		private static final String CHANNEL = "category";
		private static final String SUBCHANNEL = "sub_category";
		private static final String IMAGE_URL = "featured_image_thumb";

		public ApiV0Post(JSONObject jsonObj) throws JSONException {
			init(jsonObj);
		}

		void init(JSONObject jsonObj) throws JSONException {
			imageUrl = jsonObj.optString(IMAGE_URL);
			channel = jsonObj.optString(CHANNEL);
			subChannel = jsonObj.optString(SUBCHANNEL);
		}
	}

	class ApiV1Post extends BaseApiPost {
		public static final String FEATURED_IMAGE = "featured_image";
		private static final String CHANNELS = "categories";
		private static final String CHANNEL_SLUG = "slug";
		private static final String PARENT_DETAIL = "parent_detail";

		public ApiV1Post(JSONObject jsonObj) throws JSONException {
			init(jsonObj);
		}

		void init(JSONObject jsonObj) throws JSONException {
			imageUrl = jsonObj.optString(FEATURED_IMAGE);

			// WP Public API has a weird structure for channels
			JSONObject channels = jsonObj.optJSONObject(CHANNELS);
			if (channels != null) {
				Iterator<String> channelKeys = channels.keys();
				if (channelKeys.hasNext()) {
					String firstChannelName = channelKeys.next();
					JSONObject firstChannel = channels.getJSONObject(firstChannelName);
					if (firstChannel.has(PARENT_DETAIL)) {
						JSONObject parent = firstChannel.getJSONObject(PARENT_DETAIL);
						channel = parent.getString(CHANNEL_SLUG);
						subChannel = firstChannel.getString(CHANNEL_SLUG);
					} else {
						channel = firstChannel.getString(CHANNEL_SLUG);
					}
				}
			}
		}
	}

}
