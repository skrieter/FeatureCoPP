package de.ovgu.spldev.featurecopp.splmodel;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import de.ovgu.spldev.featurecopp.config.Configuration;

/**
 * Stores unique instances of detected feature (expressions).
 * 
 * @author K. Ludwig
 * 
 */
public class FeatureTable {
	/**
	 * Repeated program invocations in batch mode required re-initialization of feature map (per run).
	 */
	public void reinit() {
		featureTable.clear();
		featureTable = null;
		featureTable = new HashMap<String, FeatureModule>();
	}
	/**
	 * Returns a feature corresponding to given feature tree. If none such
	 * feature exists, a new one is created and delivered to the caller. Key for
	 * feature retrieval is indirectly FeatureTree.toString()
	 * 
	 * @param ftree
	 *            preprocessor feature expression ast
	 * @param moduleDir
	 *            directory where to store feature module file
	 * @return feature according to feature tree
	 * @see FeatureTree.toString
	 */
	public FeatureModule get(final FeatureTree ftree,
			final Path moduleDir) {
		String featureExpression = ftree.featureExprToString();
		FeatureModule feature = featureTable.get(featureExpression);
		// if not already present, add feature to table
		if (feature == null) {
			featureTable.put(featureExpression, feature = new FeatureModule(
					ftree, moduleDir));
		}
		return feature;
	}

	public void writeXmlTo(int indent, FileWriter fw) throws Exception {
		if (fw != null) {
			// @formatter:off
			fw.write(String.format(Locale.US, "%" + indent + "s<features count=\"%d\" requested=\"%d\" roles=\"%d\">%s", // linebreak
					Configuration.XML_INDENT_WHITESPACE, getFeatureCount(), calcNumberOfRequestedFeatures(), calcTotalNumberOfRoles(), Configuration.LINE_SEPARATOR));
			// pretty expensive but xml analysis for humans is easier with some kind of order
			// -> see FeatureModule.compare
			Object[] featureValues = featureTable.values().toArray();
			Arrays.sort(featureValues);
			for(Object o : featureValues) {
				FeatureModule fm = (FeatureModule)o;
				fm.writeXmlTo(indent + 1, fw);
			}
			fw.write(String.format(Locale.US, "%" + indent + "s</features>%s", // linebreak
					Configuration.XML_INDENT_WHITESPACE, Configuration.LINE_SEPARATOR));
			// @formatter:on
		}
	}

	/**
	 * Returns amount of currently mapped features.
	 * 
	 * @return amount of currently mapped features
	 */
	public int getFeatureCount() {
		return featureTable.size();
	}

	/**
	 * Counts total amount of user-requested features.
	 * 
	 * @return requested feature count
	 */
	public long calcNumberOfRequestedFeatures() {
		long count = 0;
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested()) {
				count++;
			}
		}
		return count;
	}
	/**
	 * Counts requested number of occurrences (roles)
	 * 
	 * @return requested feature count
	 */
	public long calcNumberOfRequestedRoles() {
		long count = 0;
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested()) {
				count += m.numOfOccurrences();
			}
		}
		return count;
	}
	
	/**
	 * Counts total number of occurrences (roles)
	 * 
	 * @return requested role count
	 */
	public long calcTotalNumberOfRoles() {
		long count = 0;
		for (FeatureModule m : featureTable.values()) {
			count += m.numOfOccurrences();
		}
		return count;
	}
	public class DirectiveCount {
		public long getIfCount() {
			return ifCount;
		}
		public void setIfCount(long ifCount) {
			this.ifCount = ifCount;
		}
		public long getIfdefCount() {
			return ifdefCount;
		}
		public void setIfdefCount(long ifdefCount) {
			this.ifdefCount = ifdefCount;
		}
		public long getIfndefCount() {
			return ifndefCount;
		}
		public void setIfndefCount(long ifndefCount) {
			this.ifndefCount = ifndefCount;
		}
		public long getElifCount() {
			return elifCount;
		}
		public void setElifCount(long elifCount) {
			this.elifCount = elifCount;
		}
		public long getElseCount() {
			return elseCount;
		}
		public void setElseCount(long elseCount) {
			this.elseCount = elseCount;
		}
		private long ifCount;
		private long ifdefCount;
		private long ifndefCount;
		private long elifCount;
		private long elseCount;
	}
	public DirectiveCount countRequestedDirectives(boolean useLoF) {
		DirectiveCount count = new DirectiveCount();
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested()) {
				count.setIfCount(count.getIfCount() + m.numOfIf(useLoF));
				count.setIfdefCount(count.getIfdefCount() + m.numOfIfdef(useLoF));
				count.setIfndefCount(count.getIfndefCount() + m.numOfIfndef(useLoF));
				count.setElifCount(count.getElifCount() + m.numOfElif(useLoF));
				count.setElseCount(count.getElseCount() + m.numOfElse(useLoF));
			}
		}
		return count;
	}
	public DirectiveCount countRequestedSimpleAbsenceDirectives(boolean useLoF) {
		DirectiveCount count = new DirectiveCount();
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested() && m.isSimpleAbsence()) {
				//System.out.println(m.featureTreeToString());
				count.setIfCount(count.getIfCount() + m.numOfIf(useLoF));
				count.setIfdefCount(count.getIfdefCount() + m.numOfIfdef(useLoF));
				count.setIfndefCount(count.getIfndefCount() + m.numOfIfndef(useLoF));
				count.setElifCount(count.getElifCount() + m.numOfElif(useLoF));
				count.setElseCount(count.getElseCount() + m.numOfElse(useLoF));
			}
		}
		return count;
	}
	public DirectiveCount countRequestedSimplePresenceDirectives(boolean useLoF) {
		DirectiveCount count = new DirectiveCount();
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested() && m.isSimplePresence()) {
				//System.out.println(m.featureTreeToString());
				count.setIfCount(count.getIfCount() + m.numOfIf(useLoF));
				count.setIfdefCount(count.getIfdefCount() + m.numOfIfdef(useLoF));
				count.setIfndefCount(count.getIfndefCount() + m.numOfIfndef(useLoF));
				count.setElifCount(count.getElifCount() + m.numOfElif(useLoF));
				count.setElseCount(count.getElseCount() + m.numOfElse(useLoF));
			}
		}
		return count;
	}
	public DirectiveCount countRequestedNonSimpleDirectives(boolean useLoF) {
		DirectiveCount count = new DirectiveCount();
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested() && ! m.isSimplePresence() && ! m.isSimpleAbsence()) {
				//System.out.println(m.featureTreeToString());
				count.setIfCount(count.getIfCount() + m.numOfIf(useLoF));
				count.setIfdefCount(count.getIfdefCount() + m.numOfIfdef(useLoF));
				count.setIfndefCount(count.getIfndefCount() + m.numOfIfndef(useLoF));
				count.setElifCount(count.getElifCount() + m.numOfElif(useLoF));
				count.setElseCount(count.getElseCount() + m.numOfElse(useLoF));
			}
		}
		return count;
	}
	public static class Quadruple<S,T,U,V> {
		public S s;
		public T t;
		public U u;
		public V v;
	}
	public Quadruple<String, String, String, Integer> getTDMax(boolean inclElse) {
		Quadruple<String, String, String, Integer> max = new Quadruple<>();
		max.s = max.t = max.u =  "";
		max.v = 0;
		for(FeatureModule fm : featureTable.values()) {
			// skip #else features introduced by #else directives:
			// if a feature is not exclusively introduced by #else
			// its TD cannot be larger than of regular directives
			if(! inclElse && fm.isElse() || !fm.isRequested()) {
				continue;
			}
			int td = fm.getTD();
			if(max.v < td) {
				max.s = fm.featureTreeToString();
				max.t = fm.getKeywordFromFeatureTree();
				// take first file as example occurrence (role)
				max.u = fm.getFilenameAt(0);
				max.v = td;
			}
		}
		return max;
	}

	public long summarizeTanglingDegree(boolean inclElse) {
		long count = 0;
		for (FeatureModule m : featureTable.values()) {
			if (m.isRequested()) {
				count += inclElse ? m.sumTanglingDegreeWithElse() : m.sumTanglingDegreeWithoutElse();
			}
		}
		return count;
	}
	/** FeatureTree.featureExpressionToString() => Feature */
	public HashMap<String, FeatureModule> featureTable = new HashMap<String, FeatureModule>();
}
