package com.diycircuits.microime;

import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputConnection;
import android.util.Log;
import android.content.pm.ApplicationInfo;
import java.io.UnsupportedEncodingException;

public class MicroIME extends InputMethodService implements KeyListener, CandidateListener {

    private KbContainer mKbContainer = null;
    private Cangjie mCangjie = null;
    private CandidateView mCandidateView = null;
    private CandidateSelect mCandidateSelect = null;
    // private View mCandidateContainer = null;
	
    public MicroIME() {
    }

    @Override
    public void onCreate() {
    	super.onCreate();

	ApplicationInfo appInfo = getApplicationInfo();
	try {
	    TableLoader.mInstance.setPath(appInfo.dataDir.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException ex) {
	}
	TableLoader.mInstance.initialize();
	TableLoader.mInstance.setInputMethod('2');
	TableLoader.mInstance.enableHongKongChar(true);
	mCangjie = new Cangjie(this);

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

	final KbView view = (KbView) mKbContainer.findViewById(R.id.kbView);
	view.setKeyListener(this);
	
	mCandidateView      = (CandidateView) mKbContainer.findViewById(R.id.candidateView);
	mCandidateSelect    = mCandidateView.getCandidateSelect();
	mCandidateSelect.setContext(this);
	mCangjie.setCandidateSelect(mCandidateSelect);
	mCangjie.setCandidateListener(this);

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

    private void commit(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
	    ic.commitText(text, 1);
	}
    }

    public void keyPressed(char code, KeyType type) {
	Log.i("MicroIME", "Key Pressed " + code + " " + type);
	if (type == KeyType.NORMAL) {
	    if (KeyboardState.getInstance().getCurrentInputMethod() == R.string.qwerty) {
		commit("" + code);
	    } else {
		mCangjie.handleCharacter(0, 0, code);
	    }
	} else if (type == KeyType.SHIFT) {
	    if (KeyboardState.getInstance().getCurrentInputMethod() == R.string.qwerty) {
		KeyboardState.getInstance().toggleShift();
		mKbContainer.invalidate();
	    }
	} else if (type == KeyType.INPUT_METHOD) {
	    KeyboardState.getInstance().toggleInputMethod();
	    mKbContainer.invalidate();
	} else if (type == KeyType.SYMBOLS || type == KeyType.MORESYMBOLS) {
	    KeyboardState.getInstance().toggleSymbol();
	    mKbContainer.invalidate();
	} else if (type == KeyType.DELETE) {
	    if (KeyboardState.getInstance().getCurrentInputMethod() == R.string.qwerty) {
		getCurrentInputConnection().deleteSurroundingText(1, 0);
	    } else if (mCangjie.isEmpty() && KeyboardState.getInstance().getCurrentInputMethod() != R.string.qwerty) {
		getCurrentInputConnection().deleteSurroundingText(1, 0);
	    } else {
		mCangjie.deleteLastCode();
	    }
	} else if (type == KeyType.SPACE) {
	    if (KeyboardState.getInstance().getCurrentInputMethod() == R.string.qwerty) {
		commit(" ");
	    } else {
		if (mCangjie.hasMatch())
		    mCangjie.sendFirstCharacter();
		else if (mCangjie.isEmpty())
		    commit(" ");
	    }
	} else if (type == KeyType.COMMA) {
	    commit(",");
	} else if (type == KeyType.DOT) {
	    commit(".");
	}
    }
    
    public void characterSelected(char c, int idx) {
	Log.i("MicroIME", "Character Selected " + c + " " + idx);
	commit("" + c);
    }
    
    public void phraseSelected(String phrase, int idx) {
	Log.i("MicroIME", "Phrase Selected " + phrase + " " + idx);
	commit(phrase);
    }

}
