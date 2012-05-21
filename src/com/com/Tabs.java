package com.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

/**
 * Corresponds to Main.xml
 */
public class Tabs extends Activity
{
	Button exchange, saveMe;
	EditText name, network, email, phone;
	String myString = null;
	
	//ExpandableListView lv;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost appTabs = (TabHost) findViewById (R.id.tabhost);
        
        //lv = (ExpandableListView) this.findViewById(R.id.Output_Contacts);
        //MyExpandableListAdapter expandableAdapter = new MyExpandableListAdapter();
        //lv.setAdapter(expandableAdapter);
        
        //BUTTON(Exchange)::Runs Beam processes::
        exchange = (Button) findViewById(R.id.Button_Exchange);
        exchange.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View view) 
			{
				Intent myIntent = new Intent(view.getContext(), BeamActivity.class);
				myIntent.putExtra("myString", myString);
                startActivityForResult(myIntent, 0);
			}
		});
        
        
        //EditTexts instantiated
        name = (EditText) findViewById(R.id.Input_Name);
        network = (EditText) findViewById(R.id.Input_Network);
        email = (EditText) findViewById(R.id.Input_Email);
        phone = (EditText) findViewById(R.id.Input_PhoneNumber);
        
        
        
        //BUTTON(SaveMe)::Saves users information into one string::
        saveMe = (Button) findViewById(R.id.Button_SaveMe);
        saveMe.setOnClickListener(new View.OnClickListener() 
        {
			
			public void onClick(View view) 
			{
				myString = name.getText() + "^" + network.getText() + "^" + email.getText() + "^" + phone.getText();
				Toast.makeText(getApplicationContext(), myString, Toast.LENGTH_LONG).show();  //Simple test----Remove Later..maybe
				
				
			}
		});
        		
        

        appTabs.setup();
        //tab1: Exchange
        TabSpec specs = appTabs.newTabSpec("tag1");
        specs.setContent(R.id.Exchange);
        specs.setIndicator("Exchange");
        appTabs.addTab(specs);
        //tab2: Contacts
        specs = appTabs.newTabSpec("tag2");
        specs.setContent(R.id.Contacts);
        specs.setIndicator("Contacts");
        appTabs.addTab(specs);
        //tab3: Me(profile)
        specs = appTabs.newTabSpec("tag3");
        specs.setContent(R.id.Me);
        specs.setIndicator("Me");
        appTabs.addTab(specs);
 
    }
    /**   :::::Work in progress::::
    class MyExpandableListAdapter extends BaseExpandableListAdapter 
    {

        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = {};
        private String[][] members = {};
        
        
		public Object getChild(int groupIndex, int memberIndex) { return members[groupIndex][memberIndex]; }
		
		public long getChildId(int groupIndex, int memberIndex) { return memberIndex; }
		
		public View getChildView(int groupIndex, int memberIndex, boolean isLastMember, View convertView, ViewGroup group) 
		{
			TextView textView = getGenericView();
            textView.setText(getChild(groupIndex, memberIndex).toString());
            return textView;
		}
		
		public int getChildrenCount(int groupIndex) { return members[groupIndex].length; }
		
		public Object getGroup(int groupIndex) { return groups[groupIndex]; }
		
		public int getGroupCount() { return groups.length; }
		
		public long getGroupId(int groupIndex) { return groupIndex; }
		
		public View getGroupView(int groupIndex, boolean isExpanded, View convertView, ViewGroup group) 
		{
			TextView textView = getGenericView();
            textView.setText(getGroup(groupIndex).toString());
            return textView;
		}
		
		public boolean hasStableIds() { return true; }
		
		public boolean isChildSelectable(int groupIndex, int memberIndex) { return false; }

    }
    **/
}