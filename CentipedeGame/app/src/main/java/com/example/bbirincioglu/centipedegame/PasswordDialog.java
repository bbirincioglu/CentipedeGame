package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PasswordDialog extends Dialog implements SimpleDialog {
	private Activity activity;
	
	public PasswordDialog(Context context) {
		super(context, android.R.style.Theme_Holo_Light_Dialog);
		setActivity((Activity) context);
		setCancelable(false);
		setTitle("PASSWORD REQUIRED");
	}
	
	public void initialize() {
		setContentView(R.layout.password_dialog);
		ButtonListener listener = new ButtonListener();
		findViewById(R.id.passwordDialogNextButton).setOnClickListener(listener);
		findViewById(R.id.passwordDialogPreviousButton).setOnClickListener(listener);
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public class ButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			int buttonID = v.getId();
			
			if (buttonID == R.id.passwordDialogNextButton) {
				EditText passwordEditText = (EditText) findViewById(R.id.passwordDialogEditText);
				String password = passwordEditText.getText().toString();
				boolean isCorrect = checkPassword(password);
				
				if (isCorrect) {
					cancel();
				} else {
					TextView errorMessageTextView = (TextView) findViewById(R.id.errorMessageTextView);
					errorMessageTextView.setText("Password is incorrect. Please try again.");
					errorMessageTextView.setVisibility(View.VISIBLE);
				}	
			} else if (buttonID == R.id.passwordDialogPreviousButton) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
				new ActivitySwitcher().fromPreviousToNext(getActivity(), MainMenuActivity.class, bundle, true);
			}
		}
	}
	
	private boolean checkPassword(String password) {
		boolean result = false;
		
		if (password.equals(Keys.PASSWORD_RESTRICTED_AREA)) {
			result = true;
		}
		
		return result;
	}
}
