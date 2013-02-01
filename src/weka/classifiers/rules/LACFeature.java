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

/**
 * Represents a value present a {@link LACInstance}
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACFeature implements Serializable
{
	private static final long serialVersionUID = 5413248826696525306L;

	private final String label;
	private final int position;
	private final boolean considerPosition;

	/**
	 * @param label
	 * @param position
	 */
	LACFeature(String label, int position, boolean considerPosition)
	{
		this.label = label;
		this.position = position;
		this.considerPosition = considerPosition;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LACFeature)
		{
			LACFeature other = (LACFeature) obj;
			if(considerPosition)
			{
				return other.position == position && label.equals(other.label);
			}
			else
			{
				return label.equals(other.label);
			}
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		return label.hashCode();
	}

	/**
	 * @return the label of the feature
	 */
	public String getLabel()
	{
		return label;
	}

	@Override
	public String toString()
	{
		return label;
	}
}