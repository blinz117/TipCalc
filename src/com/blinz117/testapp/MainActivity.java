package com.blinz117.testapp;

import java.math.BigDecimal;
import java.util.Vector;

import com.blinz117.testapp.NumPadFragment.NumPadListener;
import com.blinz117.testapp.TipManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class MainActivity extends Activity
	implements NumPadListener{
	
	static final String STATE_TEXT = "userValue";
	
	int minPercent = 15;
	int maxPercent = 20;
	
	TipManager mTipManager;

	LinearLayout mResultsView;
	TextView mTextDisplay;
	CharSequence mCurrText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        
        mTipManager = new TipManager(minPercent, maxPercent);
        
        mResultsView = (LinearLayout)findViewById(R.id.tableHolder);
        mTextDisplay = (TextView)findViewById(R.id.text_display);
        
        mCurrText = "";
        mTextDisplay.setText(ConvertToDisplayText(mCurrText));
        UpdateResult();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
            	onOpenSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onOpenSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    protected CharSequence ConvertToDisplayText(CharSequence textOrig)
    {
    	CharSequence displayText = textOrig;
    	for (int i=3-displayText.length(); i>0; i--)
    	{
    		displayText = "0" + displayText;
    	}
    	int currLength = displayText.length();
    	displayText = displayText.subSequence(0, currLength-2) + "." + displayText.subSequence(currLength-2, currLength);
    	return displayText;
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	//Save state on certain changes, such as screen rotation
        savedInstanceState.putCharSequence(STATE_TEXT, mCurrText);
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	//Restore state on certain changes, such as screen rotation
        super.onRestoreInstanceState(savedInstanceState);
        mCurrText = savedInstanceState.getCharSequence(STATE_TEXT);
        mTextDisplay.setText(ConvertToDisplayText(mCurrText));
        UpdateResult();
    }

	@Override
	public void OnNumberClicked(CharSequence value) {
    	// prevent leading zeros
    	if (mCurrText.length() == 0 && value.equals("0"))
    		return;
    	
    	mCurrText = mCurrText + value.toString();
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();		
	}

	@Override
	public void OnUndoButtonClicked() {
    	int iEndChar = mCurrText.length() - 1;
    	if (iEndChar < 0)
    		return;
    	mCurrText = mCurrText.subSequence(0, iEndChar);
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();
	}

	@Override
	public void OnClearButtonPressed() {
    	mCurrText = "";
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();
	}
	
    public void UpdateResult()
    {
    	double currValue = Double.parseDouble(mTextDisplay.getText().toString());
    	mTipManager.UpdateBaseAmount(currValue);

    	UpdateResultsTable();
    }
	
	public void UpdateResultsTable()
	{
		//Clear the children of the scroll view
		mResultsView.removeViews(0, mResultsView.getChildCount());
		
		// Now generate the new layouts
		Vector<BigDecimal> percents = mTipManager.GetPercentages();
		Vector<BigDecimal> tips = mTipManager.GetTips();
		Vector<BigDecimal> totals = mTipManager.GetTotals();
		int numItems = percents.size();
		for (int ndx = 0; ndx<numItems; ndx++)
		{
			View newTable = GenerateTipLayout(percents.get(ndx), tips.get(ndx), totals.get(ndx));
			mResultsView.addView(newTable);
			
			// Add space after each result
			Space resultSpace = new Space(this);
			LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.result_space));
			resultSpace.setLayoutParams(spaceParams);
			mResultsView.addView(resultSpace);
		}
	}
	
	private View GenerateTipLayout(BigDecimal tipPercent, BigDecimal tipAmount, BigDecimal tipTotal)
	{
		
		/*
		 * Emulate this layout:
		 * 
		 <TableLayout
	    android:id="@+id/tableLayout1"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:padding="@dimen/text_display_padding" >
	    
	    <TableRow
	        android:id="@+id/tableRow15"  
	        android:layout_height="wrap_content"  
	        android:layout_width="match_parent">  
	        <TextView  
	            android:id="@+id/percent15"  
	            android:layout_width="wrap_content"  
	            android:layout_height="wrap_content"   
	            android:textSize="@dimen/default_text_size"  
	            android:text="@string/percent15" />
	       	<TextView  
	            android:id="@+id/tip15"  
	            android:layout_width="0dp"
	            android:layout_weight="1"  
	            android:layout_height="wrap_content"
	            android:maxLines="1"   
	            android:textSize="@dimen/default_text_size"
	            android:typeface="monospace"
	            android:gravity="right" />
	    </TableRow>

	    <TableRow
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#111111" >
            <Space
                android:layout_width="match_parent"
                android:layout_height="1dp" />
        </TableRow>

        <TableRow
	        android:layout_height="wrap_content"  
	        android:layout_width="match_parent">  
	        <TextView
	            android:text="@string/total_header"
	            android:textSize="@dimen/default_text_size"
	            android:layout_width="wrap_content"/>
	       	<TextView  
	            android:id="@+id/sum15"  
	            android:layout_width="0dp"
	            android:layout_weight="1"  
	            android:layout_height="wrap_content"
	            android:maxLines="1"    
	            android:textSize="@dimen/default_text_size"
	            android:typeface="monospace"
	            android:gravity="right" >
	        </TextView> 
	    </TableRow>   
		 */
		int iMatchParent = LayoutParams.MATCH_PARENT;
		int iWrapContent = LayoutParams.WRAP_CONTENT;
		LayoutParams regularParams = new LayoutParams(iMatchParent, iWrapContent);
		TableRow.LayoutParams promptItemParams = new TableRow.LayoutParams(iWrapContent, iWrapContent);
		TableRow.LayoutParams valueItemParams = new TableRow.LayoutParams(0, iWrapContent, 1.0f);
		
		/*
		 * Create new table
		 */
		TableLayout tipLayout = new TableLayout(this);
		tipLayout.setLayoutParams(regularParams);
		
		/*
		 *  Tip row
		 */
		TableRow tipRow = new TableRow(this);
		tipRow.setLayoutParams(regularParams);
		/*
	    <TextView  
	      android:id="@+id/percent15"  
	      android:layout_width="wrap_content"  
	      android:layout_height="wrap_content"   
	      android:textSize="@dimen/default_text_size"  
	      android:text="@string/percent15" />
		 */
		TextView percentText = new TextView(this);
		percentText.setLayoutParams(promptItemParams);
		percentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_text_size));
		percentText.setText("(" + tipPercent + "%)");
		
		/*
	   	<TextView  
	      android:id="@+id/tip15"  
	      android:layout_width="0dp"
	      android:layout_weight="1"  
	      android:layout_height="wrap_content"
	      android:maxLines="1"   
	      android:textSize="@dimen/default_text_size"
	      android:typeface="monospace"
	      android:gravity="right" />
		 */
		TextView tipText = new TextView(this);
		tipText.setLayoutParams(valueItemParams);
		tipText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_text_size));
		tipText.setText("" + tipAmount);
		tipText.setGravity(Gravity.RIGHT);
		tipText.setMaxLines(1);
		tipText.setTypeface(Typeface.MONOSPACE);
		
		// Add values to tip row
		tipRow.addView(percentText);
		tipRow.addView(tipText);
		
		/*
		 *  Black line between tip and total
		 */
		TableRow lineRow = new TableRow(this);
		lineRow.setLayoutParams(regularParams);
		lineRow.setBackgroundColor(0xff111111);
		
		Space lineSpace = new Space(this);
		TableRow.LayoutParams spaceParams = new TableRow.LayoutParams(iMatchParent, getResources().getDimensionPixelSize(R.dimen.line_thickness));
		lineSpace.setLayoutParams(spaceParams);
		
		lineRow.addView(lineSpace);
		
		/*
		 *  Total row
		 */
		TableRow totalRow = new TableRow(this);
		totalRow.setLayoutParams(regularParams);

		// Total header
		TextView totalHeader = new TextView(this);
		totalHeader.setLayoutParams(promptItemParams);
		totalHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_text_size));
		totalHeader.setText(R.string.total_header);
		
		// Total value
		TextView totalText = new TextView(this);
		totalText.setLayoutParams(valueItemParams);
		totalText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.default_text_size));
		totalText.setText(tipTotal.toPlainString());
		totalText.setGravity(Gravity.RIGHT);
		totalText.setMaxLines(1);
		totalText.setTypeface(Typeface.MONOSPACE);
		
		// add values to total row
		totalRow.addView(totalHeader);
		totalRow.addView(totalText);
		
		/*
		 * Finally, add the rows to the table
		 */
		tipLayout.addView(tipRow);
		tipLayout.addView(lineRow);
		tipLayout.addView(totalRow);
		
		return tipLayout;
	}
    
}
