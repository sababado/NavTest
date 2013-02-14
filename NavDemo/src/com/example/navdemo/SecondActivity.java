package com.example.navdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.navdemo.ListFrag.ListFragHandler;

public class SecondActivity extends FragmentActivity implements ListFragHandler{
	
	private static final String TAG = "SecondActivity";
	//save the state of the selected text.
	private String mSavedTextLeft;
	private String mSavedTextRight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sim_layout);
		
		getActionBar().setTitle(getIntent().getStringExtra("title"));
		
		findViewById(R.id.side1).setOnClickListener(sideClickListener);
		findViewById(R.id.side2).setOnClickListener(sideClickListener);
		
		boolean showLeft = true;
		boolean showRight = true;
		if(savedInstanceState != null)
		{
			showLeft = savedInstanceState.getBoolean("left");
			showRight = savedInstanceState.getBoolean("right");
			
			//get left text
			mSavedTextLeft = savedInstanceState.getString("leftStr");
			if(mSavedTextLeft != null)
				((TextView)findViewById(R.id.side1)).setText(mSavedTextLeft);
			//get right text
			mSavedTextRight = savedInstanceState.getString("rightStr");
			if(mSavedTextRight != null)
				((TextView)findViewById(R.id.side2)).setText(mSavedTextRight);
		}
		animateLeftSide(showLeft, false);
		animateRightSide(showRight,false);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//save if a fragment is hidden or not so it can be reopened.
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.side1_frag);
		outState.putBoolean("left", frag.isHidden());
		frag = getSupportFragmentManager().findFragmentById(R.id.side2_frag);
		outState.putBoolean("right", frag.isHidden());
		
		//save text
		outState.putString("leftStr", mSavedTextLeft);
		outState.putString("rightStr", mSavedTextRight);
	}
	
	/**
	 * Click listener for the team select text
	 */
	private OnClickListener sideClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.side1)
			{
				//Make sure the other side is closed
				getSupportFragmentManager().popBackStack();
				animateLeftSide(false, true);
			}
			else if(v.getId() == R.id.side2)
			{
				//Make sure the other side is closed
				getSupportFragmentManager().popBackStack();
				animateRightSide(false, true);
			}
		}
	};

	@Override
	public void onListItemClick(int fragId, String selection)
	{
		if(fragId == R.id.side1_frag) //left side
		{
			((TextView)findViewById(R.id.side1)).setText(selection);
			mSavedTextLeft = selection;
			animateLeftSide(true, true);
		}
		else if(fragId == R.id.side2_frag) //right side
		{
			((TextView)findViewById(R.id.side2)).setText(selection);
			mSavedTextRight = selection;
			animateRightSide(true, true);
		}
	};
	
	/**
	 * Animate the left side fragment. Either hide or show with or without a custom animation
	 * @param hide True to hide the fragment, false to show it.
	 * @param withCustomAnimation True to show custom animation, false to act normal.
	 */
	private void animateLeftSide(boolean hide, boolean withCustomAnimation)
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.side1_frag);
		// coming in, selected and leaving, ????, exit
		if(withCustomAnimation)
			ft.setCustomAnimations(R.anim.slide_in_left_no_fade, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left_no_fade);
		if(hide)
		{
			ft.hide(frag);
		}
		else
		{
			ft.show(frag);
			ft.addToBackStack("left_side");
		}
		ft.commit();
	}
	
	/**
	 * Animate the right side fragment. Either hide or show with or without a custom animation
	 * @param hide True to hide the fragment, false to show it.
	 * @param withCustomAnimation True to show custom animation, false to act normal.
	 */
	private void animateRightSide(boolean hide, boolean withCustomAnimation)
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment frag  = getSupportFragmentManager().findFragmentById(R.id.side2_frag);
		ft.setCustomAnimations(R.anim.slide_in_right_no_fade, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right_no_fade);
		if(hide)
		{
			ft.hide(frag);
		}
		else
		{
			ft.show(frag);
			ft.addToBackStack("right_side");
		}
		ft.commit();
	}

}
