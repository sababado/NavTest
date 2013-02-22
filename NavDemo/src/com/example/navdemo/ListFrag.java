package com.example.navdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sababado.content.SearchableList;
import com.sababado.support.v4.app.SearchableSupportListFragment;
import com.sababado.widget.FilterableBaseAdapter;
/**
 * This fragment currently looks best in landscape mode, and with translate the same.
 * The scoreboard will be easier to see with more "natural" space (landscape)
 *
 */
public class ListFrag extends SearchableSupportListFragment {
	private static final String TAG = "ListFrag";

	//list of teams
	private String[] teams;
	//link to parent activity
	private ListFragHandler listFragHandler;
	//list adapter
	private BaseAdapter mAdapter;
	//for use in simple adapter
	private List<HashMap<String, String>> aList;
	//for use in custom adapter
	private ArrayList<String> teamsList;

	@Override
	public void onAttach(Activity activity) {
		Log.v(TAG, "onAttach");
		try {
			listFragHandler = (ListFragHandler) activity;
		} catch (ClassCastException e) {
			throw new RuntimeException(
					"ListFrag's Activity must implement ListFragHandler");
		}
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		teams = getResources().getStringArray(R.array.team_dummies);

		teamsList = new ArrayList<String>();
		// Each row in the list stores country name, currency and flag
		aList = new ArrayList<HashMap<String, String>>();

		//prepare the list of teams for use in an adapter.
		for (int i = 0; i < teams.length; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			//for simple adapter
			hm.put("team", teams[i]);
			aList.add(hm);
			//for custom adapter
			teamsList.add(teams[i]);
		}
		// Simple Adapter
		/*
		 * Uncomment to use simple adapter implementation.
		 * This does NOT require the custom adapter implementation below
		 */
		//mAdapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
		//		android.R.layout.simple_list_item_1, new String[] { "team" },
		//		new int[] { android.R.id.text1 });

		// Custom Adapter
		/*
		 * Comment this and uncomment the line above to use the simple adapter.
		 */
		ArrayList<?> list = getSavedListData();
		Log.v(TAG, "saved list? :"+list);
		/*
		 * Saved listdata will be null as of now because the ArrayList of teams is of Strings, not a Parcelable type.
		 */
		mAdapter = new ListFragAdapter(this, teamsList);
		/*
		 * If teams are unknown at the start, the adapter can be initialized with a null list parameter.
		 * When data is availble, use mAdapter.setListData(data) and then call mAdapter.notifyDataSetChanged();
		 */

		setListAdapter(mAdapter);


		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.v(TAG, "onViewCreated");
		view.setBackgroundColor(getResources().getColor(
				android.R.color.holo_blue_bright));
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (teams != null) {
			//use mAdapter to get the current item. This is useful in case the list is filtered.
			listFragHandler.onListItemClick(getId(), (String)mAdapter.getItem(position));
		}
	}

	interface ListFragHandler {
		public void onListItemClick(int fragId, String selection);
	}

	class ListFragAdapter extends FilterableBaseAdapter {

		public <T> ListFragAdapter(SearchableList searchableListFragment,
				ArrayList<T> listData) {
			super(searchableListFragment, listData);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				row = inflater.inflate(android.R.layout.simple_list_item_1,
						parent, false);
			}

			((TextView) row.findViewById(android.R.id.text1))
					.setText((String) getItem(position));
			return row;
		}

		@Override
		public ArrayList<?> performFiltering(ArrayList<?> listData, CharSequence constraint) {
			int listCount = listData.size();
			Log.v(TAG, "listCount: "+listCount+" / "+constraint);
			
			//check for data containing constraint
			String lowerConstraint = constraint.toString().toLowerCase();
			for (int i = 0; i < listCount; i++) {
				String text = (String) (listData.get(i));
				if (!text.toLowerCase().contains(lowerConstraint)) {
					listData.remove(i);
					i--;
					listCount--;
				}
			}
			return listData;
		}
	}
}
