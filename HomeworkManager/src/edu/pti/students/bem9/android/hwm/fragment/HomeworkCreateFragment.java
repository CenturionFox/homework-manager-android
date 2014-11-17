package edu.pti.students.bem9.android.hwm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.pti.bem9.android.hwm.R;

/**
 * Creates a new view and inflates the homework creation layout.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @version 1.0.0
 */
public class HomeworkCreateFragment extends Fragment {

	/**
	 * Instantiates a new {@code HomeworkCreateFragment}.
	 */
    public HomeworkCreateFragment() { }

    /**
     * Called when this fragment is shown.  Sets the layout of the root view
     * to the homework creation fragment.
     * 
     * @param inflater The inflater with which to inflate the layout.
     * @param container The main container of the fragment.
     * @param savedInstanceState A {@link Bundle} containing all persisted values.
     * 
     * @return The root view of the fragment, inflated by the inflater.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hwm_layout_fragment_hcreate, container, false);
        
        return rootView;
    }    
}