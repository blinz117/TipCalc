package com.blinz117.testapp;

import java.math.BigDecimal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	static final String STATE_TEXT = "userValue";

	TextView mTextDisplay;
	//TextView mResultDisplay;
	CharSequence mCurrText;
	
	TextView mTip15;
	TextView mSum15;
	TextView mTip20;
	TextView mSum20;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sandbox);
        
        //mResultDisplay = (TextView)findViewById(R.id.result_display);
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
    
    public void InsertText(View view)
    {
    	//@TODO: When the text is too big for the screen, don't add any more text
    	Button clicked = (Button)view;
    	
    	// prevent leading zeros
    	if (mCurrText.length() == 0 && clicked.getId() == R.id.button0)
    		return;
    	
    	mCurrText = mCurrText + clicked.getText().toString();
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();
    }
    
    public void ClearText(View view)
    {
    	mCurrText = "";
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();
    }
    
    public void UndoText(View view)
    {
    	int iEndChar = mCurrText.length()-1;
    	if (iEndChar < 0)
    		return;
    	mCurrText = mCurrText.subSequence(0, iEndChar);
    	mTextDisplay.setText(ConvertToDisplayText(mCurrText));
    	UpdateResult();
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
    
//    public void UpdateResult()
//    {
//    	double currValue = Double.parseDouble(mTextDisplay.getText().toString());
//    	double fifteenResult = currValue * 0.15;
//    	CharSequence result = "15%: " + round(fifteenResult,2);
//    	result = result + " (" + (round((currValue + fifteenResult), 2)) + ")";
//    	mResultDisplay.setText(result);
//    }
    
    public void UpdateResult()
    {
    	double currValue = Double.parseDouble(mTextDisplay.getText().toString());
    	
    	double dTip15 = currValue * 0.15;
    	mTip15.setText(round(dTip15,2).toPlainString());
    	mSum15.setText(" (" + (round((currValue + dTip15), 2)) + ")");
    	
    	double dTip20 = currValue * 0.20;
    	mTip20.setText("" + round(dTip20,2));
    	mSum20.setText(" (" + (round((currValue + dTip20), 2)) + ")");
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
    
}
