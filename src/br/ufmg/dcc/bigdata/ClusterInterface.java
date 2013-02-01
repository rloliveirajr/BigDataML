package br.ufmg.dcc.bigdata;

import weka.clusterers.AbstractClusterer;
import weka.clusterers.Clusterer;
import weka.core.Instances;

public class ClusterInterface {

	Clusterer cluster;
	
	public ClusterInterface(String clusterAlgorithm, String[] options) throws Exception{
		cluster = AbstractClusterer.forName(clusterAlgorithm, options);
	}
	
	public int[] clustererTrain(Instances train) throws Exception{
		cluster.buildClusterer(train);
	}
}
