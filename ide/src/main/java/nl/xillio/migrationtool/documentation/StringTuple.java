package nl.xillio.migrationtool.documentation;

/**
 * A tuple class because Java doesn't have any.
 * @author Ivor
 *
 */
public class StringTuple {
	/**
	 * The first String.
	 */
	public String first;
	/**
	 * The second String.
	 */
	public String second;
	
	/**
	 * The constructor of the StringTuple
	 * @param First
	 *        The first string.
	 * @param Second
	 * 		  The second string.
	 */
	public StringTuple(String First, String Second)
	{
		first = First;
		second = Second;
	}

}
