package nl.xillio.xill.plugins.math;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * The construct for some hungarian magic
 * @author Ivor
 *
 */
public class HungarianAlgorithmConstruct implements Construct {

	@Override
	public String getName() {
		return "hungarianalgorithm";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				HungarianAlgorithmConstruct::process,
				new Argument("matrix"),
				new Argument("max"));
		
	}
	
	private static MetaExpression process(final MetaExpression matrixVar, final MetaExpression maxVar)
	{
		if(matrixVar == ExpressionBuilder.NULL)
			return ExpressionBuilder.NULL;
		
		@SuppressWarnings("unchecked")
		List<MetaExpression> matrix = (List<MetaExpression>) matrixVar.getValue();
		
		String method = "max";
		if (maxVar.getBooleanValue() == false)
			method = "min";
		
		//Prepare array
		int rows = matrix.size();
		if(rows < 1)
			//Throw an error through the window
			return ExpressionBuilder.NULL;
		
		MetaExpression var = matrix.get(0);
		
		if(var.getType() != ExpressionDataType.LIST){
			return ExpressionBuilder.NULL;
		}
		
		@SuppressWarnings("unchecked")
		List<MetaExpression> varList = (List<MetaExpression>) var.getValue();
		
		int columns = varList.size();
		
		if(columns < 1){
			//throw an error through the wall
			return ExpressionBuilder.NULL;
		}
		
		if(rows == 0 || columns == 0)
			return ExpressionBuilder.NULL;
		
		double[][] array = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				array[i][j] = getMatrixValue(matrix, i, j);
				if (Double.isNaN(array[i][j])) {
					//Throw an error through the table
					return ExpressionBuilder.NULL;
				}
			}
		}
		
		// Transpose if required
		boolean transposed = false;
		if (array.length > array[0].length) {
			array = transpose(array);
			transposed = true;
		}
		
		// Perform the actual calculation
				int[][] assignment = new int[array.length][2];

				assignment = hgAlgorithm(array, method);

				// Calculate the final score
				double sum = 0;
				for (int[] element : assignment) {
					sum = sum + array[element[0]][element[1]];
				}

				// Transpose results back if required
				if (transposed) {
					for (int i = 0; i < assignment.length; i++) {
						int row = assignment[i][0];
						int col = assignment[i][1];
						assignment[i][0] = col;
						assignment[i][1] = row;
					}
				}

				// Prepare results
				List<MetaExpression> result = new ArrayList<>();
				
				result.add(ExpressionBuilder.fromValue(sum));

				List<MetaExpression> cells = new ArrayList<>();
				for (int i = 0; i < assignment.length; i++) {
					List<MetaExpression> pair = new ArrayList<>();
					pair.add(ExpressionBuilder.fromValue(assignment[i][0]));
					pair.add(ExpressionBuilder.fromValue(assignment[i][1]));
					cells.add(ExpressionBuilder.fromValue(pair));
				}
				result.add(ExpressionBuilder.fromValue(cells));

				return ExpressionBuilder.fromValue(result);
				
		
		
	}
	
	private static double getMatrixValue(final List<MetaExpression> matrix, final int row, final int col) {
		MetaExpression rowVariable = matrix.get(row);
		if (rowVariable.getValue() == ExpressionBuilder.NULL || rowVariable.getType() != ExpressionDataType.LIST) {
			return Double.NaN;
		}

		@SuppressWarnings("unchecked")
		MetaExpression cell = ((List<MetaExpression>) rowVariable.getValue()).get(col);
		if (cell.getValue() == ExpressionBuilder.NULL || cell.getType() != ExpressionDataType.ATOMIC) {
			return Double.NaN;
		}

		return cell.getNumberValue().doubleValue();

	}

	// ///////////////////////////////////////////////////////////////
	// Hungarian Alghoritm implementation by Konstantinos A. Nedas
	// Credits and code below

	/*
	 * Created on Apr 25, 2005
	 * 
	 * Munkres-Kuhn (Hungarian) Algorithm Clean Version: 0.11
	 * 
	 * Konstantinos A. Nedas
	 * Department of Spatial Information Science & Engineering
	 * University of Maine, Orono, ME 04469-5711, USA
	 * kostas@spatial.maine.edu
	 * http://www.spatial.maine.edu/~kostas
	 * 
	 * This Java class implements the Hungarian algorithm
	 * 
	 * It takes 2 arguments:
	 * a. A 2-D array (could be rectangular or square).
	 * b. A string ("min" or "max") specifying whether you want the min or max assignment.
	 * [It returns an assignment matrix[array.length][2] that contains the row and col of
	 * the elements (in the original inputted array) that make up the optimum assignment.]
	 * 
	 * Any comments, corrections, or additions would be much appreciated.
	 * Credit due to professor Bob Pilgrim for providing an online copy of the
	 * pseudocode for this algorithm (http://216.249.163.93/bob.pilgrim/445/munkres.html)
	 * 
	 * Feel free to redistribute this source code, as long as this header--with
	 * the exception of sections in brackets--remains as part of the file.
	 * 
	 * Requirements: JDK 1.5.0_01 or better.
	 */

	/**
	 * Finds the largest element in a positive array.
	 * Works for arrays where all values are >= 0.
	 *
	 * @param array
	 *        a positive array
	 * @return the largest element
	 */
	private static double findLargest(final double[][] array) {
		double largest = 0;
		for (double[] element : array) {
			for (double element2 : element) {
				if (element2 > largest) {
					largest = element2;
				}
			}
		}
		return largest;
	}

	/**
	 * Transposes a double[][] array.
	 *
	 * @param array
	 *        an array
	 * @return the transposed array
	 */
	private static double[][] transpose(final double[][] array) {
		double[][] transposedArray = new double[array[0].length][array.length];
		for (int i = 0; i < transposedArray.length; i++) {
			for (int j = 0; j < transposedArray[i].length; j++) {
				transposedArray[i][j] = array[j][i];
			}
		}
		return transposedArray;
	}

	/**
	 * Copies all elements of an array to a new array.
	 *
	 * @param original
	 *        an array
	 * @return a copy of the array
	 */
	private static double[][] copyOf(final double[][] original) {
		double[][] copy = new double[original.length][original[0].length];
		for (int i = 0; i < original.length; i++) {
			// Need to do it this way, otherwise it copies only memory location
			System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
		}
		return copy;
	}

	// **********************************//
	// METHODS OF THE HUNGARIAN ALGORITHM//
	// **********************************//

	private static int[][] hgAlgorithm(final double[][] array, final String sumType) {
		double[][] cost = copyOf(array); // Create the cost matrix
		if (sumType.equalsIgnoreCase("max")) { // Then array is weight array. Must change to cost.
			double maxWeight = findLargest(cost);
			for (int i = 0; i < cost.length; i++) { // Generate cost by subtracting.
				for (int j = 0; j < cost[i].length; j++) {
					cost[i][j] = (maxWeight - cost[i][j]);
				}
			}
		}
		double maxCost = findLargest(cost); // Find largest cost matrix element (needed for step 6).

		int[][] mask = new int[cost.length][cost[0].length]; // The mask array.
		int[] rowCover = new int[cost.length]; // The row covering vector.
		int[] colCover = new int[cost[0].length]; // The column covering vector.
		int[] zero_RC = new int[2]; // Position of last zero from Step 4.
		int step = 1;
		boolean done = false;
		while (!done) {
			switch (step) {
				case 1:
					step = hg_step1(step, cost);
					break;
				case 2:
					step = hg_step2(step, cost, mask, rowCover, colCover);
					break;
				case 3:
					step = hg_step3(step, mask, colCover);
					break;
				case 4:
					step = hg_step4(step, cost, mask, rowCover, colCover, zero_RC);
					break;
				case 5:
					step = hg_step5(step, mask, rowCover, colCover, zero_RC);
					break;
				case 6:
					step = hg_step6(step, cost, rowCover, colCover, maxCost);
					break;
				case 7:
					done = true;
					break;
				default:
					break;
			}
		}

		int[][] assignment = new int[array.length][2]; // Create the returned array.
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] == 1) {
					assignment[i][0] = i;
					assignment[i][1] = j;
				}
			}
		}

		// If you want to return the min or max sum, in your own main method
		// instead of the assignment array, then use the following code:
		/*
		 * double sum = 0;
		 * for (int i=0; i<assignment.length; i++)
		 * {
		 * sum = sum + array[assignment[i][0]][assignment[i][1]];
		 * }
		 * return sum;
		 */
		// Of course you must also change the header of the method to:
		// public static double hgAlgorithm (double[][] array, String sumType)

		return assignment;
	}

	private static int hg_step1(int step, final double[][] cost) {
		// What STEP 1 does:
		// For each row of the cost matrix, find the smallest element
		// and subtract it from from every other element in its row.
		double minval;
		for (int i = 0; i < cost.length; i++) {
			minval = cost[i][0];
			for (int j = 0; j < cost[i].length; j++) { // 1st inner loop finds min val in row.
				if (minval > cost[i][j]) {
					minval = cost[i][j];
				}
			}
			for (int j = 0; j < cost[i].length; j++) { // 2nd inner loop subtracts it.
				cost[i][j] = cost[i][j] - minval;
			}
		}

		step = 2;
		return step;
	}

	private static int hg_step2(int step, final double[][] cost, final int[][] mask, final int[] rowCover, final int[] colCover) {
		// What STEP 2 does:
		// Marks uncovered zeros as starred and covers their row and column.

		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost[i].length; j++) {
				if ((cost[i][j] == 0) && (colCover[j] == 0) && (rowCover[i] == 0)) {
					mask[i][j] = 1;
					colCover[j] = 1;
					rowCover[i] = 1;
				}
			}
		}

		clearCovers(rowCover, colCover); // Reset cover vectors.

		step = 3;
		return step;
	}

	private static int hg_step3(int step, final int[][] mask, final int[] colCover) {
		// What STEP 3 does:
		// Cover columns of starred zeros. Check if all columns are covered.

		for (int[] element : mask) {
			for (int j = 0; j < element.length; j++) {
				if (element[j] == 1) {
					colCover[j] = 1;
				}
			}
		}

		int count = 0;
		for (int element : colCover) {
			count = count + element;
		}

		if (count >= mask.length) { // Should be cost.length but ok, because mask has same dimensions.
			step = 7;
		} else {
			step = 4;
		}
		return step;
	}

	private static int hg_step4(int step, final double[][] cost, final int[][] mask, final int[] rowCover, final int[] colCover, final int[] zero_RC) {
		// What STEP 4 does:
		// Find an uncovered zero in cost and prime it (if none go to step 6). Check for star in same row:
		// if yes, cover the row and uncover the star's column. Repeat until no uncovered zeros are left
		// and go to step 6. If not, save location of primed zero and go to step 5.

		int[] row_col = new int[2]; // Holds row and col of uncovered zero.
		boolean done = false;
		while (!done) {
			row_col = findUncoveredZero(row_col, cost, rowCover, colCover);
			if (row_col[0] == -1) {
				done = true;
				step = 6;
			} else {
				mask[row_col[0]][row_col[1]] = 2; // Prime the found uncovered zero.

				boolean starInRow = false;
				for (int j = 0; j < mask[row_col[0]].length; j++) {
					if (mask[row_col[0]][j] == 1) { // If there is a star in the same row...
						starInRow = true;
						row_col[1] = j; // remember its column.
					}
				}

				if (starInRow) {
					rowCover[row_col[0]] = 1; // Cover the star's row.
					colCover[row_col[1]] = 0; // Uncover its column.
				} else {
					zero_RC[0] = row_col[0]; // Save row of primed zero.
					zero_RC[1] = row_col[1]; // Save column of primed zero.
					done = true;
					step = 5;
				}
			}
		}
		return step;
	}

	// Aux 1 for hg_step4.
	private static int[] findUncoveredZero(final int[] row_col, final double[][] cost, final int[] rowCover, final int[] colCover) {
		row_col[0] = -1; // Just a check value. Not a real index.
		row_col[1] = 0;

		int i = 0;
		boolean done = false;
		while (!done) {
			int j = 0;
			while (j < cost[i].length) {
				if (cost[i][j] == 0 && rowCover[i] == 0 && colCover[j] == 0) {
					row_col[0] = i;
					row_col[1] = j;
					done = true;
				}
				j = j + 1;
			}
			i = i + 1;
			if (i >= cost.length) {
				done = true;
			}
		}
		return row_col;
	}

	private static int hg_step5(int step, final int[][] mask, final int[] rowCover, final int[] colCover, final int[] zero_RC) {
		// What STEP 5 does:
		// Construct series of alternating primes and stars. Start with prime from step 4.
		// Take star in the same column. Next take prime in the same row as the star. Finish
		// at a prime with no star in its column. Unstar all stars and star the primes of the
		// series. Erasy any other primes. Reset covers. Go to step 3.

		int count = 0; // Counts rows of the path matrix.
		int[][] path = new int[(mask[0].length * mask.length)][2]; // Path matrix (stores row and col).
		path[count][0] = zero_RC[0]; // Row of last prime.
		path[count][1] = zero_RC[1]; // Column of last prime.

		boolean done = false;
		while (!done) {
			int r = findStarInCol(mask, path[count][1]);
			if (r >= 0) {
				count = count + 1;
				path[count][0] = r; // Row of starred zero.
				path[count][1] = path[count - 1][1]; // Column of starred zero.
			} else {
				done = true;
			}

			if (!done) {
				int c = findPrimeInRow(mask, path[count][0]);
				count = count + 1;
				path[count][0] = path[count - 1][0]; // Row of primed zero.
				path[count][1] = c; // Col of primed zero.
			}
		}

		convertPath(mask, path, count);
		clearCovers(rowCover, colCover);
		erasePrimes(mask);

		step = 3;
		return step;
	}

	// Aux 1 for hg_step5.
	private static int findStarInCol(final int[][] mask, final int col) {
		int r = -1; // Again this is a check value.
		for (int i = 0; i < mask.length; i++) {
			if (mask[i][col] == 1) {
				r = i;
			}
		}
		return r;
	}

	// Aux 2 for hg_step5.
	private static int findPrimeInRow(final int[][] mask, final int row) {
		int c = -1;
		for (int j = 0; j < mask[row].length; j++) {
			if (mask[row][j] == 2) {
				c = j;
			}
		}
		return c;
	}

	// Aux 3 for hg_step5.
	private static void convertPath(final int[][] mask, final int[][] path, final int count) {
		for (int i = 0; i <= count; i++) {
			if (mask[(path[i][0])][(path[i][1])] == 1) {
				mask[(path[i][0])][(path[i][1])] = 0;
			} else {
				mask[(path[i][0])][(path[i][1])] = 1;
			}
		}
	}

	// Aux 4 for hg_step5.
	private static void erasePrimes(final int[][] mask) {
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] == 2) {
					mask[i][j] = 0;
				}
			}
		}
	}

	// Aux 5 for hg_step5 (and not only).
	private static void clearCovers(final int[] rowCover, final int[] colCover) {
		for (int i = 0; i < rowCover.length; i++) {
			rowCover[i] = 0;
		}
		for (int j = 0; j < colCover.length; j++) {
			colCover[j] = 0;
		}
	}

	private static int hg_step6(int step, final double[][] cost, final int[] rowCover, final int[] colCover, final double maxCost) {
		// What STEP 6 does:
		// Find smallest uncovered value in cost: a. Add it to every element of covered rows
		// b. Subtract it from every element of uncovered columns. Go to step 4.

		double minval = findSmallest(cost, rowCover, colCover, maxCost);
		for (int i = 0; i < rowCover.length; i++) {
			for (int j = 0; j < colCover.length; j++) {
				if (rowCover[i] == 1) {
					cost[i][j] = cost[i][j] + minval;
				}
				if (colCover[j] == 0) {
					cost[i][j] = cost[i][j] - minval;
				}
			}
		}

		step = 4;
		return step;
	}

	// Aux 1 for hg_step6.
	private static double findSmallest(final double[][] cost, final int[] rowCover, final int[] colCover, final double maxCost) {
		double minval = maxCost; // There cannot be a larger cost than this.
		for (int i = 0; i < cost.length; i++) { // Now find the smallest uncovered value.
			for (int j = 0; j < cost[i].length; j++) {
				if (rowCover[i] == 0 && colCover[j] == 0 && (minval > cost[i][j])) {
					minval = cost[i][j];
				}
			}
		}
		return minval;
	}

}