package uk.co.metro.carousel.main;

import java.util.List;

import uk.co.metro.carousel.R;
import uk.co.metro.carousel.R.id;
import uk.co.metro.carousel.R.layout;
import uk.co.metro.carousel.R.menu;
import uk.co.metro.carousel.domain.Post;
import uk.co.metro.carousel.service.DataListener;
import uk.co.metro.carousel.service.FeedLoadTask;
import uk.co.metro.carousel.service.PostService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements DataListener {
	private static final String LOG_CAT = "Main";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private ProgressDialog progressDialog;  
	private List<Post> postList;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initSplashScreen();
		
		Log.i(LOG_CAT, "About to load posts in background");
//		PostService postService = new PostService(this);
//		postService.loadPostsInBackground();
		
		FeedLoadTask loader = new FeedLoadTask(this, "http://api.metro.co.uk/news-feed/?number=15");
		loader.execute();
	}
	
	private void initSplashScreen() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Loading application View, please wait...");  
        //This dialog can't be canceled by pressing the back key  
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.show();  
	}
	
	@Override
	public void onAllPostsLoaded(List<Post> posts) {
		Log.i(LOG_CAT, "All posts loaded");
		postList = posts;
		progressDialog.dismiss();
		
		setContentView(R.layout.activity_main);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	
//	@Override
//	public void onImageLoaded(int postId) {
//		// TODO Auto-generated method stub
//		
//	}
	
	@Override
	public void onException(Throwable ex) {//throws Throwable {
		showDialog(ex.getMessage(), "Error");
		//throw(ex);
	}
	
	private void showDialog(String msg, String title) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(msg);
		builder.setTitle(title);

		builder.setNegativeButton("Cancel",	//XXX:R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						// User cancelled the dialog
					}
				});

		dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			Log.i(LOG_CAT, "getItem:" + position);
			Post post = postList.get(position);
			return new PostFragment(post, position);
		}

		@Override
		public int getCount() {
			return postList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Post post = postList.get(position);
			return post.getTitle();
		}
	}

}
