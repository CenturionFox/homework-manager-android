package edu.pti.students.bem9.android.hwm.fragment.date;

import java.sql.Date;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import edu.pti.students.bem9.android.hwm.CodeResource;
import edu.pti.students.bem9.android.hwm.HomeworkManager;

/**
 * A multipurpose date selection fragment .
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * @version 1.0.0
 */
public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
	/**
	 * Constant value of the {@link Bundle} tag that the execution bit should be saved under.
	 */
	private static final String _exBit = "executionBit";
	
	
	/**
	 * constant value of the {@link Bundle} tag that the parent fragment ID should be saved under.
	 */
	private static final String _pFrag = "parentFrag";
	
	
	/**
	 * The tag of the parent fragment as specified by {@link CodeResource}
	 */
	private String parentFrag = CodeResource.TAG_CONTEXT;
	
	
	/**
	 * The execution bit to use for the {@link IDateEdit}.
	 */
	private byte executionBit = 0x0;
	
	
	/**
	 * Sets the parent fragment for this date picker. This makes sure functionality
	 * is preserved throughout the application lifecycle.
	 * 
	 * @param parent The name of the parent fragment, or "context" if the parent is
	 *     the activity.
	 * 
	 * @return This {@code DateFragment} object with the parent changed to equal the
	 *     given parent.
	 */
	public DateFragment setParentFrag(String parent) 
	{
		this.parentFrag = parent;
		return this;
	}
	
	
	/**
	 * Sets the execution bit for this date picker.  This number is used to determine
	 * what functionality the {@link IDateEdit#execute()} method should provide.
	 * 
	 * @param b0 The new execution bit.
	 * 
	 * @return This {@code DateFragment} object with the execution bit changed to
	 *     the new execution bit.
	 */
	public DateFragment setExecutionBit(byte b0)
	{
		this.executionBit = b0;
		return this;
	}
	
	
	/**
	 * Gets the parent of this object as an IDateEdit.
	 * 
	 * @return Returns the parent fragment for this {@code DateFragment}.
	 */
	public IDateEdit getParentFrag()
	{
		if(CodeResource.TAG_CONTEXT.equals(this.parentFrag))
		{
			return (IDateEdit)HomeworkManager.getContext();
		}
		
		return (IDateEdit)this.getFragmentManager().findFragmentByTag(this.parentFrag);
	}
	
	
	/**
	 * Adds the execution bit and parent fragment ID data to the saved instance state.
	 * 
	 * @param outData The bundle to which all fragment saved data is written to.
	 */
	@Override
	public void onSaveInstanceState(Bundle outData) 
	{
		super.onSaveInstanceState(outData);
		outData.putByte(_exBit, this.executionBit);
		outData.putString(_pFrag, this.parentFrag);
	}
	
	/**
	 * Executed when the dialog is shown.  Restores values from the {@code savedInstanceState} if
	 * applicable, creates a new {@link DatePickerDialog} and sets it to display the current date,
	 * and returns that dialog.
	 * 
	 * @param savedInstanceState A {@link Bundle} to which all data was saved in the {@link #onSaveInstanceState(Bundle)}
	 *     method.
	 *     
	 * @return A new {@link DatePickerDialog} set up to interact with instances of {@link IDateEdit}.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey(_exBit))
				this.executionBit = savedInstanceState.getByte(_exBit);
			if(savedInstanceState.containsKey(_pFrag)) 
				this.parentFrag = savedInstanceState.getString(_pFrag);
		}
		
		Calendar cal = Calendar.getInstance();
		
		DatePickerDialog dpDialog = new DatePickerDialog(this.getActivity(), this, 
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		dpDialog.setCancelable(false);
		return dpDialog;
	}
	
	/**
	 * Executed when the {@link DatePickerDialog} value is changed.
	 * 
	 * @param view A DatePicker that changed its date.
	 * @param year The year of the date picker.
	 * @param monthOfYear The month of the date picker. This is offset by 1 to correct
	 *     for a "start at 0"-type indexing.
	 * @param dayOfMonth The day of the month of the date picker.
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) 
	{
		IDateEdit parentFrag = this.getParentFrag();
		parentFrag.setExecuteBit(this.executionBit);
		parentFrag.setDateData(Date.valueOf(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		parentFrag.execute();
	}

}
