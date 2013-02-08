package br.ufmg.dcc.bigdata;

import java.io.Serializable;

public class Result implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1199932059686851194L;
	int misses;
	int hits;
	String[] labels;
	double[] probs;
	
	public Result(int misses, int hits, String[] labels, double[] probs) {
		this.misses = misses;
		this.hits = hits;
		this.labels = labels;
		this.probs = probs;
	}

	public Result() {
		this.misses = 0;
		this.hits = 0;
	}
	/**
	 * @return the misses
	 */
	public int getMisses() {
		return misses;
	}

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the labels
	 */
	public String[] getLabels() {
		return labels;
	}

	/**
	 * @return the probs
	 */
	public double[] getProbs() {
		return probs;
	}
}
