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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple way to store and lookup instances that belong to a given class.
 * For each class on training set, we store an entry on a
 * {@link Map}. <br/>
 * This entry is a set of integers. Each integer is the index of an {@link LACInstance}
 * that belongs to that class.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACClassOccurrences implements Serializable
{
	private static final long serialVersionUID = 4471128505943485021L;

	private Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	
	/**
	 * Creates the map of classes for all entries in {@link LACInstances}.
	 * 
	 * @param instances
	 */
	void createMap(LACInstances instances)
	{
		for (int currentPosition = 0; currentPosition < instances.length(); currentPosition++)
		{
			LACInstance currentInstance = instances.getInstance(currentPosition);
			Integer clazz = currentInstance.getIndexedClass();
			if(clazz >= 0)
			{
				List<Integer> instancesByClass = map.get(clazz);
				if (instancesByClass == null)
				{
					instancesByClass = new ArrayList<Integer>();
					map.put(clazz, instancesByClass);
				}
				instancesByClass.add(currentPosition);
			}
		}
	}
	
	/**
	 * Returns the instances that belong to a given class
	 * @param classIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	List<Integer> getInstancesOfClass(int classIndex)
	{
		List<Integer> result = map.get(classIndex);
		return result != null ? result : Collections.EMPTY_LIST;
	}
}
