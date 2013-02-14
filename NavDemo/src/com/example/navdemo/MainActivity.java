package com.example.navdemo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	
	private static final String TAG = "NavDemo";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String STATE_SELECTED_DIVISION_ITEM = "selected_division_item";
	
	private String[] sportsArray;
	private int selectedSport = 0;
	
	private boolean hasDivisions = false;
	private String[] divisionsArray;
	private int selectedDivision = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sportsArray = getResources().getStringArray(R.array.sports);
		divisionsArray = getResources().getStringArray(R.array.cfb_divisions);
		
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, sportsArray), this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			selectedSport = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			getActionBar().setSelectedNavigationItem(selectedSport);
		}
		if(savedInstanceState.containsKey(STATE_SELECTED_DIVISION_ITEM)){
			selectedDivision = savedInstanceState.getInt(STATE_SELECTED_DIVISION_ITEM);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, selectedSport);
		outState.putInt(STATE_SELECTED_DIVISION_ITEM,selectedDivision);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		if(hasDivisions)
		{
			menu.getItem(0).setVisible(true);
            SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu
            //only create the submenu once.
            if(!subm.hasVisibleItems())
            {
	            int divCount = divisionsArray.length;
	            for(int i=0; i<divCount; i++)
	                subm.add(0, i, i, divisionsArray[i]);
            }
		}
		else
		{
			menu.getItem(0).setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if(itemId < 10) //probably a division selection
		{
			selectedDivision = item.getOrder();
			//Log.v(TAG, "in onOptionsItemSelected: "+selectedDivision);
			createDummyFragment(divisionsArray[selectedDivision]);
		}
		if(itemId == R.id.menu_simulation)
		{
			Intent i = new Intent(MainActivity.this, SecondActivity.class);
			String title = "";
			if(hasDivisions)
				title = divisionsArray[selectedDivision];
			else
				title = sportsArray[selectedSport];
			i.putExtra("title", title);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		
		//check if the sport has divisions
		selectedSport = getActionBar().getSelectedNavigationIndex();
		boolean requiresDivisions = sportsArray[selectedSport].equalsIgnoreCase("cfb");
		if(hasDivisions != requiresDivisions)
		{
			//there is a change, necessary to update the menu.
			hasDivisions = requiresDivisions;
			invalidateOptionsMenu();
		}
		
		//if no divisions
		if(!hasDivisions)
		{
			//show text
			createDummyFragment(sportsArray[position]);
		}
		else
		{
			//default
			//Log.v(TAG, "in onNavigationItemSelected: "+selectedDivision);
			createDummyFragment(divisionsArray[selectedDivision]);
		}
		
		return true;
	}
	
	private void createDummyFragment(String text)
	{
		//Log.v(TAG, "createDummyFragment");
		if(isFinishing() || isRestricted())
		{
			//Log.v(TAG, "activity invalid...");
			return;
		}
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putString(DummySectionFragment.ARG_SECTION_NUMBER, text);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final int TEXT_ID = 1;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(30);
			textView.setId(TEXT_ID);
			
			String text = getArguments().getString(ARG_SECTION_NUMBER);
			textView.setText(text);
			
			return textView;
		}
	}

}
