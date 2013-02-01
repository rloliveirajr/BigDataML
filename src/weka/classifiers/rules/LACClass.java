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

/**
 * Represents the class (label) of a {@link LACInstance}
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LACClass extends LACFeature
{
	private static final long serialVersionUID = -2094728160319321936L;

	/**
	 * Constructs a class by its label and relative position in {@link Instance}
	 * 
	 * @param label
	 * @param position
	 */
	LACClass(String label, int position, boolean considerPosition)
	{
		super(label, position, considerPosition);
	}
}
