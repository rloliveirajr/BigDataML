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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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




public class Ladoop {
        
	public static class Map extends Mapper<Text, Text, Text, IntWritable> {
	  
		//private
		private final static IntWritable one = new IntWritable(1);
	    private LAC classifier = new LAC("/home/hduser/dilma_00.lac");
	    private Path[] localFiles;
		private final static Text missesText = new Text("misses");
		private final static Text hitsText = new Text("hits");
		
	   /*public void configure(JobConf job) {
	    	try {
	    		localFiles = DistributedCache.getLocalCacheFiles(job);
	    		InputStream file = new FileInputStream(localFiles[0].toString());
	    		InputStream buffer = new BufferedInputStream( file );
	    		ObjectInput input = new ObjectInputStream( buffer );
	    		classifier = (LAC) input.readObject(); 
	        } catch (Exception e) {
	        	//do something
	        	e.printStackTrace();
	        }
	    
	    }*/

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
		 private int 	value 		= 0;
	
		 public void reduce(Text key, Iterable<IntWritable> results, Context context) 
				 throws IOException, InterruptedException {
			 
			 for (IntWritable result : results) {
				 value +=  result.get();
			 }
			 System.out.println(value);
			 context.write(key, new IntWritable(value));
	
		 }
	 }
        
	public static void main(String[] args) throws Exception {
		//Train lac
	   	/*LAC classifier = new LAC(); 
	    try {
	    	
	    	FileReader fr = new FileReader(args[2]);
	    	BufferedReader buffer = new BufferedReader(fr);
	    	classifier.buildClassifierFromLacStyle(buffer);
	    	
	    	FileOutputStream fileOutput = new FileOutputStream("/tmp/LacIndex.obj");
	    	ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
	
	    	objectOutput.writeObject(classifier);
	    	objectOutput.close();
	     	System.out.println("Lac Trained");
	     	
	     	
	     	
	    } catch (Exception e) { 
	    	//do something
	    	e.printStackTrace();
	    	System.out.println("Cannot train lac");
	    }*/	
	    
	    Configuration conf = new Configuration();
	    
		Job job = new Job(conf, "Ladoop");
		
		//DistributedCache.addCacheFile(new URI("/tmp/LacIndex.obj"), conf);
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
	}
        
}