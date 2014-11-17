package edu.pti.students.bem9.android.hwm.database;

import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_ASSIGNED;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_CLASS;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_DUE;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_ID;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_KEYWORDS;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_NAME;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._COL_SUBJECT;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._DELIM;
import static edu.pti.students.bem9.android.hwm.database.HomeworkSQLiteHelper._TABLE_NAME;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.pti.students.bem9.android.hwm.CodeResource;
import edu.pti.students.bem9.android.hwm.Homework;

/**
 * Datasource class used to allow access to the SQL database.  Sets up all
 * of the database properties, handles opening and closing the database,
 * and supplies multiple methods for adding {@link Homework} objects to the
 * database, removing saved {@code Homework} objects from the database, and
 * creating new instances of {@code Homework} from the database.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * @author Based off of work by Lars Vogel.
 * 
 * @version 1.0.2
 */
public class HomeworkDataSource {

	/** 
	 * The SQL database to access. 
	 */
	private SQLiteDatabase database = null;
	
	
	/** 
	 * The database helper to use to create and control the database. 
	 */
	private HomeworkSQLiteHelper dbHelper;
	
	
	/**
	 * A list of all columns of the database. 
	 */
	private String[] cols = {_COL_ID, _COL_DUE, _COL_ASSIGNED, _COL_CLASS,
			_COL_SUBJECT, _COL_NAME, _COL_KEYWORDS};
	
	
	/**
	 * Creates a new {@code HomeworkDatasource}.  This also instantiates a new
	 * {@link HomeworkSQLiteHelper} to use as the main database control.  The
	 * value of context is passed to the database helper.
	 * 
	 * @param context The current context, used in creating the SQLiteHelper.
	 */
	public HomeworkDataSource(Context context) 
	{
		Log.i(CodeResource.TAG_DATABASE, "Initializing database...");
		this.dbHelper = new HomeworkSQLiteHelper(context);
	}
	
	
	/**
	 * Opens the database for writing.  This simply gets the writable database from
	 * the {@link #dbHelper}.
	 * 
	 * @throws SQLException Thrown if the database fails to open.
	 */
	public void open() throws SQLException 
	{
		if(!this.isOpen()) 
		{
			Log.i(CodeResource.TAG_DATABASE, "Opening database for writing!");
			this.database = this.dbHelper.getWritableDatabase();
		} else
		{
			Log.w(CodeResource.TAG_DATABASE, "Warning: The database is already open!");
		}
	}
	
	
	/**
	 * Closes any open databases.
	 */
	public void close() 
	{
		Log.i(CodeResource.TAG_DATABASE, "Closing database.");
		this.dbHelper.close();
		this.database = null;
	}
	
	
	/**
	 * Checks if the  is currently open.
	 * 
	 * @return True if the database has been opened, false if not.
	 */
	public boolean isOpen() 
	{
		return this.database != null;
	}
	
	
	/**
	 * Creates a new {@link Homework} from the supplied fields.  This function first adds all values to the
	 * SQL database, automatically incrementing the UID column.  A new {@code Homework} object is then read from the
	 * current cursor position by use of the function {@link #getHWFromID(long)}.
	 * 
	 * @param name A {@link String} representing the name of the {@code Homework} to create.
	 * @param due A {@link java.sql.Date Date} representing the due date of the {@code Homework} to create.
	 * @param assign A {@code Date} representing the assigned date of the {@code Homework}.
	 * @param classname A {@code String} representing the assigning class's name.
	 * @param subject A {@code String} representing the name of the subject matter of the {@code Homework}.
	 * @param keywords A list of {@code String}s that will be added to the keyword list of the {@code Homework}.
	 * 				   Keywords are stored as a concatenated string, and individual keywords are delimited with
	 * 				   the delimiter stored as {@link HomeworkSQLiteHelper#_DELIM}.
	 * 
	 * @return A new homework, created from the database with an autoincremented UID.
	 */
	public Homework createHomework(String name, Date due, Date assign,
			String classname, String subject, String ... keywords) 
	{
			
		ContentValues cv = new ContentValues();
		cv.put(_COL_DUE, due.toString());
		cv.put(_COL_ASSIGNED, assign.toString());
		cv.put(_COL_CLASS, classname);
		cv.put(_COL_SUBJECT, subject);
		cv.put(_COL_NAME, name);
		
		StringBuilder keywordsConcat = new StringBuilder();
		
		for(int i = 0; i < keywords.length; i++) 
		{
			if(i > 0) keywordsConcat.append(_DELIM); //delimit with form feeds
			keywordsConcat.append(keywords[i]);
		}
		
		if(!keywordsConcat.toString().equals("")) 
		{
			cv.put(_COL_KEYWORDS, keywordsConcat.toString());
		}
		
		long id = this.database.insert(_TABLE_NAME, null, cv);
		
		Homework homework = this.getHWFromID(id);
		
		Log.d(CodeResource.TAG_DATABASE, "Created new homework object " + homework.getName()
				+ " with ID " + homework.getUID());
				
		return homework;
	}
	
	/**
	 * Edits an existing {@link Homework} item with the values from the given field.  This function
	 * first removes the {@code Homework} from the database, then adds a new {@code Homework} with
	 * the values specified (essentially this function simply combines the functionality of
	 * {@link #createHomework(String, Date, Date, String, String, String...)} and
	 * {@link #deleteHomework(Homework)}.
	 * 
	 * @param hw The homework to edit in the database.
	 * @param name The new name of the edited homework.
	 * @param due The new due date of the edited homework.
	 * @param assign The new assigned date of the edited homework.
	 * @param classname The new name of the edited homework.
	 * @param subject The new subject of the edited homework.
	 * @param keywords The new keywords for the edited homework.
	 * 
	 * @return The edited homework object, as re-retrieved from the database.
	 */
	public Homework editHomework(Homework hw, String name, Date due, Date assign,
			String classname, String subject, String ... keywords) 
	{
		Log.i(CodeResource.TAG_DATABASE, "Editing homework with ID " + hw.getUID());
		this.deleteHomework(hw);
		
		return this.createHomework(name, due, assign, classname, subject, keywords);
	}
	
	
	/**
	 * Returns a new {@link Homework} object with all values retrieved from the database
	 * where the ID column's value is equal to the supplied ID number via the use of the function
	 * {@link #retrieveHomework(Cursor)}.
	 * 
	 * @param id The UID of the homework one wishes to retrieve.
	 * 
	 * @return A new {@code Homework} object created from the values within the database where
	 *     the ID column is equal to the supplied ID. If the database finds that there is no
	 * 	   name stored for the homework at that position then this will return {@code null}.
	 */
	public Homework getHWFromID(long id) 
	{
		
		Cursor cursor = this.database.query(_TABLE_NAME, this.cols, _COL_ID + " = " + id, null, 
				null, null, null);
		
		cursor.moveToFirst();
		
		Homework homework = this.retrieveHomework(cursor);
		
		cursor.close();

		return homework;
		
	}
	
	
	/**
	 * Removes a {@link Homework} object from the database.
	 * 
	 * @param hw The {@code Homework} object you wish to remove.
	 *           If you have only the ID, retrieve the {@code Homework}
	 *           from the database first by using the {@link #getHWFromID(long)}
	 *           function.
	 */
	public void deleteHomework(Homework hw) 
	{
		
		long uid = hw.getUID();
		
		this.database.delete(_TABLE_NAME, _COL_ID + " = " + uid, null);
		Log.i(CodeResource.TAG_DATABASE, "Deleted homework with ID " + uid);
	}
	

	/**
	 * Gets a list of all {@link Homework} objects stored in the database.	
	 * 
	 * @return An {@link ArrayList} (sliced as a {@link List}) of all {@code Homework} objects stored in the database.
	 */
	public List<Homework> getAllHomework() 
	{
		
		Log.i(CodeResource.TAG_DATABASE, "Restoring from database.");
		
		List<Homework> hwlist = new ArrayList<Homework>();
		
		Cursor cursor = this.database.query(_TABLE_NAME, this.cols, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Homework hw = this.retrieveHomework(cursor);
			Log.d(CodeResource.TAG_DATABASE, "Found homework " + hw.getName() + " with ID " + hw.getUID());
			hwlist.add(hw);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return hwlist;
	}
	
	
	/**
	 * Obtains all information from the database pertaining to the {@link Homework}
	 * object stored at the current position of the supplied cursor.  The string stored
	 * at the sixth position (the keywords string) is split by the character specified
	 * by {@link HomeworkSQLiteHelper#_DELIM}. It then creates and returns the {@code Homework}
	 * object.
	 * 
	 * @param cursor The cursor to use to obtain the information stored in the database.
	 * 
	 * @return A new {@code Homework} object created with the information stored at the cursor's
	 * 		   position.
	 */
	private Homework retrieveHomework(Cursor cursor) 
	{
		long id = cursor.getLong(0);
		Date due = Date.valueOf(cursor.getString(1));
		Date assign = Date.valueOf(cursor.getString(2));
		String classname = cursor.getString(3);
		String subject = cursor.getString(4);
		String name = cursor.getString(5);
		String keywordsConcat = cursor.getString(6);
		
		String[] keywords = new String[]{};
		if(keywordsConcat != null) {
			keywords = keywordsConcat.split("" + _DELIM);
		}
		
		if(name.equals("")) return null;
		
		Homework _return = new Homework(name, due, assign, classname, subject, keywords);
		_return.setUID(id);
		
		return _return;
	}
}
