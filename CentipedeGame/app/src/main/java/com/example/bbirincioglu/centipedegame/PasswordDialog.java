package com.example.bbirincioglu.centipedegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
/*
	Dialog class for displaying password entrance. This dialog is used to restrict some parts of the application which need some password. GameResultsActivity
	is a part of the application that requires password. This class receives the password and checks whether it is correct or not.
 */
public class PasswordDialog extends Dialog implements SimpleDialog {
	private Activity activity;
	
	public PasswordDialog(Context context) {
		super(context, android.R.style.Theme_Holo_Light_Dialog);
		setActivity((Activity) context);
		setCancelable(false);
		setTitle("PASSWORD REQUIRED");
	}

	public void initialize() {
		setContentView(R.layout.password_dialog); //Insert dialog layout from xml.
		ButtonListener listener = new ButtonListener();
		findViewById(R.id.passwordDialogNextButton).setOnClickListener(listener); // bind button listeners.
		findViewById(R.id.passwordDialogPreviousButton).setOnClickListener(listener); // bind button listeners.
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
			
			if (buttonID == R.id.passwordDialogNextButton) { // If next button is clicked, checks whether password is correct or not. If not correct display error message.
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
			} else if (buttonID == R.id.passwordDialogPreviousButton) { //If previous button is clicked, go back to main menu.
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
