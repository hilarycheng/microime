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

    private KbContainer mKbContainer = null;
	
    public MicroIME() {
    }

    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	Configuration c = this.getResources().getConfiguration();
    	SystemParams.getInstance().configurationChanged(c, this);
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

	SystemParams.getInstance().configurationChanged(newConfig, this);
    }

    @Override
    public View onCreateCandidatesView() {
    	return null;
    }

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        mKbContainer = (KbContainer) inflater.inflate(R.layout.kb_container, null);

    	return mKbContainer;
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
