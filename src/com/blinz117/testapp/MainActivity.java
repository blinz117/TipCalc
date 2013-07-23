package com.blinz117.testapp;

import java.math.BigDecimal;

import com.blinz117.testapp.NumPadFragment.NumPadListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity
	implements NumPadListener{
	
	static final String STATE_TEXT = "userValue";

	TextView mTextDisplay;
	CharSequence mCurrText;
	
	TextView mTip15;
	TextView mSum15;
	TextView mTip20;
	TextView mSum20;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        
        mTextDisplay = (TextView)findViewById(R.id.text_display);
        
        mTip15 = (TextView)findViewById(R.id.tip15);
        mSum15 = (TextView)findViewById(R.id.sum15);
        mTip20 = (TextView)findViewById(R.id.tip20);
        mSum20 = (TextView)findViewById(R.id.sum20);
        
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
    
    public void UpdateResult()
    {
    	double currValue = Double.parseDouble(mTextDisplay.getText().toString());
    	
    	double dTip15 = currValue * 0.15;
    	mTip15.setText(round(dTip15,2).toPlainString());
    	mSum15.setText("" + round((currValue + dTip15), 2));
    	
    	double dTip20 = currValue * 0.20;
    	mTip20.setText("" + round(dTip20,2));
    	mSum20.setText("" + round((currValue + dTip20), 2));
    }
    
    public static BigDecimal round(double value, int places)
    {
        BigDecimal result = new BigDecimal(Double.toString(value));
        result = result.setScale(places, BigDecimal.ROUND_HALF_UP);       
        return result;
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
    	int iEndChar = mCurrText.length()-1;
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
    
}
