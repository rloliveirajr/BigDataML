package br.ufmg.dcc.bigdata.hadoop;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
public class NonSplittableKeyValueTextInputFormat extends KeyValueTextInputFormat{
	@Override
	protected boolean isSplitable(JobContext context, Path file) {		
		return false;
	}
}
