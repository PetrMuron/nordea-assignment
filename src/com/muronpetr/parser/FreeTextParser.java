package com.muronpetr.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Petr Muron
 *
 * java coding exercise
 * 
 */
public class FreeTextParser {
	
	private static String inFile = "in/small.in";
	private static String xmlOutFile = "out/small.xml";	
	private static String csvOutFile = "out/small.csv";	
	
	private static final byte DOT = '.';

	private static final byte[] SENTENCE_SEPARATORS = {DOT, '!', '?'};
	
	private static final byte[] WORD_SEPARATORS = {' ', ',', '-', '\t', '\r', '\n', '-', '(', ')', ':'};
	
	private static final String[] ACRONYMS = {"Mr", "Ms", "Mrs", "etc"};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//use first argument as input file name, when argument is not set, use defaut
		if(args.length > 0) {
			
			inFile = args[0];
			
		}
		
		//use second argument as xml output file name, when argument is not set, use defaut
		if(args.length > 1) {
			
			xmlOutFile = args[1];
			
		}
		
		//use second argument as csv output file name, when argument is not set, use defaut
		if(args.length > 1) {
			
			csvOutFile = args[1];
			
		}
		
		File xmlFile = new File(xmlOutFile);
		File csvFileTmp = new File(csvOutFile+".tmp");
		
		try {
			
			xmlFile.createNewFile();
			csvFileTmp.createNewFile();
		
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
		try(InputStream inputStream = new FileInputStream(inFile); 
			OutputStream xmlOutputStream = new FileOutputStream(xmlFile);
			OutputStream csvOutputStream = new FileOutputStream(csvFileTmp)) {
			
			//write xml header
			xmlOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<text>\n".getBytes());
			
			Sentence sentence = new Sentence();
					
			List<Byte> word = new ArrayList<Byte>();
			int sentenceCount = 0;
			int wordCount = 0;
			int maxWordCount= 0;
			//read bytes from input stream when available
			while(inputStream.available() > 0) {
				
				int c = inputStream.read();
				
				if(isSentenceSeparator((byte)c) && !(isAcronym(word) && c == DOT)) {
					//when char is sentence separator
					
					if(!word.isEmpty()) {
						
						sentence.addWord(word);
						
						wordCount++;
						
					}
					
					word = new ArrayList<Byte>();
					
					if(!sentence.getWords().isEmpty()) {
						
						//sort words
						sentence.sort();
					
						//write sentence to XML file
						sentence.writeXML(xmlOutputStream);
						
						//write sentence to CSV file
						sentence.writeCSV(csvOutputStream, ++sentenceCount);
						
						sentence = new Sentence();
						
						if(wordCount > maxWordCount) {
							maxWordCount = wordCount;
						}
						
						wordCount = 0;
						
					}
					
				} 
				else if(isWordSeparator((byte)c)) {
					//when char iw word separator add word to sentence 
					
					if(!word.isEmpty()) {
						
						sentence.addWord(word);
						
						wordCount++;
						
					}
					
					word = new ArrayList<Byte>();
					
				}
				else {
					//else add char to word
					
					word.add((byte)c);
					
				}	
				
				
			}
			
			//write last sentence
			if(!sentence.getWords().isEmpty()) {
			
				sentence.sort();
			
				sentence.writeXML(xmlOutputStream);
				
				sentence.writeCSV(csvOutputStream, ++sentenceCount);
				
				if(wordCount > maxWordCount) {
					maxWordCount = wordCount;
				}
				
			}
				
			//write end tag to XML file
			xmlOutputStream.write("</text>".getBytes());	
	
			//prepend header to CSV file
			prependHeader(csvFileTmp, maxWordCount);
				
				
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
		csvFileTmp.delete();
		

	}

	/**
	 * check if byte is sentence separator
	 */
	static private boolean isSentenceSeparator(byte b) {
		
	    for (int i = 0; i < SENTENCE_SEPARATORS.length; i++) {
	       
	        if( b == SENTENCE_SEPARATORS[i] ) {
	        	return true;
	        }
	    	
	    }
		
		return false;
			
	}	

	/**
	 * check if byte is word separator
	 */
	static private boolean isWordSeparator(byte b) {
		
	    for (int i = 0; i < WORD_SEPARATORS.length; i++) {
	       
	        if( b == WORD_SEPARATORS[i] ) {
	        	return true;
	        }
	    	
	    }
		
		return false;
			
	}

	/**
	 * check if word is acronym
	 */
	static private boolean isAcronym(List<Byte> word) {
		
		StringBuilder sb = new StringBuilder();
		
		for(byte c: word) {
			sb.append((char)c);
		}
		
		String s = sb.toString();
		
	    for (int i = 0; i < ACRONYMS.length; i++) {
	    	
	        if( s.equalsIgnoreCase(ACRONYMS[i]) ) {
	        	return true;
	        }

	    }
		
		return false;
			
	}

	/**
	 * method creates header of CSV file and then append temporary CSV file
	 */
	public static void prependHeader(File input, int wordCount) throws IOException {
		
		File csvFile = new File(csvOutFile);
		
		try {
			
			csvFile.createNewFile();
		
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
		try(InputStream inputStream = new FileInputStream(input); 
			OutputStream csvOutputStream = new FileOutputStream(csvFile)) {
			
			csvOutputStream.write(", ".getBytes());
			
			for(int i = 1; i <= wordCount; i++) {
			
				csvOutputStream.write("Word ".getBytes());
				
				csvOutputStream.write(String.valueOf(i).getBytes());
			
				if(i < wordCount) {
					csvOutputStream.write(", ".getBytes());
				}
				else {
					csvOutputStream.write('\n');
				}
			
			}
			
	        while (inputStream.available() > 0) {
	        	csvOutputStream.write(inputStream.read());
	        }
			
		}
	    
	}

}
