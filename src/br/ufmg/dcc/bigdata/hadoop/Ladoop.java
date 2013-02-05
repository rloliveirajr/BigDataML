package br.ufmg.dcc.bigdata.hadoop;

import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.filecache.DistributedCache;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        
	
	public static class Map extends Mapper<LongWritable, Text, IntWritable, Result> {
	  
		//private
		private final static IntWritable one = new IntWritable(1);
	    private LAC classifier = new LAC();
	    private Path[] localFiles;

	    public void configure(JobConf job) {
	    	try {
	    		localFiles = DistributedCache.getLocalCacheFiles(job);
	    		InputStream file = new FileInputStream(localFiles[0].toString());
	    		InputStream buffer = new BufferedInputStream( file );
	    		ObjectInput input = new ObjectInputStream( buffer );
	    		classifier = (LAC) input.readObject(); 
	        } catch (Exception e) {
	        	//do something
	        }
	    
	    }

	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		    String line = value.toString();
		    try 
		    {
			    this.classifier.distributionForInstance(line.split(" "));
			    Result result = new Result();
		        context.write(one, result);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
	    }
	 } 	
	

        
 public static class Reduce extends Reducer<IntWritable, Result, Text, IntWritable> {
	 private int 	misses 		= 0;
	 private int 	hits 		= 0;

	 public void reduce(Text key, Iterable<Result> results, Context context) 
			 throws IOException, InterruptedException {
		 
		 for (Result result : results) {
			 misses +=  result.getMisses();
			 hits   +=  result.getHits();
		 }
		 
		 context.write( new Text("misses"), new IntWritable(misses));
		 context.write( new Text("hits"), new IntWritable(hits));

	 }
 }
        
 public static void main(String[] args) throws Exception {
	 //Train lac
   	Path path = new Path(args[3]);
   	LAC classifier = new LAC(); 
    try {
    	FileSystem fs  = FileSystem.get(new Configuration());
    	BufferedReader buffer = new BufferedReader(new InputStreamReader(fs.open(path)));
    	classifier.buildClassifierFromLacStyle(buffer);
    	
    	
    	
    	FileOutputStream fileOutput = new FileOutputStream("/tmp/LacIndex.obj");
    	ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

    	objectOutput.writeObject(classifier);
    	objectOutput.close();
    	
    } catch (Exception e) { 
    	//do something
    	System.out.println("Cannot train lac");
    }	
	 
    Configuration conf = new Configuration();

    
	Job job = new Job(conf, "ladoop");
	
	DistributedCache.addCacheFile(new URI("/tmp/LacIndex.obj"), conf);
    
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