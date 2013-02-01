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

import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

/**
 * Implements the Lazy Associative Classifier for Weka environment.
 * 
 * @author Gesse Dafe (Java implementation)
 * @author Adriano Veloso (algorithm and original C++ implementation)
 */
public class LAC extends AbstractClassifier implements TechnicalInformationHandler, OptionHandler
{
	private static final long serialVersionUID = 4740958383832856257L;

	private double minConfidence = 0;
	private double minSupport = 0;
	private int maxRuleSize = 4;

	private LACInstances trainingInstances;
	private LACRules rules;

	@Override
	public void buildClassifier(Instances data) throws Exception
	{
		boolean considerFeaturePositions = checkNominalAttributes(data);
		this.trainingInstances = new LACInstances(considerFeaturePositions);
		int numInstances = data.numInstances();
		
		for (int i = 0; i < numInstances; i++)
		{
			Instance wekaInstance = data.get(i);
			LACInstance trainingInstance = trainingInstances.createNewTrainingInstance();
			populateInstance(wekaInstance, trainingInstance, true);
		}
		
		this.rules = this.trainingInstances.prepare(maxRuleSize - 1, minSupport, minConfidence, considerFeaturePositions, getDebug());
	}

	@Override
	public double[] distributionForInstance(Instance wekaInstance) throws Exception
	{
		LACInstance testInstance = new LACInstance(trainingInstances);
		populateInstance(wekaInstance, testInstance, false);
		
		double[] probs = rules.calculateProbabilities(testInstance);
		double[] result = new double[wekaInstance.classAttribute().numValues()];
		
		for (int i = 0; i < probs.length; i++)
		{
			String value = trainingInstances.getClassByIndex(i).getLabel();
			int index = wekaInstance.classAttribute().indexOfValue(value);
			if(index >= 0)
			{
				result[index] = probs[i];
			}
		}
		
		return result;
	}

	/**
	 * Populates a {@link LACInstance} with the contents of an Weka {@link Instance}
	 * @param wekaInstance
	 * @param lacInstance
	 * @param populateClass 
	 */
	private void populateInstance(Instance wekaInstance, LACInstance lacInstance, boolean populateClass)
	{
		int numAtts = wekaInstance.numAttributes();
		for (int i = 0; i < numAtts; i++)
		{
			if(i != wekaInstance.classIndex())
			{
				String label = wekaInstance.toString(i);
				lacInstance.addFeature(label);
			}
			else 
			{
				if(populateClass)
				{
					String clazz = wekaInstance.classAttribute().value((int) wekaInstance.classValue());
					lacInstance.setClass(clazz);
				}
				else
				{
					String clazz = wekaInstance.classAttribute().value((int) wekaInstance.classValue());
					lacInstance.setHiddenClass(clazz);
				}
			}
		}	
	}
	
	/**
	 * Returns true if all attributes are nominal or false
	 * if all of them are string. Throws a runtime exception
	 * for mixed attribute types.
	 * 
	 * @param data
	 * @return
	 */
	private boolean checkNominalAttributes(Instances data)
	{
		boolean hasNominalAtt = false;
		boolean hasStringAtt = false;
		
		for(int i = 0; i < data.numAttributes(); i++)
		{
			if(data.classIndex() != i)
			{
				Attribute att = data.attribute(i);
				hasNominalAtt = hasNominalAtt || att.isNominal();
				hasStringAtt = hasStringAtt || att.isString();
			}
		}
		
		if(hasNominalAtt && hasStringAtt)
		{
			throw new RuntimeException("Lazy Associative Classifiers can only handle datasets were all attributes have the same type. Make sure all attributes are either string or nominal.");
		}
		
		return hasNominalAtt;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Enumeration listOptions()
	{
		Vector newVector = new Vector();
		
		newVector.addElement(
			new Option("\tDetermines the maximum length of a classification rule (its number of features plus 1, because class attribute is also considered). Mining large rules is costly, but sometimes the accuracy gain is worth. The default value for this option is 4.", "M", 1, "-M")
		);
		newVector.addElement(
			new Option("\tDetermines the support threshold for pruning (default: 0).", "S", 1, "-S")
		);
		newVector.addElement(
			new Option("\tDetermines the confidence threshold for pruning (default: 0).", "C", 1, "-C")
		);
		
		return newVector.elements();
	}
	
	@Override
	public void setOptions(String[] options) throws Exception
	{
		super.setOptions(options);

	    String opt = Utils.getOption('M', options);
	    if(opt != null && opt.trim().length() > 0)
	    {
	    	this.maxRuleSize = Integer.parseInt(opt);
	    }
	    
	    opt = Utils.getOption('C', options);
	    if(opt != null && opt.trim().length() > 0)
	    {
	    	this.minConfidence = Double.parseDouble(opt);
	    }
	    
	    opt = Utils.getOption('S', options);
	    if(opt != null && opt.trim().length() > 0)
	    {
	    	this.minSupport = Double.parseDouble(opt);
	    }
	}

	@Override
	public String[] getOptions()
	{
		return new String[] {
			"-M", "" + this.maxRuleSize, 
			"-C", "" + this.minConfidence, 
			"-S", "" + this.minSupport
		};
	}
	
	@Override
	public Capabilities getCapabilities()
	{
	    Capabilities result = super.getCapabilities();
	    
	    result.disableAll();
	    result.enable(Capability.STRING_ATTRIBUTES);
	    result.enable(Capability.NOMINAL_ATTRIBUTES);
	    result.enable(Capability.MISSING_VALUES);
	    result.enable(Capability.NOMINAL_CLASS);
	    result.setMinimumNumberInstances(1);
	    
	    return result;
	}
	
	@Override
	public TechnicalInformation getTechnicalInformation()
	{
		TechnicalInformation result = new TechnicalInformation(Type.ARTICLE);
		result.setValue(Field.AUTHOR, "Adriano Veloso and Wagner Meira Jr. and Mohammed Zaki");
		result.setValue(Field.YEAR, "2006");
		result.setValue(Field.TITLE, "Lazy Associative Classification");
		result.setValue(Field.JOURNAL, "ICDM '06 Proceedings of the Sixth International Conference on Data Mining");
		result.setValue(Field.PAGES, "645-654");
		result.setValue(Field.PUBLISHER, "IEEE Computer Society Washington, DC, USA");
		result.setValue(Field.ISBN, "0-7695-2701-9");
		return result;
	}

	public double getMinConfidence()
	{
		return minConfidence;
	}

	public void setMinConfidence(double minConfidence)
	{
		this.minConfidence = minConfidence;
	}

	public double getMinSupport()
	{
		return minSupport;
	}

	public void setMinSupport(double minSupport)
	{
		this.minSupport = minSupport;
	}

	public int getMaxRuleSize()
	{
		return maxRuleSize;
	}

	public void setMaxRuleSize(int maxRuleSize)
	{
		this.maxRuleSize = maxRuleSize;
	}
	
	public int getCacheMisses(){
		return this.rules.getCacheMisses();
	}
	
	public int getCacheHits(){
		return this.rules.getCacheHits();
	}

	/**
	 * Enables tool tip text popups for the minConfidence parameter, when listed in the
	 * "About" popup window.
	 */
	public String minConfidenceTipText()
	{
		return "Imposes a confidence threshold for pruning classification " +
				"rules: only rules with confidence greater or equal to this value will " +
				"be considered. Mining all possible rules is costly, but sometimes " +
				"the accuracy gain is worth. The default value for this parameter is 0 " +
				"(zero).";
	}
	
	/**
	 * Enables tool tip text popups for the minSupport parameter, when listed in the
	 * "About" popup window.
	 */
	public String minSupportTipText()
	{
		return "Imposes a support threshold for pruning classification " +
				"rules: only rules with support greater or equal to this value will " +
				"be considered. Mining all possible rules is costly, but sometimes " +
				"the accuracy gain is worth. The default value for this parameter is 0 " +
				"(zero).";
	}
	
	/**
	 * Enables tool tip text popups for the maxRuleSize parameter, when listed in the
	 * "About" popup window.
	 */
	public String maxRuleSizeTipText()
	{
		return "Prunes classification rules by imposing a maximum number of features in each rule. " +
				"The class attribute is also considered, e.g. a rule of size 4 is a rule having three " +
				"attributes and one class. Mining large rules is costly, but sometimes " +
				"the accuracy gain is worth. The default value for this option is 4.";
	}
	
	/**
	 * Enables "About" and "Capabilities" info to appear in the
	 * GenericObjectEditor in the GUI.
	 */
	public String globalInfo()
	{
		return "Implements the LAC (Lazy Associative Classifier) algorithm, " +
				"which uses associative rules to execute classifications. " +
				"Unlike other Apriori-based classifiers, LAC algorithm computes " +
				"association rules in a demand-driven basis. For each instance " +
				"to be classified, it filters the training set and produces only " +
				"useful rules for that instance, outperforming traditional associative " +
				"classifiers in both time and accuracy. For more information: " +
				"[Adriano Veloso, Wagner Meira Jr., Mohammed Zaki. " +
				"Lazy Associative Classification. ICDM '06 Proceedings of the " +
				"Sixth International Conference on Data Mining, Pages 645-654, " +
				"IEEE Computer Society Washington, DC, USA].";
	}
	
	@Override
	public String toString()
	{
		StringBuilder st = new StringBuilder("Lazy Associative Classifier.");
		
		if(this.trainingInstances != null)
		{
			st.append(" Running with ");
			st.append(this.trainingInstances.length());
			st.append(" instances and ");
			st.append(this.trainingInstances.getAllClasses().size());
			st.append(" distinct classes.");
		}
		
		st.append(" Options: ");
		String[] ops = getOptions();
		for (int i = 0; i < ops.length; i++)
		{
			st.append(ops[i]);
			st.append(" ");
		}
		return st.toString();
	}
}