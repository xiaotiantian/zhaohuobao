package com.bfz.zhbao.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.bfz.zhbao.R;

/**
 * @package£ºcom.huaheng.client.activity.view
 * @author£ºAllen
 * @email£ºjaylong1302@163.com
 * @data£º2012-9-27 ÉÏÎç8:59:40
 * @description£ºThe class is for...
 */
public class ImageLoadingDialog extends Dialog {

	public ImageLoadingDialog(Context context) {
		super(context, R.style.ImageloadingDialogStyle);
	}

	private ImageLoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_imageloading);
	}

}
