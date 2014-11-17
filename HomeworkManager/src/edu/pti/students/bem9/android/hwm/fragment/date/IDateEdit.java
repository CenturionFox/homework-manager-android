package edu.pti.students.bem9.android.hwm.fragment.date;

import java.sql.Date;

/**
 * Lays out the basic interactions between a {@link DateFragment} instance and the class
 * that called it (that class must implement IDateEdit).
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @version 1.0.0
 */
public interface IDateEdit
{
	/**
	 * Sets the field in this {@code IDateEdit} that should contain the returned
	 * value from the {@link DateFragment}.
	 * 
	 * @param date The date returned by the date fragment.
	 */
	void setDateData(Date date);
	
	/**
	 * Sets the type of execution that should be carried out at the end of the {@link DateFragment}'s
	 * lifecycle.  The functions that this bit defines are by the {@link #execute()} method. This could
	 * include a search, an insertion into a text field, et cetera.
	 * 
	 * @param b0 The function to execute as a byte. These should be defined in derived classes.
	 */
	void setExecuteBit(byte b0);
	
	/**
	 * Executes all functions that should be called at the end of the {@link DateFragment}'s
	 * lifecycle.
	 */
	void execute();
}
