package uk.co.metro.carousel.main;

import uk.co.metro.carousel.R;
import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.images.ImageListener;
import uk.co.metro.carousel.service.ImageService;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** Shows post image and title */
public class PostFragment extends Fragment implements ImageListener {
	private static final String LOG_CAT = "PostFrag";
	private static final String ARG_TITLE = "title";
	private static final String ARG_POSITION = "index";
	private View rootView = null;
	private Post post = null;
	
	public PostFragment(Post post, int position) {
		Bundle args = new Bundle();
		args.putString(ARG_TITLE, post.getTitle());
		args.putInt(ARG_POSITION, position);
		setArguments(args);
		this.post = post;
	}
	
	@Override
	public void onImageReady(Bitmap bmp, Post bitmapPost) {
		if (post.getId() == bitmapPost.getId()) {
			ImageView imageView = (ImageView) rootView.findViewById(R.id.article_image);
			imageView.setImageBitmap(bmp);
			
//			TextView titleView = (TextView) rootView.findViewById(R.id.section_label);
//			titleView.bringToFront();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(LOG_CAT, "Entering onCreateView");
		rootView = inflater.inflate(R.layout.fragment_main, container, false);
		
		TextView titleView = (TextView) rootView.findViewById(R.id.section_label);
		titleView.setText(getArguments().getString(ARG_TITLE));
//		titleView.bringToFront();
		
		TextView positionView = (TextView) rootView.findViewById(R.id.article_position);
		int position = getArguments().getInt(ARG_POSITION);
		positionView.setText(String.valueOf(position));
		
		Point size = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getSize(size);
		ImageService imageService = new ImageService(size.x, size.y, null);
		imageService.loadImage(post, this);
		//ImageNetworkLoadTask task = new ImageNetworkLoadTask(post, this);
		return rootView;
	}

	@Override
	public void onImageException(Throwable ex) {
		ex.printStackTrace();
		Log.e(LOG_CAT, ex.getMessage());
	}

}
