package edu.pti.students.bem9.android.hwm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import edu.pti.bem9.android.hwm.R;
import edu.pti.students.bem9.android.hwm.CodeResource;
import edu.pti.students.bem9.android.hwm.Homework;
import edu.pti.students.bem9.android.hwm.HomeworkManager;
import edu.pti.students.bem9.android.hwm.util.HomeworkListAdapter;

/**
 * A placeholder fragment containing a simple view.
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * @version 1.3.0
 */
public class HomeworkListFragment extends Fragment
{
	/**
	 * Creates a new {@code HomeworkListFragment}.
	 */
    public HomeworkListFragment() { }

    
    /**
     * Creates a new view for the container.  This view contains a {@link SearchView}
     * and a {@link ListView}.  The {@code SearchView} handles all list searches, while
     * the list view contains the actual list of homework as stored in the database.
     * @param inflater A {@link LayoutInflater} used in layout creation.
     * @param container The parent {@link ViewGroup} of the view.
     * @param savedInstanceState Any objects persisted between life cycles of the view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	//Inflates the main layout
        View rootView = inflater.inflate(R.layout.hwm_layout_fragment, container, false);
        
        //Set up the list view
        ListView mainHomeworkList = (ListView) rootView.findViewById(R.id.fragment_main_listlayout);
        HomeworkListAdapter adapter = new HomeworkListAdapter((HomeworkManager) this.getActivity());
        mainHomeworkList.setAdapter(adapter);
        
        //Set up the list view click functions
        mainHomeworkList.setOnItemClickListener(new ListView.OnItemClickListener() 
        {
        	/**
        	 * Displays a new {@link HomeworkInformationDialog} when a list tiem is pressed by the
        	 * user.
        	 * 
        	 * @param parent The parent {@link AdapterView}. This is always assumed to be an 
        	 *     {@code AdapterView<}{@link HomeworkListAdapter}{@code >} for simplicity.
        	 * @param view The {@link ListView}. (unused)
        	 * @param position The position in the {@link ListView} of the item that was clicked.
        	 * @param id unused
        	 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				HomeworkListAdapter adapter = (HomeworkListAdapter) parent.getAdapter();
				Homework hw = adapter.getItem(position);
				
				HomeworkInformationDialog dlg = new HomeworkInformationDialog().setHomeworkUID(hw);
				getActivity().getFragmentManager().beginTransaction().add(dlg, CodeResource.TAG_DIALOG_INFO).commit();
			}
		});
        
        //Set up the search view handlers.
        SearchView searchHandler = (SearchView) rootView.findViewById(R.id.fragment_main_search);

        //Set up the query text listener
        searchHandler.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
		   	/**
        	 * Searches through the {@linkplain HomeworkManager#hwList homework list} and repopulates it with
        	 * only {@link Homework} objects that match the search criteria.
        	 */
			@Override
			public boolean onQueryTextSubmit(String queryText) 
			{
				HomeworkManager hwm = (HomeworkManager) getActivity();
				hwm.searchHomeworkList(queryText);
				
				return true;
			}
			
			
			/**
			 * Resets the homework list to the values stored in the if the
			 * query text is empty.  Does not automatically search as the search operation 
			 * is too slow.
			 * @param queryText The keyphrase to search
			 */
			@Override
			public boolean onQueryTextChange(String queryText) 
			{
				if(queryText.isEmpty())
				{
					((HomeworkManager) getActivity()).resetHomeworkList();
				}
				
				return false;
			}
		});
        
        //Set up the close listener
        searchHandler.setOnCloseListener(new SearchView.OnCloseListener() 
        {
		   	/**
        	 * Resets the contents of the homework list to the values from the database.
        	 */
			@Override
			public boolean onClose() 
			{
				((HomeworkManager) getActivity()).resetHomeworkList();
				return true;
			}
		});
        
        //Set up the focus change listener
        searchHandler.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() 
        {
		   	/**
        	 * If this view no longer has focus, this will reset the contents of the homework list
        	 * to the values contained within the database.
        	 */
        	@Override
			public void onFocusChange(View v, boolean hasFocus) {
        		if(!hasFocus) {
        			((HomeworkManager) getActivity()).resetHomeworkList();
				}
			}
        });
        
        return rootView;
    }    
}