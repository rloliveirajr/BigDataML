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
import java.util.List;

/**
 * Implements a training or test instance
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACInstance implements Serializable
{
	private static final long serialVersionUID = 3252127156840855194L;

	private static final String UNKNOWN_ATTRIBUTE = "?";

	private final LACInstances instances;
	private List<Integer> featuresBitmap = new ArrayList<Integer>(100);
	private int currentFeaturePosition;
	private int indexedClass = -1;
	private int indexedHiddenClass;

	/**
	 * Creates a new {@link LACInstance} object.
	 */
	public LACInstance(LACInstances instances)
	{
		this.instances = instances;
	}

	/**
	 * Adds a feature to the new {@link LACInstance} being constructed. The given
	 * feature is indexed as an unique integer.
	 * 
	 * @param label
	 */
	public void addFeature(String label)
	{
		if (label == null || label.trim().length() == 0 || label.equals(UNKNOWN_ATTRIBUTE))
		{
			currentFeaturePosition++;
		}
		else
		{
			int index = instances.registerFeature(label, currentFeaturePosition++);
			featuresBitmap.add(index);
		}
	}

	/**
	 * Sets the class of the {@link LACInstance}
	 * 
	 * @param label
	 */
	public void setClass(String label)
	{
		indexedClass = instances.registerClass(label, currentFeaturePosition++);
	}

	/**
	 * Gets the class associated to this instance
	 * 
	 * @return
	 */
	public LACClass getClazz()
	{
		return instances.getClassByIndex(indexedClass);
	}

	/**
	 * Sets the hidden class of the {@link LACInstance}
	 * 
	 * @param label
	 */
	public void setHiddenClass(String label)
	{
		indexedHiddenClass = instances.registerClass(label, currentFeaturePosition++);
	}

	/**
	 * Gets the hidden class associated to this instance
	 * 
	 * @return
	 */
	public LACClass getHiddenClazz()
	{
		return instances.getClassByIndex(indexedHiddenClass);
	}
		
	/**
	 * Returns an array of longs, containing the indexed features of this
	 * instance.
	 */
	List<Integer> getIndexedFeatures()
	{
		return featuresBitmap;
	}

	/**
	 * @return the indexedClass
	 */
	int getIndexedClass()
	{
		return indexedClass;
	}
	
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (int i : featuresBitmap)
		{
			if(!first)
			{
				str.append(",");				
			}
			str.append(instances.getFeatureByIndex(i));
			first = false;
		}
		
		return str.toString();
	}
}