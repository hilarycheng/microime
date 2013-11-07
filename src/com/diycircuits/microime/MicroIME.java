package com.diycircuits.microime;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodSubtype;

public class MicroIME extends InputMethodService
{

	public MicroIME() {
		
	}

	@Override
	public View onCreateInputView() {
		return null;
	}

	@Override
	public void setInputView(final View view) {
		
	}
	
	@Override
	public void setCandidatesView(final View view) {
		
	}
	
    @Override
    public void onStartInput(final EditorInfo editorInfo, final boolean restarting) {
    }

    @Override
    public void onStartInputView(final EditorInfo editorInfo, final boolean restarting) {
    }

    @Override
    public void onFinishInputView(final boolean finishingInput) {
    }

    @Override
    public void onFinishInput() {
    }
	
    @Override
    public void onCurrentInputMethodSubtypeChanged(final InputMethodSubtype subtype) {
    }

    @Override
    public void onWindowHidden() {
    }

    @Override
    public void onUpdateSelection(final int oldSelStart, final int oldSelEnd,
            final int newSelStart, final int newSelEnd,
            final int composingSpanStart, final int composingSpanEnd) {
    }

}
