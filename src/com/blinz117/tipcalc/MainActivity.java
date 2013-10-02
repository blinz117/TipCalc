package com.blinz117.tipcalc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import com.blinz117.tipcalc.TipManager;
import com.blinz117.tipcalc.NumPadFragment.NumPadListener;
import com.blinz117.tipcalc.TipManager.TipCalculationListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class MainActivity extends Activity
	implements NumPadListener, TipCalculationListener{
	
	static final String STATE_TEXT = "userValue";
	
	public static final String KEY_PREF_MIN_PERCENT = "pref_MinTipkey";
	public static final String KEY_PREF_MAX_PERCENT = "pref_MaxTipkey";
	public static final String KEY_PREF_ROUND_TOTALS = "pref_RoundTotalkey";
	
	int minPercent = 15;
	int maxPercent = 20;
	
	TipManager mTipManager;
	
	List<Tip> mTipList;
	ListView mResultList;
	TipAdapter mTipAdapter;

	//LinearLayout mResultsView;
	TextView mTextDisplay;
	CharSequence mCurrText;
	
	ProgressBar mLoadingView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        
        mLoadingView = (ProgressBar)findViewById(R.id.loadingView);
        
        mTipManager = new TipManager(this, minPercent, maxPercent);
        
        mTipList = new ArrayList<Tip>();
        
        mResultList = (ListView)findViewById(R.id.result_list);
        mTipAdapter = new TipAdapter(this, R.layout.layout_result, mTipList);
        mResultList.setAdapter(mTipAdapter);
       // mResultsView = (LinearLayout)findViewById(R.id.tableHolder);
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
    	mTipList.clear();
    	mTipAdapter.notifyDataSetChanged();
    	//mResultsView.removeViews(0, mResultsView.getChildCount());
    	
    	double currValue = Double.parseDouble(mTextDisplay.getText().toString());
    	mTipManager.UpdateBaseAmount(currValue);
    	
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	double newMinPercent = Double.parseDouble(sharedPref.getString(KEY_PREF_MIN_PERCENT, "10"));
    	double newMaxPercent = Double.parseDouble(sharedPref.getString(KEY_PREF_MAX_PERCENT, "25"));
    	
    	boolean bShowRound = sharedPref.getBoolean(KEY_PREF_ROUND_TOTALS, false);
    	
    	mTipManager.UpdateRoundTips(bShowRound);

    	if (newMinPercent != minPercent || newMaxPercent != maxPercent)
    		mTipManager.UpdatePercentMinMax(newMinPercent, newMaxPercent);
    	
    	mTipManager.CalculateTips();

    	//UpdateResultsTable();
    }
	
	public void UpdateResultsTable()
	{
		/*
		//Clear the children of the scroll view
		mResultsView.removeViews(0, mResultsView.getChildCount());
		
		// Now generate the new layouts
		ArrayList<BigDecimal> percents = mTipManager.GetPercentages();
		ArrayList<BigDecimal> tips = mTipManager.GetTips();
		ArrayList<BigDecimal> totals = mTipManager.GetTotals();
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
		*/
	}
	
	@Override
	public void preTipCalculation()
	{
		// TODO: Add some kind of loading indicator here
		mLoadingView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onTipCalculation(/*BigDecimal percent, BigDecimal amount, BigDecimal total*/) {
		// TODO Auto-generated method stub
		mLoadingView.setVisibility(View.GONE);
		
		mTipList.addAll(mTipManager.mTips);
		//mTipList.add(new Tip(percent, amount, total));
		mTipAdapter.notifyDataSetChanged();
	}
	
	
	public class TipAdapter extends ArrayAdapter<Tip> {

		public TipAdapter(Context context, int textViewResourceId) {
		    super(context, textViewResourceId);
		    // TODO Auto-generated constructor stub
		}

		private List<Tip> items;

		public TipAdapter(Context context, int resource, List<Tip> items) {

		    super(context, resource, items);

		    this.items = items;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

		    View v = convertView;

		    if (v == null) {

		        LayoutInflater vi;
		        vi = LayoutInflater.from(getContext());
		        v = vi.inflate(R.layout.layout_result, null);

		    }

		    Tip p = items.get(position);

		    if (p != null) {

		        TextView percentText = (TextView) v.findViewById(R.id.percentText);
		        TextView tipAmountText = (TextView) v.findViewById(R.id.tipAmountText);
		        TextView totalText = (TextView) v.findViewById(R.id.totalText);

		        if (percentText != null) {
		        	percentText.setText(p.getPercent().toPlainString());
		        }
		        if (tipAmountText != null) {

		        	tipAmountText.setText(p.getAmount().toPlainString());
		        }
		        if (totalText != null) {

		        	totalText.setText(p.getTotal().toPlainString());
		        }
		    }

		    return v;

		}
		
	}
}
