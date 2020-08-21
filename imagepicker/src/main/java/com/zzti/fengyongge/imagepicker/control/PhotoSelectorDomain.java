package com.zzti.fengyongge.imagepicker.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengyongge.imagepicker.model.AlbumModel;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fengyongge on 2016/5/24
 */
@SuppressLint("HandlerLeak")
public class PhotoSelectorDomain {

	private AlbumController albumController;
	public PhotoSelectorDomain(Context context) {
		albumController = new AlbumController(context);
	}
	public void getReccent(final PhotoSelectorActivity.OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				List<PhotoModel> temp_photos = (List<PhotoModel>) msg.obj;

				listener.onPhotoLoaded(temp_photos);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getCurrent();
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取相册列表 */
	public void updateAlbum(final PhotoSelectorActivity.OnLocalAlbumListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				List<AlbumModel> albums = (List<AlbumModel>) msg.obj;
				int index = 0;
				for (int i = 0; i < albums.size(); i++) {
					if ("images".equals(albums.get(i).getName())) {
						index = i;
					}
				}
				if(albums.size()>0){
					albums.remove(index);
				}
				listener.onAlbumLoaded(albums);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<AlbumModel> albums = albumController.getAlbums();
				Message msg = new Message();
				msg.obj = albums;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取单个相册下的所有照片信息 */
	public void getAlbum(final String name, final PhotoSelectorActivity.OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onPhotoLoaded((List<PhotoModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getAlbum(name);
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
