package app.com.example.netra.app;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.app.Activity;
import app.com.example.netra.app.R;

public class BookmarksList extends Activity
	{
	private TextView bookmarks;
	private TextView bookmarksgoto;
	private TextView bookmarksdelete;
	private TextView goback;
	private int selectedBookmark = -1;
	
    @Override protected void onCreate(Bundle savedInstanceState)
    	{
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.bookmarkslist);
		GlobalVars.lastActivity = BookmarksList.class;
		bookmarks = (TextView) findViewById(R.id.bookmarks);
		bookmarksgoto = (TextView) findViewById(R.id.bookmarksgoto);
		bookmarksdelete = (TextView) findViewById(R.id.bookmarksdelete);
		goback = (TextView) findViewById(R.id.goback);
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=4;
		selectedBookmark = -1;

		//HIDES THE NAVIGATION BAR
		if (android.os.Build.VERSION.SDK_INT>11){try{GlobalVars.hideNavigationBar(this);}catch(Exception e){}}
    	}
	
	@Override public void onResume()
		{
		super.onResume();
		try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
		GlobalVars.lastActivity = BookmarksList.class;
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=4;
		GlobalVars.selectTextView(bookmarks,false);
		GlobalVars.selectTextView(bookmarksgoto,false);
		GlobalVars.selectTextView(bookmarksdelete,false);
		GlobalVars.selectTextView(goback,false);
		if (GlobalVars.bookmarkWasDeleted==true)
			{
			selectedBookmark = -1;
			GlobalVars.setText(bookmarks, false, getResources().getString(R.string.layoutBookmarksListList));
			GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListDeleted));
			GlobalVars.bookmarkWasDeleted=false;
			}
			else
			{
			GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListOnResume));
			}

		//HIDES THE NAVIGATION BAR
		if (android.os.Build.VERSION.SDK_INT>11){try{GlobalVars.hideNavigationBar(this);}catch(Exception e){}}
		}
		
	public void select()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //LIST BOOKMARKS
			GlobalVars.selectTextView(bookmarks,true);
			GlobalVars.selectTextView(bookmarksgoto,false);
			GlobalVars.selectTextView(goback,false);
			if (selectedBookmark==-1)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListList2));
				}
				else
				{
				String bookmarkName = GlobalVars.browserBookmarks.get(selectedBookmark).substring(0,GlobalVars.browserBookmarks.get(selectedBookmark).indexOf("|"));
				GlobalVars.talk(bookmarkName);
				}
			break;

			case 2: //GO TO BOOKMARK URL
			GlobalVars.selectTextView(bookmarksgoto, true);
			GlobalVars.selectTextView(bookmarks,false);
			GlobalVars.selectTextView(bookmarksdelete,false);
			GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListGoToLink));
			break;
			
			case 3: //DELETE BOOKMARK
			GlobalVars.selectTextView(bookmarksdelete, true);
			GlobalVars.selectTextView(bookmarksgoto,false);
			GlobalVars.selectTextView(goback,false);
			GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListDelete));
			break;

			case 4: //GO BACK TO THE PREVIOUS MENU
			GlobalVars.selectTextView(goback,true);
			GlobalVars.selectTextView(bookmarks,false);
			GlobalVars.selectTextView(bookmarksdelete,false);
			GlobalVars.talk(getResources().getString(R.string.backToPreviousMenu));
			break;
			}
		}
		
	public void execute()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //LIST BOOKMARKS
			if (GlobalVars.browserBookmarks.size()==0)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListList3));
				}
				else
				{
				if (selectedBookmark+1==GlobalVars.browserBookmarks.size())
					{
					selectedBookmark=-1;
					}
				selectedBookmark = selectedBookmark + 1;
				String bookmarkName = GlobalVars.browserBookmarks.get(selectedBookmark).substring(0,GlobalVars.browserBookmarks.get(selectedBookmark).indexOf("|"));
				GlobalVars.talk(bookmarkName);
				GlobalVars.setText(bookmarks, true, bookmarkName);
				}
			break;

			case 2: //GO TO BOOKMARK URL
			if (selectedBookmark==-1)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListSelectError));
				}
				else
				{
				new BrowserThreadGoTo().execute(GlobalVars.browserBookmarks.get(selectedBookmark).substring(GlobalVars.browserBookmarks.get(selectedBookmark).indexOf("|") + 1, GlobalVars.browserBookmarks.get(selectedBookmark).length()));
				}
			break;
			
			case 3: //DELETE BOOKMARK
			if (selectedBookmark==-1)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListSelectError));
				}
				else
				{
				GlobalVars.bookmarkWasDeleted=false;
				GlobalVars.bookmarkToDeleteIndex = selectedBookmark;
				GlobalVars.startActivity(BookmarksDelete.class);
				}
			break;

			case 4: //GO BACK TO THE PREVIOUS MENU
			this.finish();
			break;
			}
		}
		
	private void previousItem()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //LIST BOOKMARKS
			if (GlobalVars.browserBookmarks.size()==0)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBookmarksListList3));
				}
				else
				{
				if (selectedBookmark-1<0)
					{
					selectedBookmark = GlobalVars.browserBookmarks.size();
					}
				selectedBookmark = selectedBookmark - 1;
				String bookmarkName = GlobalVars.browserBookmarks.get(selectedBookmark).substring(0,GlobalVars.browserBookmarks.get(selectedBookmark).indexOf("|"));
				GlobalVars.talk(bookmarkName);
				GlobalVars.setText(bookmarks, true, bookmarkName);
				}
			break;
			}
		}

	@Override public boolean onTouchEvent(MotionEvent event)
		{
		int result = GlobalVars.detectMovement(event);
		switch (result)
			{
			case GlobalVars.ACTION_SELECT:
			select();
			break;

			case GlobalVars.ACTION_SELECT_PREVIOUS:
			previousItem();
			break;
			
			case GlobalVars.ACTION_EXECUTE:
			execute();
			break;
			}
		return super.onTouchEvent(event);
		}

	public boolean onKeyUp(int keyCode, KeyEvent event)
		{
		int result = GlobalVars.detectKeyUp(keyCode);
		switch (result)
			{
			case GlobalVars.ACTION_SELECT:
			select();
			break;

			case GlobalVars.ACTION_SELECT_PREVIOUS:
			previousItem();
			break;
			
			case GlobalVars.ACTION_EXECUTE:
			execute();
			break;
			}
		return super.onKeyUp(keyCode, event);
		}

	public boolean onKeyDown(int keyCode, KeyEvent event)
		{
		return GlobalVars.detectKeyDown(keyCode);
		}		
	}
