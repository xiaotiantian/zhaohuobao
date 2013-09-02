package com.bfz.zhbao.utils;


import java.io.File;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bfz.zhbao.R;

public class RecordButton extends Button {

	public RecordButton(Context context) {
		super(context);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setSavePath(String path) {
		mFileName = path;
	}

	public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
		finishedListener = listener;
	}

	private String mFileName = null;

	private OnFinishedRecordListener finishedListener;

	private static final int MIN_INTERVAL_TIME = 2000;// 2s
	private long startTime;

	private Dialog recordIndicator;

	private static int[] res = { R.drawable.mic_2, R.drawable.mic_3, R.drawable.mic_4, R.drawable.mic_5 };

	private static ImageView imageView;

	private MediaRecorder recorder;

	private ObtainDecibelThread thread;

	private Handler volumeHandler;

	private void init() {
		volumeHandler = new ShowVolumeHandler();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/*if (mFileName == null)
			return false;*/
		
		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//点击按钮触发的函数
			initDialogAndStartRecord();
			break;
		case MotionEvent.ACTION_UP:
			//放开按钮触发的函数
			finishRecord();
			break;
		case MotionEvent.ACTION_CANCEL://当手指移动到view外面，会cancel
			//手指移动到别处触发的函数
			cancelRecord();
			break;
		}

		return true;
	}

	private void initDialogAndStartRecord() {

		startTime = System.currentTimeMillis();
		recordIndicator = new Dialog(getContext(), R.style.like_toast_dialog_style);
		imageView = new ImageView(getContext());
		imageView.setImageResource(R.drawable.mic_2);
		mFileName = GetPath();
	    System.out.println("点击时mFileName值为：" + mFileName);
		recordIndicator.setContentView(imageView, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		
		recordIndicator.setOnDismissListener(onDismiss);
		LayoutParams lParams = recordIndicator.getWindow().getAttributes();
		lParams.gravity = Gravity.CENTER;
		startRecording();
		
		recordIndicator.show();
	}

	private void finishRecord() {
		stopRecording();
		recordIndicator.dismiss();

		long intervalTime = System.currentTimeMillis() - startTime;
		if (intervalTime < MIN_INTERVAL_TIME) {
			Toast.makeText(getContext(), "时间太短！",Toast.LENGTH_SHORT).show();
			File file = new File(mFileName);
			file.delete();
			return;
		}

		if (finishedListener != null)
			finishedListener.onFinishedRecord(mFileName);
              System.out.println("保存时的mFileName为：" + mFileName);	
 	}

	private void cancelRecord() {
		stopRecording();
		recordIndicator.dismiss();

		Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
		File file = new File(mFileName);
		file.delete();
	}

	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(mFileName);
		
		
		try {
			recorder.prepare();
		} catch (IOException e) {
			//e.printStackTrace();
			Log.v("录音文件路径", e.toString());
		}

		recorder.start();
		thread = new ObtainDecibelThread();
		thread.start();

	}

	private void stopRecording() {
		if (thread != null) {
			thread.exit();
			thread = null;
		}
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}

	private class ObtainDecibelThread extends Thread {

		private volatile boolean running = true;

		public void exit() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (recorder == null || !running) {
					break;
				}
				//int x = recorder.getAudioSourceMax();
				int x = recorder.getMaxAmplitude();
				Log.i("RECORD!!!", "音量： "	+ x);
				if (x != 0) {
					
					int f = (int) (10 * Math.log(x) / Math.log(10));
					if (f < 26)
						volumeHandler.sendEmptyMessage(0);
					else if (f < 32)
						volumeHandler.sendEmptyMessage(1);
					else if (f < 38)
						volumeHandler.sendEmptyMessage(2);
					else
						volumeHandler.sendEmptyMessage(3);

				}

			}
		}

	}

	private OnDismissListener onDismiss = new OnDismissListener() {

		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};

	static class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			imageView.setImageResource(res[msg.what]);
		}
	}

	public interface OnFinishedRecordListener {
		public void onFinishedRecord(String audioPath);
	}
	
	private String GetPath() {
		// TODO Auto-generated method stub
		Long l = System.currentTimeMillis();
		String fileName = l.toString();
		//后续对读取SD卡异常进行处理。
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".amr";
		return path;
	}

}
