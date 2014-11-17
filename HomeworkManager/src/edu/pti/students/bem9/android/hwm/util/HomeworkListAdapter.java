package edu.pti.students.bem9.android.hwm.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.pti.bem9.android.hwm.R;
import edu.pti.students.bem9.android.hwm.Homework;
import edu.pti.students.bem9.android.hwm.HomeworkManager;

/**
 * Converts an array list of homework objects into a human readable output of them
 * by binding a hwm_layout_list layout to each list item and setting the fields within
 * to the different components that make up the definition of a homework object.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * @version 1.0.0
 */
public class HomeworkListAdapter extends BaseAdapter 
{
	/**
	 * The layout inflater to use.
	 */
	private LayoutInflater inflater;
	
	
	/**
	 * The context of this list adapter as a {@link HomeworkManager}.
	 */
	private HomeworkManager mContext;
	
	
	/**
	 * The current objects contained within this list.
	 */
	private ArrayList<Homework> mObjects;
	
	
	/**
	 * Object used for thread synchronization.
	 */
	private final Object syncLock = new Object();
	
	
	/**
	 * Creates a new {@code HomeworkListAdapter} with a reference to the
	 * supplied {@link HomeworkManager}.
	 * 
	 * @param context
	 */
	public HomeworkListAdapter(HomeworkManager context)
	{
		this.mContext = context;
		this.inflater = this.mContext.getLayoutInflater();
		this.mObjects = new ArrayList<Homework>();
	}

	
    /**
     * Creates a new view for displaying {@linkplain Homework assignments}.
     * Displayed values include the assignment {@linkplain Homework#getName() name},
     * the {@linkplain Homework#getDueDate() due date}, the {@linkplain Homework#getAssignedDate()
     * assigned date}, and any {@linkplain Homework#getKeywords() keywords} associated with the
     * assignment.  Normally, assignments are listed with a white name, but if they are due or overdue,
     * the name field color changes.  Due assignments have an orange name, while overdue ones show
     * a red name.
     * 
     * @param position used to get the current homework item.
     * @param convertView View to convert into the view specified by this function.
     * @param parent The parent ViewGroup for this view.
     * 
     * @return A new view for the list.
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
    	View v = convertView;
    	
    	//Inflate the list view layout
    	if(v == null) 
    	{
    		v = this.inflater.inflate(R.layout.hwm_layout_list, parent, false);
    	}
    	
    	//Get the homework item at the current position.
    	Homework hw = this.getItem(position);
    	
    	if(hw != null) 
    	{
    		//Get all fields in the layout that must be changed.
    		TextView nameField = (TextView) v.findViewById(R.id.listview_label_name);
    		TextView dueField = (TextView) v.findViewById(R.id.listview_label_due);
    		TextView assignedField = (TextView) v.findViewById(R.id.listview_label_assigned);
    		TextView keywordField = (TextView) v.findViewById(R.id.listview_label_keywords);
    		
    		//Get the current date for due date comparison.
    		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    		Date todaysDate = Date.valueOf(format.format(new java.util.Date()));
    		
    		//Set due comparison values.
    		boolean isDue = hw.isDueOn(todaysDate);
    		boolean isOverdue = hw.isDueBefore(todaysDate);
    		
    		//Set name, due, and assigned fields.
    		nameField.setText(hw.getName());
    		
    		dueField.setText(new StringBuilder().append(v.getContext().getText(R.string.list_prefix_due_date))
    				.append(hw.getDueDate().toString()));
    		
    		assignedField.setText(new StringBuilder().append(v.getContext().getText(R.string.list_prefix_assign_date))
    				.append(hw.getAssignedDate().toString()));
    		
    		if(isDue) //Due assignments get an orange name
    		{
    			nameField.setTextColor(0xFFDD9A23);
    		}
    		else if(isOverdue) //Overdue assignments get a red name
    		{
    			nameField.setTextColor(Color.RED);
    		}
    		else //Non-due assignments get a white name.
    		{
    			nameField.setTextColor(HomeworkManager.getContext().getResources().getColor(android.R.color.primary_text_dark));
    		}
    		
    		//Populate the keywords field.
    		keywordField.setText(v.getContext().getText(R.string.list_prefix_keywords));
    		
    		if(hw.getKeywords().isEmpty()) 
    		{
    			keywordField.append(v.getContext().getText(R.string.list_label_keywords_empty));
    		} 
    		else
    		{
	    		for(int i = 0; i < hw.getKeywords().size(); i++) 
	    		{
	    			if(i > 0) keywordField.append(", ");
	    			keywordField.append(hw.getKeywords().get(i).toString().trim());
	    		}
    		}
    		
    		//Schedule redraw I guess.
    		nameField.invalidate();
    		dueField.invalidate();
    		assignedField.invalidate();
    	}
    	
    	return v;
    }

	/**
	 * Gets the number of items currently contained in this {@code HomeworkListAdapter}.
	 * 
	 * @return Tne number of items in this {@code HomeworkListAdapter}.
	 */
	@Override
	public int getCount() 
	{
		return this.mObjects.size();
	}
	
	
	/**
	 * Gets an item at the specified position.
	 * 
	 * @return The instance of {@link Homework} that is present at the given position.
	 */
	@Override
	public Homework getItem(int position) 
	{
		return this.mObjects.get(position);
	}

	
	/**
	 * Gets the ID number of an item at the specified position.
	 * 
	 * @return The {@linkplain Homework#getUID() UID} of the {@link Homework} at the
	 *     current position.
	 */
	@Override
	public long getItemId(int position) 
	{
		return this.getItem(position).getUID();
	}
	
	/**
	 * Adds the entire contents of the given collection to the list.
	 * 
	 * @param objs Collection to push to the stack.
	 */
	public void addAll(Collection<? extends Homework> objs) 
	{
		synchronized(this.syncLock) 
		{
			this.mObjects.addAll(objs);
		}
		
		this.notifyDataSetChanged();
	}
	
	/**
	 * Clears all data from the list.
	 */
	public void clear() 
	{
		synchronized(this.syncLock) 
		{
			this.mObjects.clear();
		}
		
		this.notifyDataSetChanged();
	}
}
