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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The data (list of {@link LACInstance}s) used to train or test the classifier.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACInstances implements Serializable
{
	private static final long serialVersionUID = -1049032103955322561L;

	private List<LACInstance> instances = new ArrayList<LACInstance>(50000);
	private LACFeatureIndex featureIndex = new LACFeatureIndex();
	private LACClassIndex classIndex = new LACClassIndex();
	private LACFeatureOccurrences featureOccurrences = new LACFeatureOccurrences();
	private LACClassOccurrences classOccurrences = new LACClassOccurrences();
	private LACRules rules;
	private boolean considerFeaturePosition;

	public LACInstances(boolean considerFeaturePosition)
	{
		this.considerFeaturePosition = considerFeaturePosition;
	}
	
	/**
	 * Returns the instance stored in the given position.
	 * 
	 * @param position
	 */
	public LACInstance getInstance(int position)
	{
		return instances.get(position);
	}

	/**
	 * Creates a new {@link LACInstance}
	 * 
	 * @param instance
	 */
	public LACInstance createNewTrainingInstance()
	{
		LACInstance instance = new LACInstance(this);
		this.instances.add(instance);
		return instance;
	}

	/**
	 * Gets the total number of instances.
	 * 
	 */
	public int length()
	{
		return instances.size();
	}

	/**
	 * Preapares {@link LACInstances} to beused by {@link LAC}.
	 * @param maxRuleSize
	 * @param minSupport
	 * @param minConfidence
	 * @param debug 
	 * @return
	 * @throws Exception 
	 */
	LACRules prepare(int maxRuleSize, double minSupport, double minConfidence, boolean considerFeaturePosition, boolean debug) throws Exception
	{
		if (this.rules == null)
		{
			this.classOccurrences.createMap(this);
			this.featureOccurrences.createMap(this);
			this.considerFeaturePosition = considerFeaturePosition;
			this.rules = new LACRules(this, maxRuleSize, minSupport, minConfidence, debug);
		}

		return rules;
	}

	/**
	 * Indexes a class.
	 * 
	 * @param label
	 * @param position
	 * @return the index of the given class
	 */
	int registerClass(String label, int position)
	{
		LACClass clazz = new LACClass(label, position, considerFeaturePosition);
		return classIndex.indexOf(clazz);
	}

	/**
	 * Indexes a feature
	 * 
	 * @param label
	 * @param position
	 * @return the index of the given feature
	 */
	int registerFeature(String label, int position)
	{
		LACFeature feature = new LACFeature(label, position, considerFeaturePosition);
		return featureIndex.indexOf(feature);
	}

	/**
	 * Gets the class by its index. Useful to get the class based on the
	 * calculated probabilities.
	 * 
	 * @param index
	 */
	public LACClass getClassByIndex(int index)
	{
		return classIndex.getClass(index);
	}
	
	/**
	 * Gets the feature by its index.
	 * 
	 * @param index
	 */
	LACFeature getFeatureByIndex(int index)
	{
		return featureIndex.getFeature(index);
	}

	/**
	 * Gets all classes present in the training set
	 */
	Set<Integer> getAllClasses()
	{
		return classIndex.getAllClasses();
	}

	/**
	 * Returns the index of a given class
	 * 
	 * @param clazz
	 */
	int indexOfClass(LACClass clazz)
	{
		return classIndex.indexOf(clazz);
	}

	/**
	 * Gets all instances that have all given features
	 * 
	 * @param featuresIndexes
	 */
	List<Integer> getInstancesWithFeatures(List<Integer> featuresIndexes)
	{
		List<Integer> result = featureOccurrences.instancesWithFeatures(featuresIndexes);
		if(result == null)
		{
			result = Collections.emptyList();
		}
		return result;
	}
	
	/**
	 * Returns the instances that belong to a given class
	 * @param classIndex
	 * @return
	 */
	List<Integer> getInstancesOfClass(int classIndex)
	{
		return classOccurrences.getInstancesOfClass(classIndex);
	}
	
	/**
	 * Returns the labels of the given indexed features
	 * @param features
	 * @return
	 */
	List<String> indexesToLabels(List<Integer> features)
	{
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < features.size(); i++)
		{
			result.add(getFeatureByIndex(features.get(i)).getLabel());
		}
		return result;
	}
}
