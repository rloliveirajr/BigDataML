package br.ufmg.dcc.bigdata.hadoop;

import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import br.ufmg.dcc.bigdata.Result;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URI;

import weka.core.Instances;
import weka.classifiers.rules.LAC;
import org.apache.commons.logging.*;



public class Ladoop {
        
	public static class Map extends Mapper<Text, Text, Text, IntWritable> {
	  
		//private
		private final static IntWritable one = new IntWritable(1);
	    private LAC classifier = new LAC();
		private final static Text missesText = new Text("misses");
		private final static Text hitsText = new Text("hits");
		
		
		 protected void setup(Context context) throws IOException, InterruptedException {
			 super.setup(context);
			 
			 //Load LAC object from disk
			 try {
				 ObjectInputStream obj = new ObjectInputStream(new FileInputStream("/tmp/LacIndex.obj"));
		  		 this.classifier = (LAC) obj.readObject();
		  		 obj.close();
			 } catch (ClassNotFoundException e) {
				 e.printStackTrace();
			 }
		 }  


		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
		    try 
		    {
		    	Result result = this.classifier.distributionForInstance(line.split(" "));
		        context.write(missesText, new IntWritable(result.getMisses()));
		        context.write(hitsText, new IntWritable(result.getHits()));
		    } catch (Exception e) {
		    	System.out.println("MAP ERROR");
		    	e.printStackTrace();
		    }
	    }
		
	 } 	
	

        
	 public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
	
		 public void reduce(Text key, Iterable<IntWritable> results, Context context) 
				 throws IOException, InterruptedException {
			 int 	value 		= 0;

			 for (IntWritable result : results) {
				 value +=  result.get();
			 }
			 System.out.println(value);
			 context.write(key, new IntWritable(value));
	
		 }
	 }
        
	public static void main(String[] args) throws Exception {

		//Train lac
	   	LAC classifier = new LAC(); 
	    try {
	    	
	    	FileReader fr = new FileReader(args[2]);
	    	BufferedReader buffer = new BufferedReader(fr);
	    	classifier.buildClassifierFromLacStyle(buffer);
	    	FileOutputStream fileOutput = new FileOutputStream("LacIndex.obj");
	    	ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
	    	objectOutput.writeObject(classifier);
	    	objectOutput.close();     	
	     	System.out.println("Lac Trained");
	     	
	    } catch (Exception e) { 
	    	//do something
	    	e.printStackTrace();
	    	System.out.println("Cannot train lac");
	    }	
	    Process p = Runtime.getRuntime().exec("bash DistributedCache.sh -send /usr/local/hadoop/conf/slaves LacIndex.obj"); 
	    p.waitFor();
	    
	    
	    Configuration conf = new Configuration();
	    
		Job job = new Job(conf, "Ladoop");
		
		job.setJarByClass(Ladoop.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
	        
		job.setInputFormatClass(NonSplittableKeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	        
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	        
		job.waitForCompletion(true);
		
	    p = Runtime.getRuntime().exec("bash DistributedCache.sh -clear /usr/local/hadoop/conf/slaves LacIndex.obj"); 
	    p.waitFor();
	}
        
}