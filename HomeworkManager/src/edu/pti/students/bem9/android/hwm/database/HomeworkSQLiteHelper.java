package edu.pti.students.bem9.android.hwm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.pti.students.bem9.android.hwm.CodeResource;

/**
 * Homework Minder SQL database helper.
 * Handles the creation and updating / downgrading of the SQL database.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @see #_DATABASE_CREATE Database creation field.
 * 
 * @version 1.0.1
 */
public class HomeworkSQLiteHelper extends SQLiteOpenHelper
{
	/**
	 * Constant name and version number of the database.
	 */
	private static final String _dbName = "edu.bem9.hwm.data";
	
	
	/**
	 * Constant version number of the database.
	 */
	private static final int _dbVer = 1;
	
	
	/**
	 *  Name of the main table
	 */
	protected static final String _TABLE_NAME = "homework_table";
	
	
	/** 
	 * Name of the ID column
	 */
	protected static final String _COL_ID = "id";
	
	
	/** 
	 * Name of the due date column 
	 */
	protected static final String _COL_DUE = "due";
	
	
	/**
	 * Name of the assigned date column
	 */
	protected static final String _COL_ASSIGNED = "assigned";
	
	
	/** 
	 * Name of the assigning class column
	 */
	protected static final String _COL_CLASS = "class";
	
	
	/** 
	 * Name of the subject column
	 */
	protected static final String _COL_SUBJECT = "subject";
	
	
	/** 
	 * Name of the name column 
	 */
	protected static final String _COL_NAME = "name";
	
	
	/**
	 *  Name of the keywords column
	 *  Keywords are delimited by the {@linkplain #_DELIM delimiter} character
	 */
	protected static final String _COL_KEYWORDS = "keywords";
	
	
	/**
	 * Character used to delimit keywords when they are stored in the database as a concatenated string.
	 */
	protected static final char _DELIM = '\f';
	
	
	/** 
	 * Construction information for the {@link #_COL_ID} column.
	 */
	private static final String _instId = " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT";
	
	
	/** 
	 * Construction information for the {@link #_COL_DUE} column. 
	 */
	private static final String _instDue = " DATE NOT NULL";
	
	
	/**
	 * Construction information for the {@link #_COL_ASSIGNED} column. 
	 */
	private static final String _instAssigned = " DATE NOT NULL";
	
	
	/** 
	 * Construction information for the {@link #_COL_CLASS} column.
	 */
	private static final String _instClass = " TEXT";
	
	
	/** 
	 * Construction information for the {@link #_COL_SUBJECT} column. 
	 */
	private static final String _instSubject = " TEXT";
	
	
	/** 
	 * Construction information for the {@link #_COL_NAME} column. 
	 */
	private static final String _instName = " TEXT NOT NULL";
	
	
	/** 
	 * Construction information for the {@link #_COL_KEYWORDS} column. 
	 */
	private static final String _instKeywords = " TEXT";
	
	
	/**
	 * SQL Database initialization statement.  Creates a new table with the following
	 * schema:
	 * <table border="1">
	 * <tr>
	 *   <th colspan='7'>{@linkplain #_TABLE_NAME Table Name}</th>
	 * </tr>
	 * <tr>
	 *   <td>{@linkplain #_COL_ID Integer <i>id</i>}</td>
	 *   <td>{@linkplain #_COL_DUE Date <i>due</i>}</td>
	 *   <td>{@linkplain #_COL_ASSIGNED Date <i>assigned</i>}</td>
	 *   <td>{@linkplain #_COL_CLASS Text <i>class</i>}</td>
	 *   <td>{@linkplain #_COL_SUBJECT Text <i>subject</i>}</td>
	 *   <td>{@linkplain #_COL_NAME Text <i>name</i>}</td>
	 *   <td>{@linkplain #_COL_KEYWORDS Text <i>keywords</i>}</td>
	 * </tr>
	 * <tr>
	 *   <td>Autoincrementing, unique primary key Integer that represents the ID of the  
	 *       {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} object.</td>
	 *   <td>Date representing the due date of the 
	 *       {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} object. (not null)</td>
	 *   <td>Date representing the assigned date 
	 *       of the {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} object. (not null)</td>
	 *   <td>Text that represents the name of the assigning class of the
	 *       {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} object.</td>
	 *   <td>Text that represents the name of the subject matter of the
	 *       {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} object.</td>
	 *   <td>Text that represents the name of the {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework} 
	 *       object. (not null)</td>
	 *   <td>Text that represents a list of all of the keywords related to the {@linkplain edu.pti.students.bem9.android.hwm.Homework Homework}
	 *       object.  Keywords are delimited with the form feed character. {@literal "\f"}</t>
	 * </tr>
	 * </table>
	 */
	protected static final String _DATABASE_CREATE = "CREATE TABLE " + _TABLE_NAME +
			"(" + _COL_ID + _instId + ", " + _COL_DUE + _instDue + ", " + _COL_ASSIGNED +
			_instAssigned + ", " + _COL_CLASS + _instClass + ", " + _COL_SUBJECT + 
			_instSubject + ", " + _COL_NAME + _instName + ", " + _COL_KEYWORDS + _instKeywords + ");";
 
	
	/**
	 * Creates a new HWM_SQLiteHelper. Calls the 
	 * {@link SQLiteOpenHelper#SQLiteOpenHelper(Context, String, android.database.sqlite.SQLiteDatabase.CursorFactory, int) super constructor}
	 * and passes the fields representing {@linkplain #_dbName the database name} and {@linkplain #_dbVer the database version}.
	 * 
	 * @param context The context in which to create the database.
	 */
	public HomeworkSQLiteHelper(Context context) 
	{
		super(context, HomeworkSQLiteHelper._dbName, null, HomeworkSQLiteHelper._dbVer);
	}
	
	
	/**
	 * Creates a new table {@linkplain #_TABLE_NAME} using the version schema.
	 * 
	 * @param database The database.
	 * 
	 * @see {@linkplain #_DATABASE_CREATE}
	 */
	public void onCreate(SQLiteDatabase database) 
	{
		Log.d(CodeResource.TAG_DATABASE, "EXEC: " + _DATABASE_CREATE);
		database.execSQL(_DATABASE_CREATE);
	}

	
	/**
	 * Handles upgrading of the database in case an older version exists.
	 * 
	 * @version 1.0.0 (Does nothing but drop the old table)
	 */
	public void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) 
	{
		database.execSQL("DROP TABLE IF EXISTS " + _TABLE_NAME + ";");
		this.onCreate(database);
	}

}
