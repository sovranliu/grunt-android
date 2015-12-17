package com.wehop.grunt.view.form;

import com.slfuture.carrie.base.text.Text;
import com.wehop.grunt.framework.Utility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 首页
 */
public class UserActivity extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_user, container, true);
	}

	@Override
	public void onStart() {
		super.onStart();
		prepare();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * 界面预处理
	 */
	public void prepare() {
		final ImageView imgPhoto = (ImageView) this.getActivity().findViewById(R.id.user_button_photo);
		if(Text.isBlank(Logic.user.photo)) {
			imgPhoto.setImageBitmap(Utility.makeImageRing(Utility.makeCycleImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.photo_default), 200, 200), 4));
		}
		else {
            Host.doImage("image", new ImageResponse(Logic.user.photo, null) {
				@Override
				public void onFinished(Bitmap content) {
					if(null == content) {
						return;
					}
					imgPhoto.setImageBitmap(Utility.makeImageRing(Utility.makeCycleImage(content, 200, 200), 4));
				}
            }, Logic.user.photo);
		}
//		imgPhoto.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(UserActivity.this.getActivity(), UserInfoActivity.class));
//			}
//		});
		ViewGroup viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_info);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserActivity.this.getActivity(), UserInfoActivity.class));
			}
		});
		//
		viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_blog);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserActivity.this.getActivity(), WebActivity.class);
				intent.putExtra("url", Host.fetchURL("nearby", Logic.user.token));
				startActivity(intent);
			}
		});
		//
		viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_concern);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserActivity.this.getActivity(), FriendActivity.class));
			}
		});
		//
		viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_activity);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserActivity.this.getActivity(), MyActivityActivity.class));
			}
		});
		//
		viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_message);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserActivity.this.getActivity(), MyMessageActivity.class));
			}
		});
		//
		viewGroup = (ViewGroup) this.getActivity().findViewById(R.id.user_layout_setting);
		viewGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserActivity.this.getActivity(), ConfigActivity.class));
			}
		});
		//
		TextView labNickName = ((TextView) this.getActivity().findViewById(R.id.user_label_nickname));
		labNickName.setText(Logic.user.nickName);
	}
}
