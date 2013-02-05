/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package weka.classifiers.rules;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import br.ufmg.dcc.bigdata.Result;

/**
 * Implements the Lazy Associative Classifier for Weka environment.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LAC implements Serializable {
	private static final long serialVersionUID = 4740958383832856257L;

	private double minConfidence = 0;
	private double minSupport = 0;
	private int maxRuleSize = 4;

	private LACInstances trainingInstances;
	private LACRules rules;
	private Set<String> classes;

	public LAC(){}
	
	public LAC(double minConfidence, double minSupport, int maxRuleSize){
		this.minConfidence = minConfidence;
		this.minSupport = minSupport;
		this.maxRuleSize = maxRuleSize;
	}
	
	public void buildClassifierFromLacStyle(BufferedReader data) throws Exception	{
		classes = new HashSet<String>();
		
		String line = data.readLine();
		String[] instance = line.split(" ");
		boolean considerFeaturePositions = line.contains("w[");
		this.trainingInstances = new LACInstances(considerFeaturePositions);
		
		LACInstance lacInstance = trainingInstances.createNewTrainingInstance();
		populateInstance(instance, lacInstance, true);
		
		while(data.ready())	{
			line = data.readLine();
			instance = line.split(" ");
			LACInstance trainingInstance = trainingInstances.createNewTrainingInstance();
			populateInstance(instance, trainingInstance, true);			
		}
		
		data.close();
		
		this.rules = this.trainingInstances.prepare(maxRuleSize - 1, minSupport, minConfidence, 
				considerFeaturePositions, false);
	}

	public Result distributionForInstance(String[] instance) throws Exception {
		LACInstance testInstance = new LACInstance(trainingInstances);
		populateInstance(instance, testInstance, false);
		
		double[] probs = rules.calculateProbabilities(testInstance);
		
		int cacheHits = rules.getCacheHits();
		int cacheMisses = rules.getCacheMisses();
		String[] labels = new String[classes.size()];
		
		for (int i = 0; i < probs.length; i++) 	{
			String value = trainingInstances.getClassByIndex(i).getLabel();
			labels[i] = value;
		}
		
		Result result = new Result(cacheMisses, cacheHits, labels, probs);
		
		return result;
	}

	/**
	 * Populates a {@link LACInstance} with the contents of an Weka {@link Instance}
	 * @param wekaInstance
	 * @param lacInstance
	 * @param populateClass 
	 */
	private void populateInstance(String[] instance, LACInstance lacInstance, boolean populateClass) {
		String clazz = instance[1];
		
		classes.add(clazz);
		
		if(populateClass){
			lacInstance.setClass(clazz);
		}else{
			lacInstance.setHiddenClass(clazz);
		}
		
		for(int i = 2; i < instance.length; i++){
			lacInstance.addFeature(instance[i]);
		}	
	}
	
	public double getMinConfidence() {
		return minConfidence;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public int getMaxRuleSize() {
		return maxRuleSize;
	}
}