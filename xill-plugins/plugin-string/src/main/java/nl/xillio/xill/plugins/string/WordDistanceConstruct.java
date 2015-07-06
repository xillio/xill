package nl.xillio.xill.plugins.string;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * Returns a number between 0 (no likeness) and 1 (identical), indicating how
 * much the two strings are alike. </br>
 * If the option 'relative' is set to false, then the absolute editdistance will
 * be returned rather than a relative distance.
 * 
 * @author Sander
 *
 */
public class WordDistanceConstruct implements Construct {

	@Override
	public String getName() {

		return "wordDistance";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(WordDistanceConstruct::process, new Argument("source"), new Argument("target"),
				new Argument("relative", ExpressionBuilder.TRUE));
	}

	private static MetaExpression process(final MetaExpression source, final MetaExpression target,
			final MetaExpression relative) {

		if (source.getType() != ExpressionDataType.ATOMIC || target.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (source == ExpressionBuilder.NULL || target == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		int edits = damlev(source.toString(), target.toString());

		if (!relative.getBooleanValue()) {
			return ExpressionBuilder.fromValue(edits);
		}

		int maxlength = Math.max(source.toString().length(), target.toString().length());
		double similarity = (1 - ((double) edits / maxlength));
		return ExpressionBuilder.fromValue(similarity);
	}

	private static int damlev(final String source, final String target) {
		int[] workspace = new int[1024];
		int lenS = source.length();
		int lenT = target.length();

		if (lenS * lenT > workspace.length) {
			workspace = new int[(source.length() + 1) * (target.length() + 1)];
		}

		int lenS1 = lenS + 1;
		int lenT1 = lenT + 1;

		if (lenT1 == 1) {
			return lenS1 - 1;
		}
		if (lenS1 == 1) {
			return lenT1 - 1;
		}

		int[] dl = workspace;
		int dlIndex = 0;
		int sPrevIndex = 0, tPrevIndex = 0, rowBefore = 0, min = 0, cost = 0, tmp = 0;
		int tri = lenS1 + 2;

		// start row with constant
		dlIndex = 0;
		for (tmp = 0; tmp < lenT1; tmp++) {
			dl[dlIndex] = tmp;
			dlIndex += lenS1;
		}
		for (int sIndex = 0; sIndex < lenS; sIndex++) {
			dlIndex = sIndex + 1;
			dl[dlIndex] = dlIndex; // start column with constant
			for (int tIndex = 0; tIndex < lenT; tIndex++) {
				rowBefore = dlIndex;
				dlIndex += lenS1;
				// deletion
				min = dl[rowBefore] + 1;
				// insertion
				tmp = dl[dlIndex - 1] + 1;
				if (tmp < min) {
					min = tmp;
				}
				cost = 1;
				if (source.charAt(sIndex) == target.charAt(tIndex)) {
					cost = 0;
				}
				if (sIndex > 0 && tIndex > 0) {
					if (source.charAt(sIndex) == target.charAt(tPrevIndex)
							&& source.charAt(sPrevIndex) == target.charAt(tIndex)) {
						tmp = dl[rowBefore - tri] + cost;
						// transposition
						if (tmp < min) {
							min = tmp;
						}
					}
				}
				// substitution
				tmp = dl[rowBefore - 1] + cost;
				if (tmp < min) {
					min = tmp;
				}
				dl[dlIndex] = min;
				tPrevIndex = tIndex;
			}
			sPrevIndex = sIndex;
		}
		return dl[dlIndex];
	}
}
