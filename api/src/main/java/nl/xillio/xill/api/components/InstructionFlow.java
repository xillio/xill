package nl.xillio.xill.api.components;

import java.util.NoSuchElementException;

/**
 * This object identifies a break in the instruction flow
 *
 * @param <T>
 *        the type contained in this {@link InstructionFlow}
 */
public class InstructionFlow<T> {
	private enum Type {
		CONTINUE, BREAK, RETURN, RESUME
	}

	private final Type flowType;
	private final T value;

	private InstructionFlow(final Type flowType, final T value) {
		this.flowType = flowType;
		this.value = value;
	}

	/**
	 * @return true if this is a return flow
	 */
	public boolean returns() {
		return flowType == Type.RETURN;
	}

	/**
	 * @return true if the flow should be interrupted
	 */
	public boolean breaks() {
		return returns() || flowType == Type.BREAK;
	}

	/**
	 * @return true if the current instruction should be skipped
	 */
	public boolean skips() {
		return flowType == Type.CONTINUE;
	}

	/**
	 * @return true if the flow was not interrupted
	 */
	public boolean resumes() {
		return flowType == Type.RESUME;
	}

	/**
	 * @return true if a non-null value is set
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * Get the stored value
	 *
	 * @return the value
	 * @throws NoSuchElementException
	 *         When no value was set
	 */
	public T get() {
		if (value == null) {
			throw new NoSuchElementException("No value was set.");
		}
		return value;
	}

	/**
	 * Create a new {@link InstructionFlow} that represents a return value
	 * 
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 *
	 * @param value
	 *        the value to set
	 * @return the instructionflow
	 */
	public static <T> InstructionFlow<T> doReturn(final T value) {
		return new InstructionFlow<T>(Type.RETURN, value);
	}

	/**
	 * Create a new {@link InstructionFlow} that represents an empty return
	 * 
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 *
	 * @return the instructionflow
	 */
	public static <T> InstructionFlow<T> doReturn() {
		return new InstructionFlow<T>(Type.RETURN, null);
	}

	/**
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 * @return a continue token
	 */
	public static <T> InstructionFlow<T> doContinue() {
		return new InstructionFlow<T>(Type.CONTINUE, null);
	}

	/**
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 * @return a break token
	 */
	public static <T> InstructionFlow<T> doBreak() {
		return new InstructionFlow<T>(Type.BREAK, null);
	}

	/**
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 * @return a resume token
	 */
	public static <T> InstructionFlow<T> doResume() {
		return new InstructionFlow<T>(Type.RESUME, null);
	}

	/**
	 * @param <T>
	 *        the type parameter for {@link InstructionFlow}
	 * @param value
	 *        the value to set
	 * @return a resume token with a value
	 */
	public static <T> InstructionFlow<T> doResume(final T value) {
		return new InstructionFlow<T>(Type.RESUME, value);
	}
}
