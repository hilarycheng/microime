package com.diycircuits.microime;

import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.CompletionInfo;

public class MicroIME extends InputMethodService
{

	private KbView mKbView = null;
	
	public MicroIME() {
		
	}

    @Override
    public void onCreate() {
    	super.onCreate();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
   
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

    @Override
    public View onCreateCandidatesView() {
    	return null;
    }

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        mKbView = (KbView) inflater.inflate(R.layout.kb_view, null);

    	return mKbView;
    }

    @Override
    public void onStartInput(EditorInfo editorInfo, boolean restarting) {
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
    }

    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        super.onFinishCandidatesView(finishingInput);
    }

    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
    }

    @Override
    public void requestHideSelf(int flags) {
        super.requestHideSelf(flags);
    }

}
