package edu.pti.students.bem9.android.hwm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import edu.pti.bem9.android.hwm.R;

/**
 * {@link DialogFragment} that contains the information to create a new "About the Author" 
 * {@link AlertDialog}.  This dialog displays a short blurb of information regarding
 * the author of this program, the program version, and other pertinent information.
 * The dialog also displays a button allowing the user to rate the application.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @version 3.0.1
 */
public class CommonAboutDialog extends DialogFragment 
{
	/**
	 * Instantiates a new {@code CommonAboutDialog}
	 */
	public CommonAboutDialog() { }
	
	
	/**
	 * Creates a new alert dialog that will display application information such as the author,
	 * the current version, and other pertinent information.  Also includes a "Rate App"
	 * button to bring a user to the play store entry.
	 * 
	 * @param savedInstanceState A {@link Bundle} containing any persisted values.
	 * 
	 * @return A new alert dialog containing application information.
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		adb.setMessage(this.getString(R.string.app_desc) + " " + this.getString(R.string.app_version))
		   .setNegativeButton(getString(R.string.app_return), new DialogInterface.OnClickListener() {
			   
			   /**
			    * Dismisses the dialog when the button is clicked.
			    * 
			    * @param dialog The dialog which housed the button
			    * @param which The button that was clicked or the position of the item clicked. (unused)
			    */
			   public void onClick(DialogInterface dialog, int which) {
				   dialog.dismiss();					
			   }
		   })
		   .setPositiveButton(getString(R.string.app_rate), new DialogInterface.OnClickListener() {
			   
			   /**
				 * When the button is clicked, this method executes a new intent that will start the play store
				 * and direct the user to the entry where this program resides.  This allows the user to submit
				 * feedback on the app at any time.
				 * 
				 * @param dialog The dialog which housed the button
				 * @param which The button that was clicked or the position of the item clicked. (unused)
				 */
			   public void onClick(DialogInterface dialog, int which) 
			   {
					getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getString(R.string.app_package))));
					dialog.dismiss();
			   }
		   })
		   .setTitle(getString(R.string.app_info))
		   .setIcon(getResources().getDrawable(R.drawable.ic_about));
		
		return adb.create();
	}
	
}

