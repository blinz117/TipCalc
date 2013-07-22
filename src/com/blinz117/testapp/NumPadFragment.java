package com.blinz117.testapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TableLayout;

public class NumPadFragment extends Fragment {
	
	/*
	 * NumPadListener Interface
	 * Defines methods for the NumPadListener (a.k.a. the activity hosting this fragment)
	 */
    // Container Activity must implement this interface
    public interface NumPadListener {
        public void OnNumberClicked(CharSequence value);
        public void OnUndoButtonClicked();
        public void OnClearButtonPressed();
    }
	
    NumPadListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NumPadListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NumPadListener");
        }
    }
    
    /*
     * Button click handling
     */
    View.OnClickListener NumButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	    	Button clicked = (Button)v;
	    	mListener.OnNumberClicked(clicked.getText());
		}
	};
    
    View.OnClickListener UndoButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	    	mListener.OnUndoButtonClicked();
		}
	};
	
    View.OnClickListener ClearButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	    	mListener.OnClearButtonPressed();
		}
	};
	
    /*
     * (non-Javadoc)
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * 
     * Creation and initialization
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	TableLayout mLayout = (TableLayout) inflater.inflate(R.layout.fragment_numpad, container, false);
    	
    	Button[] mNumButtons = {
	    	(Button)mLayout.findViewById(R.id.button1),
	    	(Button)mLayout.findViewById(R.id.button2),
	    	(Button)mLayout.findViewById(R.id.button3),
	    	(Button)mLayout.findViewById(R.id.button4),
	    	(Button)mLayout.findViewById(R.id.button5),
	    	(Button)mLayout.findViewById(R.id.button6),
	    	(Button)mLayout.findViewById(R.id.button7),
	    	(Button)mLayout.findViewById(R.id.button8),
	    	(Button)mLayout.findViewById(R.id.button9),
	    	(Button)mLayout.findViewById(R.id.button0),
    	};
    	Button ButtonUndo = (Button)mLayout.findViewById(R.id.buttonUndo);
    	Button ButtonClear = (Button)mLayout.findViewById(R.id.buttonClear);
    	
    	for (int ndx = 0; ndx < 10; ndx++)
    	{
    		mNumButtons[ndx].setOnClickListener(NumButtonClick);
    	}
    	
    	ButtonClear.setOnClickListener(ClearButtonClick);
    	ButtonUndo.setOnClickListener(UndoButtonClick);
    	
    	return mLayout;
    }
   
}