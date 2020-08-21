package com.zzti.fengyongge.imagepicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzti.fengongge.imagepicker.R;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import com.zzti.fengyongge.imagepicker.util.AnimationUtils;
import com.zzti.fengyongge.imagepicker.view.PhotoPreview;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fengyongge on 2016/5/24
 */
public class BasePhotoPreviewActivity extends Activity implements ViewPager.OnPageChangeListener, OnClickListener {
	protected List<PhotoModel> photos = new ArrayList<>();
	protected int current;
	private Boolean is_save;
	private ViewPager mViewPager;
	private RelativeLayout layoutTop;
	private ImageButton btnBack;
	private TextView tvPercent;
	protected boolean isUp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photopreview);
		layoutTop = (RelativeLayout) findViewById(R.id.layout_top_app);
		btnBack = (ImageButton) findViewById(R.id.btn_back_app);
		tvPercent = (TextView) findViewById(R.id.tv_percent_app);
		mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
		btnBack.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
		overridePendingTransition(R.anim.activity_alpha_action_in, 0); // 渐入效果

		layoutTop.setVisibility(View.GONE);
	}


	private PagerAdapter mPagerAdapter = new PagerAdapter() {
		@Override
		public int getCount() {
			if (photos == null) {
				return 0;
			} else {
				return photos.size();
			}
		}

		@Override
		public View instantiateItem(final ViewGroup container, final int position) {
			PhotoPreview photoPreview = new PhotoPreview(BasePhotoPreviewActivity.this);
			((ViewPager) container).addView(photoPreview);
			photoPreview.loadImage(photos.get(position),is_save);
			photoPreview.setOnClickListener(photoItemClickListener);
			return photoPreview;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	};


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_back_app)
			finish();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		current = arg0;
		updatePercent();
	}


	/** 图片点击事件回调 */
	private OnClickListener photoItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isUp) {
				new AnimationUtils(getApplicationContext(), R.anim.translate_up)
						.setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
				isUp = true;
			} else {
				new AnimationUtils(getApplicationContext(), R.anim.translate_down_current)
						.setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
				isUp = false;
			}
		}
	};


	/** 绑定数据，更新界面 */
	protected void bindData(Boolean is_save) {
		this.is_save = is_save;
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(current);
	}

	/**
	 * 展示当前张数
	 */
	protected void updatePercent() {
		tvPercent.setText((current + 1) + "/" + photos.size());
	}
	
}
