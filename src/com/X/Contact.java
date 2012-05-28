package com.X;

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
			String [] details = s.split("^");
			if (details.length >1)
			{
				setContactName(details[0]);
				setContactDetails(details[1]);
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
