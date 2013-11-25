package com.diycircuits.microime;

import java.util.ArrayList;
import android.util.Log;

public class Keyboard {

    private ArrayList<KeyRow> mKeyRow = new ArrayList<KeyRow>();

    private String mName = "";

    private int mColumn = 0;

    public void addRow(KeyRow row) {
	mKeyRow.add(row);
	if (row.getColumn() > mColumn) mColumn = row.getColumn();
    }

    public KeyRow getRow(int row) {
	return mKeyRow.get(row);
    }
    
    public void setName(String name) {
	mName = name;
    }

    public String getName() {
	return mName;
    }

    public int getColumn() {
	return mColumn;
    }
    
    public int getRow() {
	return mKeyRow.size();
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("{ name = ");
	sb.append(mName);
	sb.append(", ");
	sb.append(mKeyRow.toString());
	sb.append("}");

	return sb.toString();
    }

}
