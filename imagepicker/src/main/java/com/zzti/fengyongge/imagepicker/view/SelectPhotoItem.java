package com.zzti.fengyongge.imagepicker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengongge.imagepicker.R;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;

import java.util.Random;

/**
 * Created by fengyongge on 2016/5/24
 */
public class SelectPhotoItem extends LinearLayout implements OnCheckedChangeListener, OnClickListener {
	private ImageView ivPhoto;
	private CheckBox cbPhoto;
	private View cb_photo_RL;
	private onPhotoItemCheckedListener listener;
	private PhotoModel photo;
	private boolean isCheckAll;
	private onItemClickListener l;
	private int position;
	private int limit;
	private SelectPhotoItem(Context context) {
		super(context);
	}

	public SelectPhotoItem(final Context context, final onPhotoItemCheckedListener listener,final int limit) {
		this(context);
		LayoutInflater.from(context).inflate(R.layout.layout_photoitem, this, true);
		this.listener = listener;
		this.limit = limit;

		setOnClickListener(this);
		ivPhoto = (ImageView) findViewById(R.id.iv_photo_lpsi);
		cbPhoto = (CheckBox) findViewById(R.id.cb_photo_lpsi);
		cb_photo_RL = findViewById(R.id.cb_photo_RL);

		cb_photo_RL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!cbPhoto.isChecked()) {
					if (PhotoSelectorActivity.selected.size() >= limit) {
						Toast.makeText(context, "最多添加"  + limit +"张", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				cbPhoto.setChecked(!cbPhoto.isChecked());
				boolean isChecked = cbPhoto.isChecked();
				if (!isCheckAll) {
					listener.onCheckedChanged(photo, null, isChecked);
				}
				
				if (isChecked) {
					setDrawingable();
					ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
				} else {
					ivPhoto.clearColorFilter();
				}
				photo.setChecked(isChecked);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (!isCheckAll) {
			listener.onCheckedChanged(photo, buttonView, isChecked);
		}
		if (isChecked) {
			setDrawingable();
			ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		} else {
			ivPhoto.clearColorFilter();
		}
		photo.setChecked(isChecked);
	}

	public void setImageDrawable(final PhotoModel photo) {
		this.photo = photo;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ImageLoader.getInstance().displayImage("file://" + photo.getOriginalPath(), ivPhoto);
			}
		}, new Random().nextInt(10));
	}

	private void setDrawingable() {
		ivPhoto.setDrawingCacheEnabled(true);
		ivPhoto.buildDrawingCache();
	}

	@Override
	public void setSelected(boolean selected) {
		if (photo == null) {
			return;
		}
		isCheckAll = true;
		cbPhoto.setChecked(selected);
		isCheckAll = false;
	}

	public void setOnClickListener(onItemClickListener l, int position) {
		this.l = l;
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		if (l != null)
			l.onItemClick(position);
	}

	public static interface onPhotoItemCheckedListener {
		public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked);
	}

	public interface onItemClickListener {
		public void onItemClick(int position);
	}

}
