package edu.pti.students.bem9.android.hwm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import edu.pti.bem9.android.hwm.R;

/**
 * Creates a new view and inflates the homework removal dialog.
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * @version 1.0.0
 */
public class HomeworkRemovalFragment extends Fragment 
{
	/**
	 * Instantiates a new {@code HomeworkRemovalFragment}.
	 */
    public HomeworkRemovalFragment() { }

    
    /**
     * Called when this fragment is shown.  Sets the layout of the root view
     * to the homework removal fragment.  Also sets up the {@code removeOn} switch
     * to be true by default.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.hwm_layout_fragment_hremove, container, false);
        
        //Set up the switch to remove homework that matches the date to make it automatically checked by default.
        Switch removeOn = (Switch)rootView.findViewById(R.id.hremove_removal_type_on);
        removeOn.setChecked(true);
        
        return rootView;
    }    
}