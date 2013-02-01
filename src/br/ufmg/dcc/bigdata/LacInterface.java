package br.ufmg.dcc.bigdata;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.LAC;
import weka.core.Instance;
import weka.core.Instances;

public class LacInterface {

	LAC lac;
	Instances train;
	int misses;
	int hits;
	
	public LacInterface(Instances train, String[] options) throws Exception{
		this.train = train;
		
		this.lac = new LAC();
		this.lac.setOptions(options);
		this.lac.buildClassifier(train);
	}	
	
	public void classify(Instances testSet) throws Exception{
		int nInstances = testSet.numInstances();
		
		for(int i = 0; i < nInstances; i++){
			Instance test = testSet.get(i);
			double classIndex = lac.classifyInstance(test);
			
			test.setClassValue(classIndex);
		}		
	}
	
	public int getCacheMisses(){
		return  lac.getCacheMisses();
	}
	
	public int getCacheHits(){
		return lac.getCacheHits();
	}
}
