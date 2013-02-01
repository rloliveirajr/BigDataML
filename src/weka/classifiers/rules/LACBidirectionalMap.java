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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bidirectional {@link Map} implementation
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACBidirectionalMap<K, V> extends HashMap<K, V>
{
	private static final long serialVersionUID = 1624099630339084162L;

	private HashMap<V, K> reverseMap;

	/**
	 * Constructs a new instance of {@link LACBidirectionalMap}
	 * 
	 * @param capacity
	 */
	public LACBidirectionalMap(int capacity)
	{
		super(capacity);
		reverseMap = new HashMap<V, K>(capacity);
	}

	/**
	 * Returns a key for a given value.
	 * 
	 * @param value
	 */
	public K reverseGet(V value)
	{
		return reverseMap.get(value);
	}

	/**
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value)
	{
		reverseMap.put(value, key);
		return super.put(key, value);
	}

	/**
	 * More efficient way to discover if the value is present.
	 * 
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		return reverseMap.containsKey(value);
	}

	/**
	 * Returns a set containing all stored values
	 */
	public Set<V> valueSet()
	{
		return reverseMap.keySet();
	}

	/**
	 * Prints the reverse map
	 * 
	 * @return
	 */
	public String reverseString()
	{
		return reverseMap.toString();
	}
}