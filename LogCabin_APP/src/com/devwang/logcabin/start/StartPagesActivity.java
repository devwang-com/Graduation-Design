package com.devwang.logcabin.start;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.devwang.logcabin.R;

public class StartPagesActivity extends Activity {
	private ViewPager viewPager;
	private ImageView imageView;
	private ArrayList<View> pageViews;
	private ImageView[] imageViews;
	private ViewGroup viewPictures;
	private ViewGroup viewPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.start_pager00, null));
		pageViews.add(inflater.inflate(R.layout.start_pager00, null));
		pageViews.add(inflater.inflate(R.layout.start_pager_end, null));

		imageViews = new ImageView[pageViews.size()];
		viewPictures = (ViewGroup) inflater.inflate(R.layout.startpagers_main,
				null);
		viewPager = (ViewPager) viewPictures.findViewById(R.id.guidePagers);
		viewPoints = (ViewGroup) viewPictures.findViewById(R.id.viewPoints);
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(StartPagesActivity.this);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(5, 0, 5, 0);
			imageViews[i] = imageView;
			if (i == 0)
				imageViews[i].setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
			else
				imageViews[i].setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
			viewPoints.addView(imageViews[i]);
		}
		setContentView(viewPictures);

		viewPager.setAdapter(new NavigationPageAdapter());
		viewPager.setOnPageChangeListener(new NavigationPageChangeListener());
	}

	class NavigationPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(pageViews.get(position));
			return pageViews.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(pageViews.get(position));
		}

	}

	class NavigationPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[i].setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				if (position != i)
					imageViews[i].setImageDrawable(getResources().getDrawable(
							R.drawable.page_indicator_unfocused));
			}
		}

	}
	
	public void startbutton(View v) {
		Intent intent = new Intent(StartPagesActivity.this,
				StartAnimationActivity.class);
		startActivity(intent);
		StartPagesActivity.this.finish();
	}

}
