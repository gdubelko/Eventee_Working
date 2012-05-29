package com.X;

import android.util.Log;

public class Contact 
{
	private String name;
	private String details;
	
	public Contact(String s) throws Exception 
	{
		if (s.length() == 0 || s == null)
		{
			throw new Exception("Failed to pass a correct string.");
		}
		else 
		{
			String [] temp = s.split("\\^");
			Log.i("2", temp[0]+" & "+temp[1]);
			if (temp.length >1)
			{
				this.name = temp[0] + "";
				this.details = temp[1]+"/"+temp[2]+"/"+temp[3];
			} 
			else 
			{
				throw new Exception("Malformed string constructor.");
			}
		}
	}
	
	public Contact() 
	{
		this.name = "";
		this.details = "";
	}

	public String getContactName()
	{
		return this.name;
	}

	public String getContactDetails() 
	{
		return this.details;
	}

	public void setContactName(String name) 
	{
		this.name = name;
	}

	public void setContactDetails(String details) 
	{
		this.details = details;
	}
}
