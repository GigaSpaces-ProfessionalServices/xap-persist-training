package com.c123.billbuddy.custom;

import java.util.ArrayList;

import com.gigaspaces.datasource.DataIterator;

public class CustomDataIterator implements DataIterator<Object>{

	private ArrayList<Object> items;
	private int currentIndex = 0;
	
	public CustomDataIterator(ArrayList<Object> input){
		items = input;
		currentIndex = 0;
	}
	
	@Override
	public boolean hasNext() {
		if (currentIndex >= items.size()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object next() {
		return items.get(currentIndex++);
	}

	@Override
	public void remove() {
		items.remove(--currentIndex);
	}

	@Override
	public void close() {
		
	}

    public DataIterator<Object> iterator() {
        return new CustomDataIterator(null);
    }
	
}
