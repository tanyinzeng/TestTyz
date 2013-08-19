package com.example.constants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class AlertUtil {

	/**
	 * Show Alert Dialog
	 * 
	 * @param context
	 * @param titleId
	 * @param messageId
	 * @param positiveButtontxt
	 * @param positiveListener
	 * @param negativeButtontxt
	 * @param negativeListener
	 */
	public static void showAlert(Context context, int titleId,
			String messageId, CharSequence positiveButtontxt,
			DialogInterface.OnClickListener positiveListener,
			CharSequence negativeButtontxt,
			DialogInterface.OnClickListener negativeListener) {
		Dialog dlg = new AlertDialog.Builder(context).setTitle(titleId)
				.setMessage(messageId)
				.setPositiveButton(positiveButtontxt, positiveListener)
				.setNegativeButton(negativeButtontxt, negativeListener)
				.setCancelable(false).create();

		dlg.show();
	}

	public static void showAlert(Context context, int titleId,
			String messageId, CharSequence positiveButtontxt,
			DialogInterface.OnClickListener positiveListener) {
		Dialog dlg = new AlertDialog.Builder(context).setTitle(titleId)
				.setMessage(messageId)
				.setPositiveButton(positiveButtontxt, positiveListener)
				.setCancelable(false).create();

		dlg.show();
	}

	private static ProgressDialog progressDialog;
	private static AlertDialog alertDialog;

	public static void showProgress(Context ctx) {
		try {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(ctx);
			}

			progressDialog.setTitle("提示");
			progressDialog.setMessage("正在发送请求…");
			progressDialog.setCancelable(true);
			progressDialog.show();

		} catch (Exception e) {
			System.out.println("showProgress  e " + e.toString());
		}
	}

	public static void disProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public static void showDialog(Context ctx, String title) {
		try {
			if (alertDialog == null) {
				AlertDialog.Builder builder = new Builder(ctx);
				builder.setMessage(title);

				builder.create();
				builder.show();

			}
		} catch (Exception e) {
			System.out.println("showDialog  e " + e.toString());
		}

	}

}
