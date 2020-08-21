package com.zzti.fengyongge.imagepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.zzti.fengongge.imagepicker.R;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import com.zzti.fengyongge.imagepicker.util.StringUtils;
import com.zzti.fengyongge.imagepicker.view.SelectPhotoItem;

import java.util.ArrayList;

/**
 * Created by fengyongge on 2016/5/24
 */
public class PhotoSelectorAdapter extends MBaseAdapter<PhotoModel> {
	private int itemWidth;
	private int horizentalNum = 3;
	private SelectPhotoItem.onPhotoItemCheckedListener listener;
	private LayoutParams itemLayoutParams;
	private SelectPhotoItem.onItemClickListener mCallback;
	private OnClickListener cameraListener;
	private int limit;
	private PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models) {
		super(context, models);
	}

	public PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth,
								SelectPhotoItem.onPhotoItemCheckedListener listener,
								SelectPhotoItem.onItemClickListener mCallback, OnClickListener cameraListener, int limit) {
		this(context, models);
		setItemWidth(screenWidth);
		this.listener = listener;
		this.mCallback = mCallback;
		this.limit = limit;
		this.cameraListener = cameraListener;
	}
	

	/** 设置每一个Item的宽高 */
	public void setItemWidth(int screenWidth) {
		int horizentalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
		this.itemWidth = (screenWidth - (horizentalSpace * (horizentalNum - 1))) / horizentalNum;
		this.itemLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SelectPhotoItem item = null;
		TextView tvCamera = null;
		if (position == 0 && StringUtils.isNull(models.get(position).getOriginalPath())) { // 当时第一个时，显示按钮
			if (convertView == null || !(convertView instanceof TextView)) {
				tvCamera = (TextView) LayoutInflater.from(context).inflate(R.layout.view_camera, null);
				tvCamera.setHeight(itemWidth);
				tvCamera.setWidth(itemWidth);
				convertView = tvCamera;
			}
			convertView.setOnClickListener(cameraListener);
		} else { // 显示图片
			if (convertView == null || !(convertView instanceof SelectPhotoItem)) {
				item = new SelectPhotoItem(context, listener,limit);
				item.setLayoutParams(itemLayoutParams);
				convertView = item;
			} else {
				item = (SelectPhotoItem) convertView;
			}
			item.setImageDrawable(models.get(position));
			item.setSelected(models.get(position).isChecked());
			item.setOnClickListener(mCallback, position);
		}
		return convertView;
	}
}
