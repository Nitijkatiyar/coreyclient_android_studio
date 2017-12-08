package com.coremobile.coreyhealth.newui;

import java.util.ArrayList;

public class Row implements DashBoardObjects {

    private ArrayList<RowDisplayObject> mRowDisplayObjects = new ArrayList<RowDisplayObject>();

    

    public void add(RowDisplayObject objects) {
        mRowDisplayObjects.add(objects);
    }

    public void remove(RowDisplayObject objects) {
        mRowDisplayObjects.remove(objects);
    }

    @Override
    public void addAttributes(String key, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void retrieveAttributes(String key) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void retrieveObjectList() {
        // TODO Auto-generated method stub
        
    }
    public ArrayList<RowDisplayObject> getobjectlist(){
        return mRowDisplayObjects;
    }
    public RowDisplayObject getObject(int index) {
       return(mRowDisplayObjects.get(index)); // TODO Auto-generated method stub
        
    }
    public int size()
    {
    	int size=mRowDisplayObjects.size();
    	return size;
    }
}
