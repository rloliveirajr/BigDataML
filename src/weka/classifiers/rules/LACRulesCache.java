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
import java.util.List;

/**
 * A cache to store the extracted associative rules
 * 
 * @author Adriano Veloso (algorithm and original C++ implementation)
 * @author Gesse Dafe (Java implementation)
 */
public class LACRulesCache implements Serializable
{
	private static final long serialVersionUID = -1340440155675141479L;

	private LACLRU<List<Integer>, List<LACRule>> rulesPerFeatures = new LACLRU<List<Integer>, List<LACRule>>(50000);

	/**
	 * Gets all rules that are applicable to instances that have the given
	 * combination of features.
	 * 
	 * @param featuresCombination
	 */
	List<LACRule> getRules(List<Integer> featuresCombination)
	{
		return rulesPerFeatures.get(featuresCombination);
	}

	/**
	 * Stores a list of rules that are applicable to a given combination of
	 * features
	 * 
	 * @param featuresCombination
	 * @param rulesForClass
	 */
	public void storeRules(List<Integer> featureCombinations, List<LACRule> rulesForClass)
	{
		rulesPerFeatures.put(featureCombinations, rulesForClass);
	}
}