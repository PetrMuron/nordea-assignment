package com.muronpetr.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sentence {
	
	private List<List<Byte>> words;
	
	public Sentence() {
		words = new ArrayList<List<Byte>>();
	}

	public List<List<Byte>> getWords() {
		return words;
	}

	public void setWords(List<List<Byte>> words) {
		this.words = words;
	}
	
	public void addWord(List<Byte> word) {
		words.add(word);
	}
	
	/**
	 * method sort words in sentence ascending
	 */
	public void sort() {
		
		Collections.sort(words, new Comparator<List<Byte>>() {
		    @Override
		    public int compare(List<Byte> o1, List<Byte> o2) {

		    	for(int i = 0; i< o1.size(); i++) {
		    		
		    		if(i >= o2.size()) return 1;
		    		
		    		char ch1 = Character.toLowerCase((char)o1.get(i).byteValue());
		    		char ch2 = Character.toLowerCase((char)o2.get(i).byteValue());
		    		
		    		if(ch1 != ch2) {
		    			return ch1-ch2;
		    		}
		    		
		    	}
		    	
		    	if(o2.size() > o1.size()) {
		    		return -1;
		    	}
		    	else {
		    		return 0;
		    	}
		    	
		    }
		});
		
	}
	
	/**
	 * method writes sentence as XML element to outputStream
	 */
	public void writeXML(OutputStream outputStream) throws IOException {	
		
		outputStream.write("    <sentence>\n".getBytes());
		
		for(List<Byte> word:words) {

			outputStream.write("        <word>".getBytes());

			for(Byte character:word) {
				outputStream.write(character);
			}

			outputStream.write("</word>\n".getBytes());

		}

		outputStream.write("    </sentence>\n".getBytes());

	}

	/**
	 * method writes sentence as CSV line to outputStream
	 */
	public void writeCSV(OutputStream outputStream, int sentenceNo) throws IOException {	
		
		outputStream.write("Sentence ".getBytes());
		
		outputStream.write(String.valueOf(sentenceNo).getBytes());
		
		outputStream.write(", ".getBytes());
		
		for(int i = 0; i < words.size(); i++) {

			for(Byte character:words.get(i)) {
				outputStream.write(character);
			}

			if(i < words.size() - 1) {
				outputStream.write(", ".getBytes());
			}
			else {
				outputStream.write('\n');
			}

		}

	}

}
