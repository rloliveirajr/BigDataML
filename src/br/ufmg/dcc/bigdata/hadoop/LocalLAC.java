package br.ufmg.dcc.bigdata.hadoop;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hadoop.io.IntWritable;

import br.ufmg.dcc.bigdata.Result;


import weka.classifiers.rules.LAC;


public class LocalLAC {
	public static void main(String[] args) {
		 //Train lac
		int misses = 0;
		int hits = 0;
		try {
			LAC classifier = new LAC(args[0]);
			
	    	/*FileReader fr = new FileReader("/home/alessandro/ufmg/baselac/dilma/dilma_00.lac");
	    	BufferedReader buffer = new BufferedReader(fr);
	    	classifier.buildClassifierFromLacStyle(buffer);
	    	
	    	FileOutputStream fileOutput = new FileOutputStream("/tmp/LacIndex.obj");
	    	ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
	    	objectOutput.writeObject(classifier);
	    	objectOutput.close();
	     	ObjectInputStream obj = new ObjectInputStream(new FileInputStream("/tmp/LacIndex.obj"));
  			classifier = (LAC) obj.readObject();
  			obj.close();
			*/
			
	    	FileInputStream fstream = new FileInputStream(args[1]);
	    	DataInputStream in = new DataInputStream(fstream);
	    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    	String line;
	    	while ((line = br.readLine()) != null) {
	    		
		    	Result result = classifier.distributionForInstance(line.split(" "));
		    	misses += result.getMisses();
		    	hits += result.getHits();
	    	}

	    	System.out.println(hits);
	     	System.out.println(misses);
	     	
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.out.println("Cannot test lac");
	    }	
	}

}
