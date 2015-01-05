package com.cardan.lab4_chatandroid.fragments;

import com.cardan.lab4_chatandroid.R;
import com.cardan.lab4_chatandroid.R.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Rooms extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_rooms, container,false);
		return rootView;
	}
}
