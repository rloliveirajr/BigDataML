package br.ufmg.dcc.bigdata;

public class Result {

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
