package edu.pti.students.bem9.android.hwm;

import java.sql.Date;
import java.util.Vector;

/**
 * Implementation of a representation of a homework object.<br>
 * 
 * This class also contains storage for a name representation of the Homework object, 
 * due and assigned date storage, subject and "assigning class" storage, and keyword 
 * storage and processing.  
 *     
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @version 2.0.0
 */
public class Homework 
{
	/**
	 * A Unique Identifier, used to both differentiate two homework objects from one another
	 * and in the equivalence function.
	 * 
	 * SQL Database column "id" equates to this field.
	 * <br><br>SQL: id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT
	 */
	private long UID = -1;
	
	
	/**
	 * Object used for thread synchronization lock.
	 */
	private final Object lock = new Object();
	
	
	/**
	 * A character sequence representation of the date at which this homework is due.
	 * 
	 * Stored in the SQL Database column "due".
	 * <br><br>SQL: due DATE NOT NULL
	 */
	private CharSequence dueDate;
	
	
	/**
	 * A character sequence representation of the date at which this homework is assigned.
	 * 
	 * Stored in the SQL Database column "assigned".<br><br>
	 * SQL: assigned DATE NOT NULL
	 */
	private CharSequence assignedDate;
	
	
	/**
	 * A character sequence representation of the class for which this homework belongs to.
	 * 
	 * Stored in the SQL Database column "class". Can be null.
	 * <br><br>SQL: class TEXT
	 */
	private CharSequence assigningClass;
	
	
	/**
	 * A character sequence representation of the subject for which this homework falls under.
	 * 
	 * Stored in the SQL Database column "subject". Can be null.
	 * <br><br>SQL: subject TEXT
	 */
	private CharSequence subject;
	
	
	/**
	 * Character sequence identifier for this homework.
	 * 
	 * Stored in the SQL Database column "name".
	 * <br><br>SQL: name TEXT NOT NULL
	 */
	private CharSequence name;
	
	
	/**
	 * A vector used for storing all keywords in this Homework object.
	 * Synchronized on each update. Stored in the SQL column "keywords".<br><br>
	 * SQL: keywords TEXT (delimited by {@link edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper#_DELIM _DELIM})
	 */
	private Vector<CharSequence> keywords = new Vector<CharSequence>(4);
	
	
	/**
	 * Instantiates a homework object with the specified name.
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 */
	public Homework(CharSequence name) 
	{
		this.name = name;
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, and assigned date.<br>
	 * 
	 * This is a second-level constructor, and it calls the lower level constructor
	 * {@linkplain Homework#Homework(CharSequence)}. Note that SQL date formatting
	 * conventions must be followed in the date fields.
	 *     
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A char sequence representing the due date. Note that this sequence must
	 * 			be formatted in SQL date format (yyyy-MM-dd).
	 * @param assign A char sequence representing the assigned date. Note that this sequence
	 * 			must be formatted in SQL date format (yyyy-MM-dd).
	 */
	public Homework(CharSequence name, CharSequence due, CharSequence assign) 
	{
		this(name);
		this.setDueDate(due);
		this.setAssignedDate(assign);
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, and assigned date.<br>
	 * 
	 * This is a third level constructor, and it calls the lower level constructor 
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence)}.  This constructor 
	 * could be considered "safer", as it ensures that the format of the resulting due and
	 * assigned char sequences will be in SQL format, thereby avoiding most potential
	 * date parsing exceptions.
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A {@linkplain java.sql.Date} object representing the due date of the homework.
	 * @param assign A {@linkplain java.sql.Date} object representing the assigned date of the homework.
	 */
	public Homework(CharSequence name, Date due, Date assign) 
	{
		this(name, due.toString(), assign.toString());
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, and class name.<br>
	 * 
	 * This is a third level constructor, and it calls the lower level constructor 
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence)}. Note that SQL date formatting
	 * conventions must be followed in the date fields.
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A char sequence representing the due date. Note that this sequence must
	 * 			be formatted in SQL date format (yyyy-MM-dd).
	 * @param assign A char sequence representing the assigned date. Note that this sequence
	 * 			must be formatted in SQL date format (yyyy-MM-dd).
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 */
	public Homework(CharSequence name, CharSequence due, CharSequence assigned, CharSequence classname) 
	{
		this(name, due, assigned);
		this.setAssigningClass(classname);
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, and class name.<br>
	 * 
	 * This is a fourth level constructor, and it calls the lower level constructor 
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence, CharSequence)}.  This constructor 
	 * could be considered "safer", as it ensures that the format of the resulting due and
	 * assigned char sequences will be in SQL format, thereby avoiding most potential
	 * date parsing exceptions. 
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A {@linkplain java.sql.Date} object representing the due date of the homework.
	 * @param assign A {@linkplain java.sql.Date} object representing the assigned date of the homework.
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 */
	public Homework(CharSequence name, Date due, Date assigned, CharSequence classname) 
	{
		this(name, due.toString(), assigned.toString(), classname);
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, class name, and
	 * subject name.<br>
	 * 
	 * This is a fourth level constructor, and it calls the lower level constructor 
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence, CharSequence)}.  Note that 
	 * SQL date formatting conventions must be followed in the date fields.
	 *     
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A char sequence representing the due date. Note that this sequence must
	 * 			be formatted in SQL date format (yyyy-MM-dd).
	 * @param assign A char sequence representing the assigned date. Note that this sequence
	 * 			must be formatted in SQL date format (yyyy-MM-dd).
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 * @param subject A char sequence representing the subject name of this homework.
	 */
	public Homework(CharSequence name, CharSequence due, CharSequence assigned, CharSequence classname, CharSequence subject) 
	{
		this(name, due, assigned, classname);
		this.subject = subject;
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, class name, and
	 * subject name.<br>
	 * 
	 * This is a fifth level constructor, and it calls the lower level constructor
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence, CharSequence)}.  This constructor 
	 * could be considered "safer", as it ensures that the format of the resulting due and
	 * assigned char sequences will be in SQL format, thereby avoiding most potential
	 * date parsing exceptions. 
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A {@linkplain java.sql.Date} object representing the due date of the homework.
	 * @param assign A {@linkplain java.sql.Date} object representing the assigned date of the homework.
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 * @param subject A char sequence representing the subject name of this homework.
	 */
	public Homework(CharSequence name, Date due, Date assigned, CharSequence classname, CharSequence subject) 
	{
		this(name, due.toString(), assigned.toString(), classname, subject);
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, class name, and
	 * subject name.  Also adds all specified keywords.  The adding function is synchronized across threads. <br>
	 * 
	 * This is a fifth level constructor, and it calls the lower level constructor
	 * {@linkplain #Homework(CharSequence, CharSequence, CharSequence, CharSequence)}. Note that 
	 * SQL date formatting conventions must be followed in the date fields.
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A char sequence representing the due date. Note that this sequence must
	 * 			be formatted in SQL date format (yyyy-MM-dd).
	 * @param assign A char sequence representing the assigned date. Note that this sequence
	 * 			must be formatted in SQL date format (yyyy-MM-dd).
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 * @param subject A char sequence representing the subject name of this homework.
	 * @param keywords A list of char sequences that are used to further identify this homework object.
	 */
	public Homework(CharSequence name, CharSequence due, CharSequence assigned, CharSequence classname, 
			CharSequence subject, CharSequence ... keywords) 
	{
		this(name, due, assigned, classname, subject);
		this.addKeyword(keywords);
	}
	
	
	/**
	 * Instantiates a homework object with the specified name, due date, assigned date, class name, and
	 * subject name.  Also adds all specified keywords.  The adding function is synchronized across threads. <br>
	 *     
	 * This is the sixth and top level constructor in the Homework class.  All lower constructors are invoked when
	 * using this constructor.  Due to this, initial construction time may be slower when using this.  Implicity
	 * invokes {@linkplain #Homework(CharSequence, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence...)}.
	 * This constructor could be considered "safer" than the invoked one, as it ensures that the format of the resulting due and
	 * assigned char sequences will be in SQL format, thereby avoiding most potential date parsing exceptions.
	 * 
	 * @param name A char sequence representing the name of this homework object.
	 * @param due A {@linkplain java.sql.Date} object representing the due date of the homework.
	 * @param assign A {@linkplain java.sql.Date} object representing the assigned date of the homework.
	 * @param classname A char sequence representing the name of the class that assigned this homework object.
	 * @param subject A char sequence representing the subject name of this homework.
	 * @param keywords A list of char sequences that are used to further identify this homework object.
	 */
	public Homework(CharSequence name, Date due, Date assigned, CharSequence classname, 
			CharSequence subject, CharSequence ... keywords) 
	{
		this(name, due.toString(), assigned.toString(), classname, subject, keywords);
	}
	
	
	/**
	 * Manually sets the "assigned" date field to a date represented by the specified
	 * char sequence.  Note that SQL date formatting conventions must be followed
	 * when setting the date in this manner.
	 * 
	 * @param assign A char sequence representing the assigned date. Note that this sequence
	 * 	   must be formatted in SQL date format (yyyy-MM-dd).
	 */
	public void setAssignedDate(CharSequence assign) 
	{ 
		this.assignedDate = assign;
	}
	
	
	/**
	 * Manually sets the "due" date field to a date represented by the specified
	 * char sequence.  Note that SQL date formatting conventions must be followed
	 * when setting the date in this manner.
	 *     
	 * @param due A char sequence representing the due date. Note that this sequence
	 * 	   must be formatted in SQL date format (yyyy-MM-dd).
	 */
	public void setDueDate(CharSequence due) 
	{ 
		this.dueDate = due; 
	}
	
	
	/**
	 * Manually sets the "assigned" date field to the specified date.  Calls the function
	 * {@link #setAssignedDate(CharSequence)} after converting the given date to a
	 * string.  This helps prevent date parsing errors by ensuring SQL compliant formatting
	 * of the date string.
	 * 
	 * @param assign A {@linkplain java.sql.Date} object representing the assigned date of the homework.
	 */
	public void setAssignedDate(Date date) 
	{ 
		this.setAssignedDate(date.toString());
	}
	
	
	/**
	 * Manually sets the "due" date field to the specified date.  Calls the function
	 * {@link #setDueDate(CharSequence)} after converting the given date to a
	 * string.  This helps prevent date parsing errors by ensuring SQL compliant formatting
	 * of the date string.
	 * 
	 * @param assign  A {@linkplain java.sql.Date} object representing the due date of the homework.
	 */
	public void setDueDate(Date date) 
	{ 
		this.setDueDate(date.toString()); 
	}

	
	/**
	 * Allows access to this homework's due date field as a {@linkplain java.sql.Date} object.
	 * 
	 * @return A {@code java.sql.Date} object representing the parsed value of this homework's
	 *     due date.
	 */
	public Date getDueDate() 
	{ 
		return Date.valueOf(this.dueDate.toString()); 
	}
	
	
	/**
	 * Allows access to this homework's assigned date field as a {@linkplain java.sql.Date} object.
	 * 
	 * @return A {@code java.sql.Date} object representing the parsed value of this homework's
	 *     assigned date.
	 */
	public Date getAssignedDate() 
	{ 
		return Date.valueOf(this.assignedDate.toString());
	}
	
	
	/**
	 * Allows access of this homework object's unique identification number.
	 * 
	 * @return The UID of this homework object.
	 */
	public long getUID() 
	{ 
		return this.UID;
	}
	
	
	/**
	 * Sets this homework object's unique identification number.
	 * 
	 * @param uid The number to use as this homework's UID.
	 */
	public void setUID(long uid) 
	{ 
		this.UID = uid; 
	}
	
	
	/**
	 * Manually sets the assigning class name of the homework.
	 * 
	 * @param className The new name to set for the homework's assigning class.
	 */
	public void setAssigningClass(CharSequence className) 
	{ 
		this.assigningClass = className;
	}
	
	
	/**
	 * Manually sets the subject name for the homework.
	 * 
	 * @param subjectName the new name to set for the homework's subject.
	 */
	public void setSubject(CharSequence subjectName) 
	{ 
		this.subject = subjectName;
	}
	
	
	/**
	 * Manually sets the name of this homework.
	 * 
	 * @param name The new name for this homework object.
	 */
	public void setName(CharSequence name) 
	{ 
		this.name = name;
	}
	
	
	/**
	 * Allows access to this homework's assigning class.
	 * 
	 * @return This homework's assigning class.
	 */
	public CharSequence getAssigningClass()
	{
		return this.assigningClass; 
	}
	
	
	/**
	 * Allows access to this homework's subject.
	 * 
	 * @return This homework's subject.
	 */
	public CharSequence getSubject() 
	{ 
		return this.subject;
	}
	
	
	/**
	 * Allows access to this homework's name.
	 * 
	 * @return This homework's name.
	 */
	public CharSequence getName() 
	{ 
		return this.name; 
	}
	
	
	/**
	 * Allows access to this homework's keywords.
	 * 
	 * @return The keyword vector for this object.
	 */
	public Vector<CharSequence> getKeywords() 
	{ 
		return this.keywords; 
	}
	
	
	/**
	 * Searches this homework's identifying fields for any identifiers containing the specified
	 * sequence of characters.  If one is found, the function will return true. Otherwise,
	 * the function returns false.
	 *     
	 * @param keyfragm The sequence to look for in the identifying fields.
	 * 
	 * @return True if the sequence can be located, false if not.
	 */
	public boolean _keywordSearch(CharSequence keyfragm) 
	{
		keyfragm = keyfragm.toString().toLowerCase();
		String[] akeyfragm = keyfragm.toString().split("[ ,;\\.]");
		
		boolean contains = false;
		
		for(String keystring : akeyfragm)
		{
			keystring = keystring.trim();
			
			if(keystring.isEmpty()) continue;
			
			contains |= this.name != null && this.name.toString().toLowerCase().contains(keystring);
			
			contains |= this.assigningClass != null && this.assigningClass.toString().toLowerCase().contains(keystring);

			contains |= this.subject != null && this.subject.toString().toLowerCase().contains(keystring);

			contains |= this.dueDate != null && this.dueDate.toString().toLowerCase().contains(keystring);

			contains |= this.assignedDate != null && this.assignedDate.toString().toLowerCase().contains(keystring);
			
			for(int i = 0; this.keywords != null && i < this.keywords.size(); i++) 
			{
				CharSequence s = this.keywords.get(i);
				contains |= s != null && s.toString().contains(keystring);
			}
		}
		
		StringBuilder debugText = new StringBuilder(this.getName() + ": " + contains + " keyphrases:");
		
		for(String s : akeyfragm) 
		{ 
			debugText.append(s).append("_"); 
		}
		
		return contains;
	}
	
	
	/**
	 * Adds the specified keyword to the keywords list. This function synchronizes across threads.
	 * 
	 * @param keys The keywords to add.
	 */
	public void addKeyword(CharSequence ... keys)
	{
		synchronized(this.lock)
		{
			for(CharSequence cs : keys) 
			{
				if(!this.keywords.contains(cs.toString().trim().toLowerCase()))
				{
					this.keywords.add(cs.toString().trim().toLowerCase());
				}
			}
		}
	}
	
	
	/**
	 * Empties the current keyword list and repopulates with the supplied keywords.
	 * This function synchronizes across threads.
	 * 
	 * @param keys the keywords to add.
	 */
	public void replaceKeywords(CharSequence ... keys)
	{
		synchronized(this.lock) 
		{
			this.keywords.clear();
		}
		
		this.addKeyword(keys);
	}
	
	
	/**
	 * Checks equivalence of two homework objects. Returns true if the UIDs are equivalent.  
	 * 
	 * @param compare Another object to compare this {@code Homework} to.
	 */
	@Override
	public boolean equals(Object compare) 
	{
		if(compare instanceof Homework) 
		{
			Homework comparehw = (Homework) compare;
			if(comparehw.UID == this.UID) return true;
		}
		
		return super.equals(compare);
	}
	
	
	/**
	 * Compares this homework to a given date. If this homework is due on that date,
	 * this function will return true.
	 *     
	 * @return True if this homework is due on the given date, false if it is not.
	 */
	public boolean isDueOn(Date d) 
	{
		return this.getDueDate().compareTo(d) == 0;
	}
	
	
	/**
	 * Compares this homework to a given date. If this homework is due before or on
	 * that date, this function will return true.
	 *     
	 * @return true if the homework is due on or before the given date, false if it
	 *     is not.
	 */
	public boolean isDueOnBefore(Date d)
	{
		return this.getDueDate().compareTo(d) <= 0;
	}
	
	
	/**
	 * Compares this homework to a given date. If this homework is due before
	 * that date, this function will return true.
	 * 
	 * @return true if the homework is due before the given date, false if it
	 *     is not.
	 */
	public boolean isDueBefore(Date d)
	{
		return this.getDueDate().compareTo(d) < 0;
	}
	
	
	/**
	 * Compares this homework to the given date.  If this homework is due on or after
	 * that date, this function will return true.
	 * 
	 * @return true if the homework is due on or after the given date, false if it is
	 *     not.
	 */
	public boolean isDueOnAfter(Date d)
	{
		return this.getDueDate().compareTo(d) >= 0;
	}
	
	
	/**
	 * Compares this homework to a given date. If this homework is due after
	 * that date, this function will return true.
	 * 
	 * @return true if the homework is due after the given date, false if it
	 *     is not.
	 */
	public boolean isDueAfter(Date d)
	{
		return this.getDueDate().compareTo(d) > 0;
	}
}
