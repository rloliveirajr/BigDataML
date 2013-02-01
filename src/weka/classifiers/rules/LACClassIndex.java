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
import java.util.Set;

/**
 * Contains a bidirectional map for indexing all classes present in training
 * set. Each class is represented as an integer.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACClassIndex implements Serializable
{
	private static final long serialVersionUID = 7710800772809571155L;

	private LACBidirectionalMap<Integer, LACClass> indexed = new LACBidirectionalMap<Integer, LACClass>(1000);

	/**
	 * Gets a {@link LACClass} by its index.
	 * 
	 * @param index
	 */
	LACClass getClass(int index)
	{
		return indexed.get(index);
	}

	/**
	 * Gets all indexed classes.
	 * 
	 */
	Set<Integer> getAllClasses()
	{
		return indexed.keySet();
	}

	/**
	 * Indexes the given class
	 * 
	 * @param clazz
	 */
	int indexOf(LACClass clazz)
	{
		Integer index = indexed.reverseGet(clazz);
		if (index == null)
		{
			index = indexed.size();
			indexed.put(index, clazz);
		}
		return index;
	}
}
