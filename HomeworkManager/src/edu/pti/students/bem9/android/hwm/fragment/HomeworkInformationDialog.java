package edu.pti.students.bem9.android.hwm.fragment;

import java.sql.Date;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.pti.bem9.android.hwm.R;
import edu.pti.students.bem9.android.hwm.Homework;
import edu.pti.students.bem9.android.hwm.HomeworkManager;
import edu.pti.students.bem9.android.hwm.fragment.date.IDateEdit;

/**
 * A dialog that displays all 
 * @author bem9
 *
 */
public class HomeworkInformationDialog extends DialogFragment implements IDateEdit
{
	/**
	 * A string representing the value of the tag that the {@linkplain #homeworkUID persisted homework ID}
	 * is saved into the bundle with.
	 */
	private static final String _hwID = "homeworkUID";
	
	
	/**
	 * The unique ID of the homework to edit. Persisted throughout
	 * the lifecycle of this dialog.
	 */
	private long homeworkUID;
	
	
	/**
	 * Determines whqat type of action should be performed when a {@linkplain DateFragment date picker}
	 * completes an action on this object.
	 * <table border="1">
	 * <tr>
	 *   <th>Value of Execution Bit</th>
	 *   <th>Action Performed</th>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_DUE_SET 0x0}</td>
	 *   <td>Set the due date.</td>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_ASS_SET 0x1}</td>
	 *   <td>Set the assigned date.</td>
	 * </tr>
	 * </table>
	 */
	private byte executionBit = 0;
	
	/** Execution bit definition to set the due date field. */
	public static final byte EXECUTE_DUE_SET = 0x0;
	
	/** Execution bit definition to set the assigned date field. */
	public static final byte EXECUTE_ASS_SET = 0x1;
	
	
	/**
	 * Reference for the date field that will be edited to match the
	 * date selected by the date picker.
	 */
	private Date referenceDate;
	
	
	/**
	 * The root view of the dialog.
	 */
	private View mainView;
	
	
	/**
	 * Instantiates a new {@code HomeworkInformationDialog}.
	 */
	public HomeworkInformationDialog() { }
	
	
	/**
	 * Saves the state of the {@code HomeworkInformationDialog}.  This stores the value of
	 * the {@link #homeworkUID} under the tag {@link #_hwID}.
	 * 
	 * @param outData The {@link Bundle} to save all of the dialog data to.
	 */
	public void onSaveInstanceState(Bundle outData) 
	{
		super.onSaveInstanceState(outData);
		outData.putLong(_hwID, this.homeworkUID);
	}
	
	
	/**
	 * Sets the value of the dialog's homework UID. This is most efficiently called directly after
	 * the constructor.
	 * 
	 * @param hw The homework to match the UID to.
	 * @return This instance, with the UID updated.
	 */
	public HomeworkInformationDialog setHomeworkUID(Homework hw) 
	{
		this.homeworkUID = hw.getUID();
		return this;
	}
	
	
	/**
	 * Creates a new dialog with {@link AlertDialogBuilder} that can display and edit
	 * all of the information in a saved {@link Homework} instance. Also repopulates the
	 * {@linkplain #homeworkUID persisted ID field} if it is saved in the passed bundle.
	 * 
	 * @param savedInstanceState The {@link Bundle} to restore all saved data from.
	 */
	@SuppressLint( "InflateParams" )
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		//Restore data from the saved instance state
		if(savedInstanceState != null) 
		{
			if(savedInstanceState.containsKey(_hwID)) 
			{
				this.homeworkUID = savedInstanceState.getLong(_hwID);
			}
		}
		
		//Retrieve the Homework object from the database.
		Homework homework = ((HomeworkManager) this.getActivity()).getDataSource().getHWFromID(this.homeworkUID);
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this.getActivity());
		
		//Inflate the main layout and set the main view.
		LayoutInflater linf = this.getActivity().getLayoutInflater();
		this.mainView = linf.inflate(R.layout.hwm_layout_fragment_info, null);
		adb.setView(this.mainView);
		
		//Setup basic dialog
		adb.setTitle(this.getString(R.string.hinfo_dialog_title) + " \"" + homework.getName() + "\"")
		   .setNegativeButton(android.R.string.cancel, this.cancel)
		   .setNeutralButton(R.string.app_delete, this.delete)
		   .setPositiveButton(R.string.app_edit, this.edit);
		
		//Retrieve information fields and set the text to information gleaned from the homework
		((EditText) this.mainView.findViewById(R.id.hinfo_name_section_edit)).setText(homework.getName());
		((EditText) this.mainView.findViewById(R.id.hinfo_class_section_edit)).setText(homework.getAssigningClass());
		((EditText) this.mainView.findViewById(R.id.hinfo_subject_section_edit)).setText(homework.getSubject());
		EditText dueDate = (EditText) this.mainView.findViewById(R.id.hinfo_due_section_edit);
		EditText assignedDate = (EditText) this.mainView.findViewById(R.id.hinfo_assigned_section_edit);
		EditText keywords = (EditText) this.mainView.findViewById(R.id.hinfo_main_keywords_edit);
		
		//Reformat due date into dd/MM/yyyy from yyyy-MM-dd
		String[] dueDateFormat = homework.getDueDate().toString().split("-");
		String[] assignedDateFormat = homework.getAssignedDate().toString().split("-");
		
		dueDate.setText(dueDateFormat[2] + "/" + dueDateFormat[1] + "/" + dueDateFormat[0]);
		assignedDate.setText(assignedDateFormat[2] + "/" + assignedDateFormat[1] + "/" + assignedDateFormat[0]);
		
		//Display keywords as they were created.
		for(int i = 0; i < homework.getKeywords().size(); i++)
		{
			if(i > 0) keywords.append("; ");
			keywords.append(homework.getKeywords().get(i));
		}
		
		return adb.create();
	}
	
	/**
	 * Handles the negative button clicks. Simply closes the dialog.
	 */
	private DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() 
	{
		
		/**
		 * Cancels the dialog when the button is clicked.
		 * @param dialog The dialog which housed the button
		 * @param The button that was clicked or the position of the item clicked. (unused)
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			dialog.cancel();
		}
	};
	
	
	/**
	 * Handles positive button presses. Changes the saved values of the homework in the database. 
	 */
	private DialogInterface.OnClickListener edit = new DialogInterface.OnClickListener() 
	{
		/**
		 * Edits the homework instance in the database.
		 * @param dialog The dialog which housed the button
		 * @param which The button that was clicked or the position of the item clicked. (unused)
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			//Get the activity to use the datasource of.
			HomeworkManager hwm = (HomeworkManager) getActivity();
			
			//Get all fields from the main view
			EditText name = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_name_section_edit);
			EditText className = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_class_section_edit);
			EditText subject = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_subject_section_edit);
			EditText dueDate = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_due_section_edit);
			EditText assignedDate = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_assigned_section_edit);
			EditText keywords = (EditText) HomeworkInformationDialog.this.mainView.findViewById(R.id.hinfo_main_keywords_edit);
			
			//Retrieve the homework to edit from the datasource
			Homework hw = hwm.getDataSource().getHWFromID(HomeworkInformationDialog.this.homeworkUID);
			
			//Check for empty date / name
	    	String cannotComplete = hwm.getString(R.string.toast_message_hinfo_failure) + "\n";
	    	
	    	if(name.getText().toString().isEmpty() || dueDate.getText().toString().isEmpty() || assignedDate.getText().toString().isEmpty()) //Uh-oh, key information is missing.
	    	{
	    		if(name.getText().toString().isEmpty())
	    		{
	    			cannotComplete += hwm.getString(R.string.toast_message_hcreate_failure_name);
	    		}
	    		if(dueDate.getText().toString().isEmpty()) 
	    		{
	    			cannotComplete += hwm.getString(R.string.toast_message_hcreate_failure_due_unset);
	    		} 
	    		if(assignedDate.getText().toString().isEmpty())
	    		{
	    			cannotComplete += hwm.getString(R.string.toast_message_hcreate_failure_assigned_unset);
	    		}
	    		
	    		Toast.makeText(hwm, cannotComplete, Toast.LENGTH_LONG).show();
	    		return;
	    	}
			
			//Split the date for conversion.
			String[] dueDateFormatter = dueDate.getText().toString().split("[/-]");
			String[] assignedDateFormatter = assignedDate.getText().toString().split("[/-]");
			
	    	if(dueDateFormatter.length != 3 || assignedDateFormatter.length != 3) //Incorrectly formatted date fields!
	    	{
	    		if(dueDateFormatter.length != 3) cannotComplete += hwm.getString(R.string.toast_message_hcreate_failure_due_format); else
	    		if(assignedDateFormatter.length != 3) cannotComplete += hwm.getString(R.string.toast_message_hcreate_failure_assigned_format);
	    		
	    		Toast.makeText(hwm, cannotComplete, Toast.LENGTH_LONG).show();
	    		return;
	    	}
			
	    	//Split keywords
			String[] keysSplit = keywords.getText().toString().split(";");
			
			//Execute the edit command.
			hwm.getDataSource().editHomework(hw, name.getText().toString(), 
					Date.valueOf(dueDateFormatter[2] + "-" + dueDateFormatter[1] + "-" + dueDateFormatter[0]),
					Date.valueOf(assignedDateFormatter[2] + "-" + assignedDateFormatter[1] + "-" + assignedDateFormatter[0]),
					className.getText().toString(), subject.getText().toString(), keysSplit);
			
			//Reset the list and dismiss the dialog.
			hwm.resetHomeworkList();
			
			dialog.dismiss();
		}
	};
	
	
	/**
	 * Handles neutral button presses.  Deletes the edited homework.
	 */
	private DialogInterface.OnClickListener delete = new DialogInterface.OnClickListener() 
	{
		/**
		 * Deletes the homework from the database.
		 * @param dialog The dialog which housed the button
		 * @param which The button that was clicked or the position of the item clicked.  (unused)
		 */
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			HomeworkManager hwm = (HomeworkManager) getActivity();
			
			Homework hw = hwm.getDataSource().getHWFromID(HomeworkInformationDialog.this.homeworkUID);
			hwm.getDataSource().deleteHomework(hw);
			
			hwm.resetHomeworkList();
			dialog.dismiss();
		}
	};

	
	/**
	 * Sets the reference date for the {@linkplain edu.pti.students.bem9.android.hwm.fragment.date.DateFragment date picker}.
	 * @param date The date returned by the date fragment.
	 */
	@Override
	public void setDateData(Date date) 
	{
		this.referenceDate = date;
	}

	
	/**
	 * Sets this {@link IDateEdit}'s {@linkplain #executionBit execution bit}.
	 * @see #executionBit For execution bit functions, values, and the like.
	 * @param b0 The function to execute as a byte.
	 */
	@Override
	public void setExecuteBit(byte b0) 
	{
		this.executionBit = b0;
	}

	
	/**
	 * Executes the a function defined by {@linkplain #executionBit the execution bit} when a linked 
	 * {@link edu.pti.students.bem9.android.hwm.fragment.date.DateFragment DateFragment}'s
	 * lifecycle ends.
	 * @see #executionBit For the valid execution bit states and their functions.
	 */
	@Override
	public void execute()
	{
		EditText edit = null;
		String[] dateSplit = this.referenceDate.toString().split("-");
		String dateFormat = dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0];
		
		switch(this.executionBit)
		{
		case EXECUTE_DUE_SET:
			edit = (EditText) this.mainView.findViewById(R.id.hinfo_due_section_edit);
			break;
			
		case EXECUTE_ASS_SET:
			edit = (EditText) this.mainView.findViewById(R.id.hinfo_assigned_section_edit);
			break;
		}
		
		edit.setText(dateFormat);
	}
}
