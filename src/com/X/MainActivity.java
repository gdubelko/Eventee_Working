package com.X;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
/**
 * CSE694 Project
 * @authors Greg Dubelko, Eric Gottschalk, Jason Monroe, Todd Simmons
 * An application that uses NFC to exchange business information
 */
public class MainActivity extends ListActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback
{
    private ProgressDialog m_ProgressDialog = null;
    protected static ArrayList<Contact> m_contacts = null;
    protected static ContactAdapter m_adapter;
    private Runnable viewContacts;
    NfcAdapter mNfcAdapter;
    private static final int MESSAGE_SENT = 1;
    TextView otherStringDisplay;
    Button saveMe, saveThem;
    EditText name, network, email, phone;
    String myString, otherString;
    
    HashMap<String, String> contactMap = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        m_contacts = new ArrayList<Contact>();
        TabHost appTabs = (TabHost) findViewById (R.id.tabhost);

        //EditTexts(inputs)
        name = (EditText) findViewById(R.id.Input_Name);
        network = (EditText) findViewById(R.id.Input_Network);
        email = (EditText) findViewById(R.id.Input_Email);
        phone = (EditText) findViewById(R.id.Input_PhoneNumber);

       otherStringDisplay = (TextView) findViewById(R.id.beamString);
        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        //BUTTON(SaveMe)::Saves users information into one string::
        saveMe = (Button) findViewById(R.id.Button_SaveMe);
        saveMe.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                myString = name.getText() + "^" + network.getText() + "^" + email.getText() + "^" + phone.getText();
                Toast.makeText(getApplicationContext(), myString, Toast.LENGTH_LONG).show();  //Simple test----Remove Later..maybe
                contactMap.put(myString, "itWorked");
                Contact myself = new Contact();
                myself.setContactName(name.getText()+"");
                myself.setContactDetails(network.getText()+"");
                m_contacts.add(myself);
                m_adapter.add(myself);
                m_adapter.notifyDataSetChanged();
            }
        }); 
        saveThem = (Button) findViewById(R.id.Button_saveOtherString);
        saveThem.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Contact them;
				try 
				{
					them = new Contact(otherString);
					m_contacts.add(them);
	                m_adapter.add(them);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
                m_adapter.notifyDataSetChanged();
            }
        });
        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        //TABS::::::::::::::::::::::::::::::
        appTabs.setup();
        //tab1: Exchange
        TabSpec specs = appTabs.newTabSpec("tag1");
        specs.setContent(R.id.Exchange);
        specs.setIndicator("Exchange");
        appTabs.addTab(specs);
        //tab2: Contacts
        specs = appTabs.newTabSpec("tag2");
        specs.setContent(R.id.Output_Contacts);
        specs.setIndicator("Contacts");
        appTabs.addTab(specs);
        //tab3: Me(profile)
        specs = appTabs.newTabSpec("tag3");
        specs.setContent(R.id.Me);
        specs.setIndicator("Me");
        appTabs.addTab(specs);
        
        m_contacts = new ArrayList<Contact>();
        MainActivity.m_adapter = new ContactAdapter(this, R.layout.row, m_contacts);
        setListAdapter(MainActivity.m_adapter);
        
        appTabs.setOnTabChangedListener(new OnTabChangeListener() 
        {
        	public void onTabChanged(String tabId) 
        	{
        		m_adapter.notifyDataSetChanged();
        	}
        	
        });
        viewContacts = new Runnable()
        {
            public void run()
            {
                getContacts();
            }
        };
        Thread thread =  new Thread(null, viewContacts, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Retrieving data ...", true);
     // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null)
        {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback to set NDEF Message and Register callback to listen for message-sent success
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);       
    }//oncreate end
    private Runnable returnRes = new Runnable()
    {
        public void run()
        {
            if(m_contacts != null && m_contacts.size() > 0)
            {
                m_adapter.notifyDataSetChanged();
                for(int i=0; i<m_contacts.size(); i++) m_adapter.add(m_contacts.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
    
    private void getContacts()
    {
        try
        {
            m_contacts = new ArrayList<Contact>();
            
            Set<String> keys = contactMap.keySet();
    		Iterator<String> it = keys.iterator();
    		while(it.hasNext())
    		{
    			String key = it.next();
    			Contact c1 = new Contact();
                c1.setContactName(key);
                c1.setContactDetails(contactMap.get(key));

                m_contacts.add(c1);
    		}
            

            Thread.sleep(1000);
            Log.i("ARRAY", ""+ m_contacts.size());
            
        }
        catch (Exception e)
        {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }
        runOnUiThread(returnRes);
    }
    protected class ContactAdapter extends ArrayAdapter<Contact>
    {
        private ArrayList<Contact> items;

        public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items)
        {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup group)
        {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Contact c = items.get(index);
            if (c != null)
            {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null)
                {
                    tt.setText(c.getContactName());
                }
                if(bt != null)
                {
                    bt.setText(c.getContactDetails());
                }
            }
            return v;
        }
    }
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        NdefMessage msg = new NdefMessage(new NdefRecord[]
        {
            createMimeRecord("application/com.Eventee", myString.getBytes())
            /**
             * The Android Application Record (AAR) is commented out. When a device
             * receives a push with an AAR in it, the application specified in the AAR
             * is guaranteed to run. The AAR overrides the tag dispatch system.
             * You can add it back in to guarantee that this
             * activity starts when receiving a beamed message. For now, this code
             * uses the tag dispatch system.
             */
            //,NdefRecord.createApplicationRecord("com.Eventee.BeamActivity")
        });
        return msg;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and sets value to otherString variable
     */
    void processIntent(Intent intent)
    {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        // record 0 contains the MIME type, record 1 is the AAR, if present
        String contactDetails = new String(msg.getRecords()[0].getPayload());
        Contact newContact;
        try {
			newContact = new Contact(contactDetails);
			m_contacts.add(newContact);
			m_adapter.add(newContact);
	        m_adapter.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("66", "malformed or incorrect string");
			//don't add it to the listview, but show an error state contact.
			newContact = new Contact();
		} 
		otherStringDisplay.setText(newContact.getContactName() + "\n" + newContact.getContactDetails());
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload)
    {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
    public void onNdefPushComplete(NfcEvent event)
    {
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

    }
    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Contact sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
}

