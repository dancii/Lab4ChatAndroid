package com.cardan.lab4_chatandroid.fragments;

import java.util.ArrayList;

import com.cardan.lab4_chatandroid.ChatWindowActivity;
import com.cardan.lab4_chatandroid.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActiveChats extends Fragment {
	
	private ArrayList<String> listItems=new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	ListView list=null;
	private String contactChatId = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_active_chats, container,false);
		list = (ListView) rootView.findViewById(R.id.listConvos);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listItems);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),ChatWindowActivity.class);
				i.putExtra("contactName", listItems.get(arg2));
				i.putExtra("contactChatId", contactChatId);
				startActivity(i);
			}
		});
		return rootView;
	}
	
	public void setContact(String contact, String contactChatId){
		listItems.add(contact);
		this.contactChatId=contactChatId;
		adapter.notifyDataSetChanged();
	}
}
