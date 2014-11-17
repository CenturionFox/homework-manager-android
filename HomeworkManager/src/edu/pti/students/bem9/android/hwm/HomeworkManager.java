package edu.pti.students.bem9.android.hwm;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import edu.pti.bem9.android.hwm.R;
import edu.pti.students.bem9.android.hwm.database.HomeworkDataSource;
import edu.pti.students.bem9.android.hwm.fragment.CommonAboutDialog;
import edu.pti.students.bem9.android.hwm.fragment.HomeworkCreateFragment;
import edu.pti.students.bem9.android.hwm.fragment.HomeworkInformationDialog;
import edu.pti.students.bem9.android.hwm.fragment.HomeworkListFragment;
import edu.pti.students.bem9.android.hwm.fragment.HomeworkRemovalFragment;
import edu.pti.students.bem9.android.hwm.fragment.date.DateFragment;
import edu.pti.students.bem9.android.hwm.fragment.date.IDateEdit;
import edu.pti.students.bem9.android.hwm.util.HomeworkListAdapter;

/**
 * The main activity of the Homework Manager program.  Handles click events,
 * dialog displays, fragment management, and database initialization.
 * Essentially, this class forms the backbone of the entire program.
 * 
 * @author Bridger Maskrey (bem9@students.pti.edu)
 * 
 * @version 2.2.0
 */
public class HomeworkManager extends FragmentActivity implements IDateEdit
{
	/**
	 * The maximum number of pages that should be present in the main
	 * pager view.
	 */
	private static final int NUM_PAGES = 3;
	
	
	/**
	 * Constant reference to the main page index of the pager
	 * (the homework list page)
	 */
	private static final int PRIMARY_PAGE = 1;
	
	
	/**
	 * Constant reference to the removal page index of the pager.
	 * (Homework removal page)
	 */
	private static final int REMOVE_PAGE = 0;
	
	
	/** 
	 * Constant reference to the creation page index of the pager.
	 * (Homework creation page)
	 */
	private static final int CREATE_PAGE = 2;
	
	
	private boolean refresh = true;
	
	/**
	 * A static reference to the main activity context.
	 */
	private static Context context;
	
	
	/**
	 * The main instance of the database manipulator.
	 */
	private HomeworkDataSource dataSource;
	
	
	/**
	 * A temporary list of homework that may be edited and manipulated in
	 * place of the values stored in the database.
	 */
	private List<Homework> hwlist = new ArrayList<Homework>();
	
	
	/**
	 * The main pager, which controls which fragment is currently being shown.
	 */
	private ViewPager pager;
	
	/**
	 * The main pager's adapter, which actually defines the max number of pages, the
	 * fragment to show on page <i>x</i>, and so on.
	 */
	private PagerAdapter pagerAdapter;
	
	
	/**
	 * Reference for the date field that will be edited to match the
	 * date selected by the date picker.
	 */
	private Date referenceDate;
	
	
	/**
	 * Determines what type of action should be executed after setting the date with the {@linkplain DateFragment date picker}.
	 * <table border="1">
	 * <tr>
	 *   <th>Value of Execution Bit</th>
	 *   <th>Action that is performed</th>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_DUE_ON 0xFF}</td>
	 *   <td>Search on the chosen date.</td>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_DUE_BEFORE 0x00}</td>
	 *   <td>Search on and before the chosen date.</td>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_HCREATE_DUE 0x01}</td>
	 *   <td>Set the hcreate_due section.</td>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_HCREATE_ASS 0x02}</td>
	 *   <td>Set the hcreate_assigned section.</td>
	 * </tr>
	 * <tr>
	 *   <td>{@link #EXECUTE_REMOVE_DATE 0x03}</td>
	 *   <td>Set the hremove_date section.</td>
	 * </tr>
	 * </table>
	 */
	private byte executionBit = 0;
	
	
	/** 
	 * Execution bit definition to search for homework due on the chosen date.
	 */
	private static final byte EXECUTE_DUE_ON = (byte)0xFF;
	
	
	/** 
	 * Execution bit definition to search for homework due on and before the chosen date. 
	 */
	private static final byte EXECUTE_DUE_BEFORE = 0x0;
	
	
	/** 
	 * Execution bit definition to set the hcreate_due section. 
	 */
	private static final byte EXECUTE_HCREATE_DUE = 0x1;
	
	
	/** 
	 * Execution bit definition to set the hcreate_assigned section.
	 */
	private static final byte EXECUTE_HCREATE_ASS = 0x2;
	
	
	/**
	 *  Execution bit definition to set the hremove_date section. 
	 */
	private static final byte EXECUTE_REMOVE_DATE = 0x3;
	
	
    /**
     * Gets a static reference to this activity as a {@link Context}.
     * 
     * @return The static context field, as populated in {@link #onCreate(Bundle)}.
     */
    public static Context getContext()
    {
    	return context;
    }
	
    
	/**
	 * Creates a new {@code HomeworkManager}, sets up the main content view, hooks a new 
	 *     {@link ScreenSlidePagerAdapter} to the main pager, opens up the {@linkplain #dataSource data source},
	 *     populates the {@linkplain #hwlist temporary homework list}, and, lastly, sets the 
	 *     {@linkplain #context static context reference} to the initialized activity.
	 *     
	 * <br>This function is automatically called by the Android operating system when the activity starts, and should
	 *     probably never be called manually in the application code.  To manually repopulate the temporary list,
	 *     use {@link #resetHomeworkList()} for full repopulation and {@link #refreshHomeworkList()} to simply
	 *     update the list view.
	 *     
	 * @param savedInstanceState If the activity is being re-initialized after previously
	 *     being shut down then this Bundle contains the data it most recently supplied in
	 *     {@link #onSaveInstanceState(Bundle)}. <b><i>Note: Otherwise it is null. </i></b>
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.hwm_layout_activity);
        
        this.pager = (ViewPager) this.findViewById(R.id.pager);
        this.pagerAdapter = new ScreenSlidePagerAdapter(this.getSupportFragmentManager());
        this.pager.setAdapter(this.pagerAdapter);
        this.pager.setPageTransformer(true, new ZoomPageTransformer());
        this.pager.setCurrentItem(PRIMARY_PAGE);
        
        this.dataSource = new HomeworkDataSource(this);
        this.dataSource.open();
        this.hwlist = this.dataSource.getAllHomework();
        
        context = this;
    }
    
    
    /**
     * Invoked whenever the application is resumed.
     * Reopens the database if the database was closed.
     */
    @Override
    protected void onResume() 
    {
    	super.onResume();
    	this.dataSource.open();
    }
    
    
    /**
     * Called when the application is suspended.
     * Closes the database.
     */
    @Override
    protected void onPause() 
    {
    	super.onPause();
    	this.dataSource.close();
    }
    
    
    /**
     * Sets the current activity intent to the new intent and
     * passes the intent on to the {@link #handleIntent(Intent)} method.
     * 
     * @param intent The intent that was passed to this activity.
     */
    @Override
    protected void onNewIntent(Intent intent) 
    {
    	this.setIntent(intent);
    	this.handleIntent(intent);
    }


    /**
     * Inflates the action bar and menu layout constructors.
     * 
     * @param menu The menu object to inflate into. Any items and
     *     submenus will be inserted into this menu.
     * 
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        this.getMenuInflater().inflate(R.menu.homework_manager, menu);
        return true;
    }

    
    /**
     * Handles any interactions made with the menu, based on the menu item that
     * was selected.
     * 
     * @param item The item that was selected. The ID of this item determines the action performed.
     * <table border="1">
     * <tr>
     *   <th>Item ID</th>
     *   <th>Description of Action</th>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_exit Exit Button}</td>
     *   <td> {@linkplain #finish() Exits} the application.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_menu_about About Menu Item}</td>
     *   <td>Shows a new "{@linkplain CommonAboutDialog about}" dialog.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_add_new Add New Button}</td>
     *   <td>Switches to the {@linkplain HomeworkCreateFragment assignment creation view}
     *           if it is not already on that view.  Otherwise, it {@linkplain #submitHomeworkCreateData(View) submits all data}
     *           present in the creation fragment.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_remove Remove Button}</td>
     *   <td>Switches to the {@linkplain HomeworkRemovalFragment multiple assignment deletion view}
     *           if it is not already in that view.  Otherwise, it {@linkplain #batchRemoveHomework(View) submits all data}
     *           present in the removal fragment.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_display_on Display Homework Due On Date Button}</td>
     *   <td>Displays a new {@linkplain DateFragment date picker} that will allow the user to pick a date to filter
     *           the homework display list by.  This filters the list to display only homework due on that date.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_display_before Display Homework Due Before Date Button}</td>
     *   <td>Displays a new {@linkplain DateFragment date picker} that will allow the user to pick a date to filter
     *           the homework display list by.  This filters the list to display only homework due on or before the selected
     *           date.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#menu_actionbar_refresh Refresh Button}</td>
     *   <td>Forces a {@linkplain #resetHomeworkList() full homework list reset}.</td>
     * </tr>
     * </table>
     * 
     * @return True if the item's action was consumed by this function.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	DateFragment dpFrag;
    	
    	switch(item.getItemId()) 
    	{
    	case R.id.menu_actionbar_exit:  // Exit Button
    		this.finish();
    		return true;
    		
    	case R.id.menu_menu_about: // About Menu Item
    		this.getSupportFragmentManager().beginTransaction()
    				.add(new CommonAboutDialog(), CodeResource.TAG_DIALOG_ABOUT)
    				.commit();
    		return true;
    		
    	case R.id.menu_actionbar_add_new: // Add New Button
    		
    		if(this.pager.getCurrentItem() != CREATE_PAGE) 
    		{
    			this.pager.setCurrentItem(CREATE_PAGE);
    		} else
    		{
    			this.submitHomeworkCreateData(null);
    		}
    		
    		this.resetHomeworkList();
    		return true;
    		
    	case R.id.menu_actionbar_remove: // Remove Button
    		if(this.pager.getCurrentItem() != REMOVE_PAGE)
    		{
    			this.pager.setCurrentItem(REMOVE_PAGE);
    		} else
    		{
    			this.batchRemoveHomework(null);
    		}
    		this.resetHomeworkList();
    		return true;
    		
    	case R.id.menu_actionbar_display_on: // Display Homework Due On Date Button
   			dpFrag = new DateFragment().setExecutionBit(EXECUTE_DUE_ON).setParentFrag(CodeResource.TAG_CONTEXT);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			return true;
   			
    	case R.id.menu_actionbar_display_before: // Display Homework Due Before Date Button
   			dpFrag = new DateFragment().setExecutionBit(EXECUTE_DUE_BEFORE).setParentFrag(CodeResource.TAG_CONTEXT);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			return true;
   			
    	case R.id.menu_actionbar_refresh: // Refresh Button
    		this.resetHomeworkList();
    		return true;
    	
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    
    /**
     * Handles an {@link Intent#ACTION_INSERT ACTION_INSERT} intent handed to this activity.  This is largely unused.<br>
     * 
     * The intent is expected to be handed to this activity in the same format as a calendar
     * insertion event. The title of the event becomes the homework name, the begin time becomes the assigned date,
     * and the end time becomes the due date. The data source then creates and adds the newly created
     * assignment to the database.
     * 
     * @param intent The intent to handle.
     */
    private void handleIntent(Intent intent) 
    {
    	if(Intent.ACTION_INSERT.equals(intent.getAction()))
    	{
    		if(Events.CONTENT_URI.equals(intent.getDataString()))
    		{
    			Bundle extras = intent.getExtras();
    			if(extras != null)
    			{
    				String name = "", className = "";
    				Date assign = null, due = null;
    				if(extras.containsKey(Events.TITLE)) 
    				{ 
    					name = extras.getString(Events.TITLE); 
    				}
    				if(extras.containsKey(Events.EVENT_LOCATION)) 
    				{ 
    					className = extras.getString(Events.EVENT_LOCATION); 
    				}
    				if(extras.containsKey(CalendarContract.EXTRA_EVENT_BEGIN_TIME)) 
    				{ 
    					Calendar cal = (Calendar) extras.get(CalendarContract.EXTRA_EVENT_BEGIN_TIME);
    					String dateString = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    					assign = Date.valueOf(dateString);
    				}
    				if(extras.containsKey(CalendarContract.EXTRA_EVENT_END_TIME))
    				{
    					Calendar cal = (Calendar) extras.get(CalendarContract.EXTRA_EVENT_END_TIME);
    					String dateString = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    					due = Date.valueOf(dateString);
    				}
    				
    				if(!name.isEmpty() && due != null && assign != null)
    				{
    					this.dataSource.createHomework(name, due, assign, className, "", new String[]{""});
    				}
    			}
    		}
    	}
    }
    
    
    /**
     * Iterates through the homework list and removes any homework that does not
     * contain the key phrase. This removal is only temporary: all changes are made
     * directly to the {@linkplain #hwlist temporary homework list} and do not
     * persist between launches, refreshes, layout changes, or even concurrent searches.
     * The homework list is reset prior to the search to ensure that all available
     * homework objects are searched.
     * 
     * @param keyphrase The key phrase to search for when iterating.
     */
    public void searchHomeworkList(String keyphrase)
    {
    	this.resetHomeworkList();
    	for(Iterator<Homework> iterhw = this.hwlist.iterator(); iterhw.hasNext();)
    	{
    		Homework hw = iterhw.next();
    		if(!hw._keywordSearch(keyphrase)) iterhw.remove();
    	}
    	this.refreshHomeworkList();
    }
    
    
    /**
     * Empties the {@linkplain #hwlist temporary homework list} and {@linkplain HomeworkDataSource#getAllHomework() repopulates}
     * it with the values contained in the database.  Afterwards, it {@linkplain #refreshHomeworkList() refreshes the homework list display}.<br>
     * 
     * This method is best used after changing the contents of the homework database.  If all that was changed was the temporary homework
     * list, use {@link #refreshHomeworkList()} instead.
     */
    public void resetHomeworkList() {
		Log.i(CodeResource.TAG_DEBUG, "Resetting homework list...");
		this.hwlist.clear();
		this.hwlist = this.dataSource.getAllHomework();
		this.refreshHomeworkList();
    }
    
    
    /**
     * Refreshes the {@link ListView} that is currently displaying the values contained in
     * the {@link #hwlist temporary homework list}. The temporary list remains unchanged.<br>
     * 
     * This method is best used after sorting or otherwise editing the temporary homework list.
     * It is automatically called when the list is reset.
     */
    protected void refreshHomeworkList() {
		Log.i(CodeResource.TAG_DEBUG, "Refreshing homework list...");
    	HomeworkListAdapter adapt = null;
		
		ListView list = (ListView) this.findViewById(R.id.fragment_main_listlayout);
		
		if(list != null) adapt = (HomeworkListAdapter) list.getAdapter();
		else 
		{
			Log.e(CodeResource.TAG_DEBUG, "Unable to refresh homework list: List was null!");
			return;
		}
		
		if(adapt != null) {
			adapt.clear();
			adapt.addAll(this.hwlist);
		} else {
			Log.e(CodeResource.TAG_DEBUG, "Unable to refresh homework list: List adapter was null!");
		}
    }
    
    
    /**
     * Handles all date-based button interaction.
     * 
     * @param view The ID of the view supplied determines the action performed.
     * <table border="1">
     * <tr>
     *   <th>View ID</th>
     *   <th>Description of Action</th>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#hcreate_assigned_section_button Assigned Date Selector}<br>(From homework creation view)</td>
     *   <td>Shows a new {@linkplain DateFragment date picker} that will set the date in the assigned field.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#hcreate_due_section_button Due Date Selector}<br>(From homework creation view)</td>
     *   <td>Shows a new {@linkplain DateFragment date picker} that will set the date in the due field.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#hinfo_assigned_section_button Assigned Date Selector}<br>(From homework information view)</td>
     *   <td>Shows a new {@linkplain DateFragment date picker} that will set the date in the assigned field.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#hinfo_due_section_button Due Date Selector}<br>(From homework information view)</td>
     *   <td>Shows a new {@linkplain DateFragment date picker} that will set the date in the due field.</td>
     * </tr>
     * <tr>
     *   <td>{@linkplain R.id#hremove_date_section_button Removal Date Selector}</td>
     *   <td>Shows a new {@linkplain DateFragment date picker} that will set the date in the removal fragment's date field.</td>
     * </tr>
     * </table>
     */
    public void pickDate(View view) 
    {
    	DateFragment dpFrag;
    	
   		switch(view.getId()) 
   		{
   		case R.id.hcreate_assigned_section_button: // Assigned section button on the creation view
   			Log.i(CodeResource.TAG_DEBUG, "Picking date for hcreate_assigned section!");
   			
   			dpFrag = new DateFragment().setExecutionBit(EXECUTE_HCREATE_ASS).setParentFrag(CodeResource.TAG_CONTEXT);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			
   			return;
   		
   		case R.id.hcreate_due_section_button: // Due section button on the creation view
   			Log.i(CodeResource.TAG_DEBUG, "Picking date for hcreate_due section!");
   			
   			dpFrag = new DateFragment().setExecutionBit(EXECUTE_HCREATE_DUE).setParentFrag(CodeResource.TAG_CONTEXT);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			
   			return;
   			
		case R.id.hinfo_assigned_section_button: // Assigned section button in the information dialog
			Log.i(CodeResource.TAG_DEBUG, "Picking date for hinfo_assigned section!");
			
			dpFrag = new DateFragment().setExecutionBit(HomeworkInformationDialog.EXECUTE_ASS_SET).setParentFrag(CodeResource.TAG_DIALOG_INFO);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			
			return;
		
		case R.id.hinfo_due_section_button: // Due section button in the information dialog
			Log.i(CodeResource.TAG_DEBUG, "Picking date for hinfo_due section!");
			
			dpFrag = new DateFragment().setExecutionBit(HomeworkInformationDialog.EXECUTE_DUE_SET).setParentFrag(CodeResource.TAG_DIALOG_INFO);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
			return;
			
		case R.id.hremove_date_section_button: // Homework removal date selection button
			Log.i(CodeResource.TAG_DEBUG, "Picking date for hremove_date section!");
			
			dpFrag = new DateFragment().setExecutionBit(EXECUTE_REMOVE_DATE).setParentFrag(CodeResource.TAG_CONTEXT);
   			this.getFragmentManager().beginTransaction().add(dpFrag, CodeResource.TAG_DATE_PICKER).commit();
   			
   		default:
   			return;
   		}
    }
    
    
    /**
     * Handles the removal of multiple assignments from the database "at once".  Matches
     * all assignments to specific criteria as decided by the user on the removal menu.
     * This is accomplished by iterating through a {@linkplain HomeworkDataSource#getAllHomework() list mirroring the contents of 
     * the database}.<br>
     * 
     * If the assignment matches the deletion requirements, it is summarily
     * {@linkplain HomeworkDataSource#deleteHomework(Homework) deleted from the database.}
     * The {@linkplain #hwlist main temporary homework list} is {@linkplain #resetHomeworkList() reset}
     * and the list view reset, and all fields in the remove menu are set back to their initial values.
     * 
     * @param view unused
     */
    public void batchRemoveHomework(View view)
    {   
    	//Fields in the view.
    	EditText dateEdit = (EditText) this.findViewById(R.id.hremove_date_section_edit);
    	EditText keywordText = (EditText) this.findViewById(R.id.hremove_main_keywords_edit);
    	Switch deleteOn = (Switch) this.findViewById(R.id.hremove_removal_type_on);
    	Switch deleteBefore = (Switch) this.findViewById(R.id.hremove_removal_type_before);
    	Switch deleteAfter = (Switch) this.findViewById(R.id.hremove_removal_type_after);
    	
    	Date delRefDate = null; //Reference date for the deletion menu.
    	if(deleteOn.isChecked() || deleteBefore.isChecked() || deleteAfter.isChecked()) //Should dates even be checked?
    	{
	    	//Failure message for later reference
	    	String cannotComplete = this.getString(R.string.toast_message_hremove_failure) + "\n";
	    	
	    	//Get the date text.
	    	String dateRaw = dateEdit.getText().toString();
	    	if(dateRaw.isEmpty()) //Date text was not entered.
	    	{
	    		cannotComplete += this.getString(R.string.toast_message_hremove_failure_date_unset);
	    		Toast.makeText(this, cannotComplete, Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	    	//Split by valid date separators.
	    	String[] dateSplit = dateRaw.split("[/-]");
	    	if(dateSplit.length != 3) //Whoops, wrong number of parts for that date!
	    	{
	    		cannotComplete += this.getString(R.string.toast_message_hremove_failure_date_format);
	    		Toast.makeText(this, cannotComplete, Toast.LENGTH_LONG).show();
	    		return;
	    	}
	    	
	    	//Correct shortened date format (dd/MM/yy) to dd/MM/yyyy
	    	//Assumed 21st century (20XX) because it's only logical to.
	    	if(dateSplit[2].length() == 2) dateSplit[2] = "20" + dateSplit[2];
	    	
	    	//Reformat to SQL convention (yyyy-MM-dd)
	    	String dateFormat = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];
	    	
	    	//Set the reference date.
	    	delRefDate = Date.valueOf(dateFormat);
    	}
    	
    	//Iterate through the data source objects.
    	for(Iterator<Homework> hwiter = this.dataSource.getAllHomework().iterator(); hwiter.hasNext();)
    	{
    		Homework hw = hwiter.next();
    		boolean shouldDelete = false;
    		
    		Switch currentSwitch = deleteOn;
    		if(currentSwitch.isChecked()) shouldDelete |= hw.isDueOn(delRefDate);
    		
    		currentSwitch = deleteBefore;
    		if(currentSwitch.isChecked()) shouldDelete |= hw.isDueBefore(delRefDate);
    		
    		currentSwitch = deleteAfter;
    		if(currentSwitch.isChecked()) shouldDelete |= hw.isDueAfter(delRefDate);
    		
    		String keywords = keywordText.getText().toString();
    		if(!keywords.isEmpty()) shouldDelete |= hw._keywordSearch(keywords);
    		
    		if(shouldDelete) this.dataSource.deleteHomework(hw);
    	}
    	
    	//Reset all fields and lists.
    	this.resetHomeworkList();
    	this.pager.setCurrentItem(PRIMARY_PAGE);
    	
    	dateEdit.setText("");
    	keywordText.setText("");
    	deleteOn.setChecked(true);
    	deleteBefore.setChecked(false);
    	deleteAfter.setChecked(false);
    }
    
    
    /**
     * Handles the addition of new assignments into the database.  The fragment this data
     * is gathered from has several fields that data must be aggregated from.<br>
     * 
     * If the name field, due date field, or the assigned field are empty, the creation fails.
     * It also fails if the due or assigned date fields are not properly formatted.  Otherwise it 
     * {@linkplain HomeworkDataSource#createHomework(String, Date, Date, String, String, String...) adds a new assignment}
     * to the database.<br>
     * 
     * After creation, it {@linkplain #resetHomeworkList() resets the homework list} and refreshes the views. It also resets all
     * fields in the homework creation view to their default values and sets the {@linkplain #pager main pager} page to 0.
     * 
     * @param view unused
     */
    public void submitHomeworkCreateData(View view) 
    {
    	//All fields that are used in creating a homework object.
    	EditText[] fields = 
    	{	
    		(EditText) this.findViewById(R.id.hcreate_name_section_edit),
    		(EditText) this.findViewById(R.id.hcreate_class_section_edit),
    		(EditText) this.findViewById(R.id.hcreate_subject_section_edit),
    		(EditText) this.findViewById(R.id.hcreate_due_section_edit),
    		(EditText) this.findViewById(R.id.hcreate_assigned_section_edit),
    		(EditText) this.findViewById(R.id.hcreate_main_keywords_edit)
    	};
    	
    	//Aggregate all field values.
    	String name = fields[0].getText().toString();
    	String className = fields[1].getText().toString();
    	String subject = fields[2].getText().toString();
    	String dueRaw = fields[3].getText().toString();
    	String assignedRaw = fields[4].getText().toString();
    	String keysRaw = fields[5].getText().toString();
    	
    	//Default failure message prefix
    	String cannotComplete = this.getString(R.string.toast_message_hcreate_failure) + "\n";
    	
    	if(name.isEmpty() || dueRaw.isEmpty() || assignedRaw.isEmpty()) //Uh-oh, key information is missing.
    	{
    		if(name.isEmpty())
    		{
    			cannotComplete += this.getString(R.string.toast_message_hcreate_failure_name);
    		} else if(dueRaw.isEmpty()) 
    		{
    			cannotComplete += this.getString(R.string.toast_message_hcreate_failure_due_unset);
    		} else if(assignedRaw.isEmpty())
    		{
    			cannotComplete += this.getString(R.string.toast_message_hcreate_failure_assigned_unset);
    		}
    		
    		Toast.makeText(this, cannotComplete, Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	//Split the date values by allowed delimiters.
    	String[] dueSplit = dueRaw.split("[/-]");
    	String[] assignedSplit = assignedRaw.split("[/-]");
    	
    	if(dueSplit.length != 3 || assignedSplit.length != 3) //Incorrectly formatted date fields!
    	{
    		if(dueSplit.length != 3) cannotComplete += this.getString(R.string.toast_message_hcreate_failure_due_format); else
    		if(assignedSplit.length != 3) cannotComplete += this.getString(R.string.toast_message_hcreate_failure_assigned_format);
    		
    		Toast.makeText(this, cannotComplete, Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	//Correct date lengths by assuming the 21st century.
    	//Because anything else is morbidly overdue or otherwise illogical to assume.
    	if(dueSplit[2].length() == 2) dueSplit[2] = "20" + dueSplit[2];
    	if(assignedSplit[2].length() == 2) assignedSplit[2] = "20" + assignedSplit[2];
    	
    	//Reformat to match SQL conventions (yyyy-MM-dd)
    	String dueFormat = dueSplit[2] + "-" + dueSplit[1] + "-" + dueSplit[0];
    	String assignedFormat = assignedSplit[2] + "-" + assignedSplit[1] + "-" + assignedSplit[0];
    	
    	//Split the monolithic keyword string by the semicolon delimiter.
    	//The Homework class will automatically trim leading and trailing whitespace from each individual keyword.
    	String[] keysSplit = keysRaw.split(";");
    	
    	//Attempt to create the homework. On the occasion that something goes horribly wrong, warn the user and continue.
    	try 
    	{
    		this.dataSource.createHomework(name, Date.valueOf(dueFormat), Date.valueOf(assignedFormat), className, subject, keysSplit);
    		this.resetHomeworkList();
    	} catch(IllegalFormatException exc) 
    	{
    		exc.printStackTrace();
    		Toast.makeText(this, R.string.toast_message_hcreate_failure, Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	//Reset all fields and the pager.
    	for(EditText field : fields) {
    		field.setText("");
    	}
    	
    	this.pager.setCurrentItem(PRIMARY_PAGE);
    }
    
    
    /**
     * Obtains the database manipulator.
     * 
     * @return The {@linkplain main database manipulator}.
     */
    public HomeworkDataSource getDataSource()
    {
    	return this.dataSource;
    }
    
    
    /**
     * Sets the {@link #referenceDate} field equal to the
     * supplied date.
     * 
     * @param date The date that the reference should be equivalent
     *     to.
     */
	@Override
	public void setDateData(Date date)
	{
		this.referenceDate = date;
	}
	
	/**
	 * Sets the search type.  If the byte given is less than or equal to 0,
	 * {@link #executionBit} will be set to true; if it is greater than 0,
	 * {@code searchViewType} will be set to false;
	 * 
	 * @param b0 The new {@linkplain #executionBit execution bit} value.
	 * 
	 * @see #executionBit The actual execution bit, along with a list of functions
	 *     activated when it is equal to a specific value.
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
	 * 
	 * @see #executionBit For the valid execution bit states and their functions.
	 */
	@Override
	public void execute()
	{
		if(this.referenceDate == null) //Well, we can't really do anything if the reference date wasn't set, can we?
		{
			Toast.makeText(this, R.string.app_no_date, Toast.LENGTH_LONG).show();
			return;
		}
		
		//Setup for later functionality (specifically all the non-search oriented execution functions).
		EditText dateField = null;
		String dateSplit[] = this.referenceDate.toString().split("-");
		String dateFormatted = dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0];
		
		switch(this.executionBit)
		{
		case EXECUTE_DUE_ON:
		case EXECUTE_DUE_BEFORE:
			this.resetHomeworkList();
			
			for(Iterator<Homework> hwiter = this.hwlist.iterator(); hwiter.hasNext();)
			{
				Homework hw = hwiter.next();
				if(!(this.executionBit == EXECUTE_DUE_BEFORE ? hw.isDueOnBefore(this.referenceDate) : hw.isDueOn(this.referenceDate)))
				{
					hwiter.remove();
				}
			}
			break;
			
		case EXECUTE_HCREATE_DUE:
			Log.d(CodeResource.TAG_DEBUG, "Executing date pick for hcreate_due section");
			dateField = (EditText) this.findViewById(R.id.hcreate_due_section_edit);
			dateField.setText(dateFormatted);
			break;
			
		case EXECUTE_HCREATE_ASS:
			Log.d(CodeResource.TAG_DEBUG, "Executing date pick for hcreate_assigned section");
			dateField = (EditText) this.findViewById(R.id.hcreate_assigned_section_edit);
			dateField.setText(dateFormatted);
			break;
			
		case EXECUTE_REMOVE_DATE:
			Log.d(CodeResource.TAG_DEBUG, "Executing date pick for hremove_date section");
			dateField = (EditText)this.findViewById(R.id.hremove_date_section_edit);
			dateField.setText(dateFormatted);
			break;
		}
		
		this.refreshHomeworkList();
		this.referenceDate = null;
	}
    
	
	
	/**
	 * Controls the functionality of the {@linkplain HomeworkManager#pager main view pager}.
	 * Also updates the {@linkplain HomeworkManager#hwlist homework list} when the view pager finishes
	 * updating its state. This also is fired initially on app launch for an initial population of the
	 * list.
	 * 
	 * @author Bridger Maskrey (bem9@students.pti.edu)
	 * 
	 * @version 1.0.0
	 */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter 
    {
    	/**
    	 * Constructs a new {@code ScreenSlidePagerAdapter}.
    	 * 
    	 * @param fm The fragment manager.
    	 */
		public ScreenSlidePagerAdapter(FragmentManager fm) 
		{
			super(fm);
		}

		
		/**
		 * Returns the item that should appear on the current page.
		 * 
		 * @param arg0 The ID of the page that the fragment currently rests on.
		 * 
		 * @return The fragment that should be shown on the selected page.
		 * <table border=1>
		 * <tr>
		 *   <th colspan=2>Return Data</th>
		 * </tr>
		 * <tr>
		 *   <td>{@link #PRIMARY_PAGE}</td>
		 *   <td>Returns a new {@link HomeworkListFragment}.</td>
		 * </tr>
		 * <tr>
		 *   <td>{@link #CREATE_PAGE}</td>
		 *   <td>Returns a new {@link HomeworkCreateFragment}.</td>
		 * </tr>
		 * <tr>
		 *   <td>{@link #REMOVE_PAGE}</td>
		 *   <td>Returns a new {@link HomeworkRemovalFragment}.</td>
		 * </tr>
		 * </table>
		 */
		@Override
		public Fragment getItem(int arg0)
		{
			switch(arg0) {
			case PRIMARY_PAGE:
				return new HomeworkListFragment();
			case CREATE_PAGE:
				return new HomeworkCreateFragment();
			case REMOVE_PAGE:
				return new HomeworkRemovalFragment();
			default:
				return null;
			}
		}

		/**
		 * Returns the total number of pages in this adapter.
		 * 
		 * @return The total number of pages in this adapter.
		 */
		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
		
		/**
		 * Completes the update of this page. If the refresh check boolean is true,
		 * it will refresh the page.
		 * 
		 * @param container The main container this pager resides in.
		 */
		@Override
		public void finishUpdate(ViewGroup container) 
		{
			super.finishUpdate(container);
			
			if(HomeworkManager.this.refresh)
			{
				refreshHomeworkList();
				HomeworkManager.this.refresh = false;
			}
		}
    }
    
    
    
    /**
     * Transformer that manages the animation rendered as pages are switched out in the {@link ViewPager}.<br>
     * 
     * This one creates an intense zoom-and-fade effect in which pages will quickly shrink to an offscreen
     * size of 10% of their original and quickly fade out as they leave the screen.
     * 
     * @author Bridger Maskrey (bem9@students.pti.edu)
     * 
     * @see <a href=http://developer.android.com/training/animation/screen-slide.html>The android developer 
     *     screen slide tutorial</a>, as this is based on that code.
     */
    private class ZoomPageTransformer implements ViewPager.PageTransformer 
    {
    	/**
    	 * The minimum size multiplier allowed for this view.
    	 */
    	private static final float _minZoom = 0.1F;
    	
    	/**
    	 * Applies a zoom and fade effect to the pager as it is between pages.
    	 * 
    	 * @param view The view to transform.
    	 * @param pos The current position of the pager.
    	 */
		@Override
		public void transformPage(View view, float pos) 
		{
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();
			
			if(pos < -1 || pos > 1) view.setAlpha(0);
			else if (pos <= 1) 
			{
				float scaleFactor = Math.max(_minZoom, 1 - Math.abs(pos));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horizMargin = pageWidth * (1 - scaleFactor) / 2;
				
				if(pos < 0)
				{
					view.setTranslationX(horizMargin - vertMargin / 2);
				} else 
				{
					view.setTranslationX(vertMargin / 2 - horizMargin);
				}
				
				view.setAlpha((scaleFactor - _minZoom) / (1 - _minZoom));
				view.setScaleX(0.45F + (scaleFactor - _minZoom) /
						(1 - _minZoom) * (1 - 0.45F));
				view.setScaleY(0.45F + (scaleFactor - _minZoom) /
						(1 - _minZoom) * (1 - 0.45F));
			}
		}
    	
    }
}
