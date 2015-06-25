package nl.xillio.xill;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.LanguageFactory;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.components.ListExpression;
import nl.xillio.xill.api.components.Literal;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.components.expressions.CallbotExpression;
import nl.xillio.xill.components.expressions.ConstructCall;
import nl.xillio.xill.components.expressions.FunctionCall;
import nl.xillio.xill.components.expressions.VariableAccessExpression;
import nl.xillio.xill.components.instructions.BreakInstruction;
import nl.xillio.xill.components.instructions.ContinueInstruction;
import nl.xillio.xill.components.instructions.ExpressionInstruction;
import nl.xillio.xill.components.instructions.ForeachInstruction;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.IfInstruction;
import nl.xillio.xill.components.instructions.Instruction;
import nl.xillio.xill.components.instructions.InstructionSet;
import nl.xillio.xill.components.instructions.ReturnInstruction;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import nl.xillio.xill.components.instructions.WhileInstruction;
import nl.xillio.xill.components.operators.Add;
import nl.xillio.xill.components.operators.And;
import nl.xillio.xill.components.operators.Assign;
import nl.xillio.xill.components.operators.Concat;
import nl.xillio.xill.components.operators.Divide;
import nl.xillio.xill.components.operators.Equals;
import nl.xillio.xill.components.operators.FromList;
import nl.xillio.xill.components.operators.GreaterThan;
import nl.xillio.xill.components.operators.GreaterThanOrEquals;
import nl.xillio.xill.components.operators.Modulo;
import nl.xillio.xill.components.operators.Multiply;
import nl.xillio.xill.components.operators.Negate;
import nl.xillio.xill.components.operators.NotEquals;
import nl.xillio.xill.components.operators.Or;
import nl.xillio.xill.components.operators.Power;
import nl.xillio.xill.components.operators.SmallerThan;
import nl.xillio.xill.components.operators.SmallerThanOrEquals;
import nl.xillio.xill.components.operators.Subtract;
import nl.xillio.xill.debugging.DebugInfo;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import xill.lang.xill.BooleanLiteral;
import xill.lang.xill.Expression;
import xill.lang.xill.IntegerLiteral;
import xill.lang.xill.ListExtraction;
import xill.lang.xill.NullLiteral;
import xill.lang.xill.Target;
import xill.lang.xill.UseStatement;
import xill.lang.xill.Variable;

/**
 * This class is responsible for processing the Robot token into a functional
 * program tree
 *
 */
public class XillProgramFactory implements LanguageFactory<xill.lang.xill.Robot> {
	private final DynamicInvoker<EObject> expressionParseInvoker = new DynamicInvoker<EObject>("parseToken", this);
	private final Map<xill.lang.xill.Target, VariableDeclaration> variables = new HashMap<>();
	private final Map<xill.lang.xill.FunctionDeclaration, FunctionDeclaration> functions = new HashMap<>();
	/**
	 * Because functions don't have to be declared before the calls the declaration might
	 * not exist while parsing the call. To fix this we will not set the declaration on the call
	 * until we are finished parsing.
	 */
	private final Map<xill.lang.xill.FunctionCall, FunctionCall> functionCalls = new HashMap<>();
	private final Map<xill.lang.xill.FunctionCall, List<Processable>> functionCallArguments = new HashMap<>();
	private final Map<xill.lang.xill.UseStatement, PluginPackage> useStatements = new HashMap<>();
	private final Map<Resource, RobotID> robotID = new HashMap<>();
	private final PluginLoader<PluginPackage> pluginLoader;
	private final Debugger debugger;
	private final RobotID rootRobot;

	/**
	 * Create a new {@link XillProgramFactory}
	 *
	 * @param loader
	 * @param debugger
	 * @param robotID
	 */
	public XillProgramFactory(final PluginLoader<PluginPackage> loader, final Debugger debugger, final RobotID robotID) {
		this(loader, debugger, robotID, false);
	}

	/**
	 * Create a new {@link XillProgramFactory}
	 *
	 * @param loader
	 * @param debugger
	 * @param robotID
	 *
	 * @param verbose
	 *        verbose logging for the compiler
	 */
	public XillProgramFactory(final PluginLoader<PluginPackage> loader, final Debugger debugger, final RobotID robotID, final boolean verbose) {
		this.debugger = debugger;
		rootRobot = robotID;
		expressionParseInvoker.VERBOSE = verbose;
		pluginLoader = loader;
	}

	@Override
	public Robot parse(final xill.lang.xill.Robot robot, final RobotID robotID, final List<Robot> libraries)
			throws XillParsingException {

		this.robotID.put(robot.eResource(), robotID);
		DebugInfo info = new DebugInfo();

		info.setVariables(variables);
		info.setUsing(useStatements);

		for (UseStatement using : robot.getUses()) {
			String pluginName = using.getPlugin();

			// In case of non-qualified name: use MySQL;
			if (pluginName == null) {
				pluginName = using.getName();
			}

			// Really? Java...
			String searchName = pluginName;

			Optional<PluginPackage> plugin = pluginLoader.getPluginManager().getPlugins().stream().filter(pckage -> pckage.getName().equals(searchName)).findAny();

			if (!plugin.isPresent()) {
				CodePosition pos = pos(robot);
				throw new XillParsingException("Could not find plugin " + pluginName, pos.getLineNumber(), pos.getRobotID());
			}

			useStatements.put(using, plugin.get());
		}


		nl.xillio.xill.components.Robot instructionRobot = new nl.xillio.xill.components.Robot(robotID, libraries, debugger);

		for (xill.lang.xill.Instruction instruction : robot.getInstructionSet().getInstructions()) {
			instructionRobot.add(parse(instruction));
		}

		// Push all FunctionDeclarations after parsing
		for (Map.Entry<xill.lang.xill.FunctionCall, FunctionCall> entry : functionCalls.entrySet()) {
			parseToken(entry.getKey(), entry.getValue());
		}

		debugger.addDebugInfo(info);

		return instructionRobot;
	}

	/**
	 * This method will use a {@link DynamicInvoker} to route the current parse
	 * assignment to the correct method. To do this it will search through all
	 * declared methods called parse that have 1 argument and try to select the
	 * best argument type.
	 *
	 * @param token
	 *        The component that should be parsed.
	 * @return The resulting expression from the selected parse method
	 * @throws XillParsingException
	 *         When something went wrong while parsing this component.
	 */
	private Processable parse(final Expression token) throws XillParsingException {
		if (token == null) {
			throw new NullPointerException("Cannot parse null token.");
		}

		try {
			return expressionParseInvoker.invoke(token, Processable.class);
		} catch (InvocationTargetException | IllegalArgumentException e) {
			Throwable root = ExceptionUtils.getRootCause(e);

			if (root instanceof XillParsingException) {
				throw (XillParsingException) root;
			}

			CodePosition pos = pos(token);
			throw new XillParsingException(
				"Something went wrong while parsing expression of type "
						+ token.getClass().getSimpleName() + ": " + ExceptionUtils.getRootCauseMessage(e), pos.getLineNumber(), pos.getRobotID(), e);
		}
	}

	/**
	 * @see XillProgramFactory#parse(Expression)
	 */
	private Instruction parse(final xill.lang.xill.Instruction token)
			throws XillParsingException {
		if (token == null) {
			throw new NullPointerException("Cannot parse null token.");
		}

		try {
			Instruction result = expressionParseInvoker.invoke(token, Instruction.class);
			result.setPosition(pos(token));
			return result;
		} catch (InvocationTargetException | IllegalArgumentException e) {
			Throwable root = ExceptionUtils.getRootCause(e);

			if (root instanceof XillParsingException) {
				throw (XillParsingException) root;
			}

			CodePosition pos = pos(token);
			throw new XillParsingException(
				"Something went wrong while parsing instruction of type "
						+ token.getClass().getSimpleName() + ": " + ExceptionUtils.getRootCauseMessage(e), pos.getLineNumber(), pos.getRobotID(), e);
		}
	}

	/**
	 * Parse the instruction set
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 *         When parsing an instruction wasn't successful
	 */
	InstructionSet parseToken(final xill.lang.xill.InstructionSet token)
			throws XillParsingException {
		InstructionSet instructionSet = new InstructionSet(debugger);

		for (xill.lang.xill.Instruction instruction : token.getInstructions()) {
			instructionSet.add(parse(instruction));
		}

		return instructionSet;
	}

	/**
	 * Parse an If Instruction
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	IfInstruction parseToken(final xill.lang.xill.IfInstruction token)
			throws XillParsingException {
		if (token.getElseBlock() == null) {
			return new IfInstruction(parse(token.getCondition()),
				parseToken(token.getIfBlock().getInstructionSet()));
		}

		return new IfInstruction(parse(token.getCondition()), parseToken(token
			.getIfBlock().getInstructionSet()), parseToken(token
				.getElseBlock().getInstructionSet()));
	}

	/**
	 * Parse a While Instruction
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	WhileInstruction parseToken(final xill.lang.xill.WhileInstruction token) throws XillParsingException {
		return new WhileInstruction(parse(token.getCondition()), parseToken(token.getInstructionBlock().getInstructionSet()));
	}

	/**
	 * Parse a Foreach Instruction
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	ForeachInstruction parseToken(final xill.lang.xill.ForEachInstruction token) throws XillParsingException {
		VariableDeclaration valueDec = VariableDeclaration.nullDeclaration(pos(token));
		variables.put(token.getValueVar(), valueDec);

		if (token.getKeyVar() != null) {
			VariableDeclaration keyDec = VariableDeclaration.nullDeclaration(pos(token));
			variables.put(token.getKeyVar(), keyDec);

			return new ForeachInstruction(parseToken(token.getInstructionBlock().getInstructionSet()), parse(token.getItterator()), valueDec, keyDec);
		}

		return new ForeachInstruction(parseToken(token.getInstructionBlock().getInstructionSet()), parse(token.getItterator()), valueDec);
	}

	/**
	 * Parse a Break Instruction
	 *
	 * @param token
	 * @return
	 */
	BreakInstruction parseToken(final xill.lang.xill.BreakInstruction token) {
		return new BreakInstruction();
	}

	/**
	 * Parse a InstructionFlow instruction
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	ReturnInstruction parseToken(final xill.lang.xill.ReturnInstruction token)
			throws XillParsingException {
		if (token.getValue() == null) {
			return new ReturnInstruction();
		}
		return new ReturnInstruction(parse(token.getValue()));
	}

	/**
	 * Parse a Continue instruction
	 *
	 * @param token
	 * @return
	 */
	ContinueInstruction parseToken(final xill.lang.xill.ContinueInstruction token) {
		return new ContinueInstruction();
	}

	/**
	 * Parse a Function Declaration
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	FunctionDeclaration parseToken(final xill.lang.xill.FunctionDeclaration token) throws XillParsingException {

		// Push the arguments
		List<VariableDeclaration> parameters = new ArrayList<>(token.getParameters().size()
				);
		for (Target parameter : token.getParameters()) {
			// TODO Default values
			VariableDeclaration declaration = VariableDeclaration.nullDeclaration(pos(token));

			parameters.add(declaration);
			variables.put(parameter, declaration);
		}

		InstructionSet instructions = parseToken(token.getInstructionBlock().getInstructionSet());
		FunctionDeclaration declaration = new FunctionDeclaration(instructions, parameters);

		functions.put(token, declaration);

		return declaration;
	}

	/**
	 * Parse a MetaExpression Declaration
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	VariableDeclaration parseToken(final xill.lang.xill.VariableDeclaration token)
			throws XillParsingException {
		Processable expression = token.getValue() == null ? Literal.NULL : parse(token.getValue());

		VariableDeclaration declaration = new VariableDeclaration(expression);

		variables.put(token.getName(), declaration);

		return declaration;
	}

	/**
	 * Parse an Expression at root level
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	ExpressionInstruction parseToken(final xill.lang.xill.ExpressionInstruction token) throws XillParsingException {
		ExpressionInstruction instruction = new ExpressionInstruction(parse(token.getExpression()));

		return instruction;
	}

	/**
	 * Parse a general Expression
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.Expression token)
			throws XillParsingException {

		Processable value = parse(token.getExpression());

		// Parse prefixes
		if (token.getPrefix() != null) {
			switch (token.getPrefix()) {
				case "-":
					value = new Subtract(Literal.fromValue(0), value);
					break;
				case "!":
					value = new Negate(value);
					break;
				case "++":
					Target pTarget = getTarget(token.getExpression());
					List<Processable> pPath = getPath(token.getExpression());
					VariableDeclaration pDeclaration = variables.get(pTarget);
					value = new Assign(pDeclaration, pPath, new Add(value, Literal.fromValue(1)));
					break;
				case "--":
					Target mTarget = getTarget(token.getExpression());
					List<Processable> mPath = getPath(token.getExpression());
					VariableDeclaration mDeclaration = variables.get(mTarget);
					value = new Assign(mDeclaration, mPath, new Subtract(value, Literal.fromValue(1)));
					break;
				default:
					throw new NotImplementedException("This prefix has not been implemented.");
			}
		}

		// Parse suffixes
		if (token.getSuffix() != null) {
			switch (token.getSuffix()) {
				case "++":
					Target pTarget = getTarget(token.getExpression());
					List<Processable> pPath = getPath(token.getExpression());
					VariableDeclaration pDeclaration = variables.get(pTarget);
					value = new Subtract(new Assign(pDeclaration, pPath, new Add(value, Literal.fromValue(1))), Literal.fromValue(1));
					break;
				case "--":
					Target mTarget = getTarget(token.getExpression());
					List<Processable> mPath = getPath(token.getExpression());
					VariableDeclaration mDeclaration = variables.get(mTarget);
					value = new Add(new Assign(mDeclaration, mPath, new Subtract(value, Literal.fromValue(1))), Literal.fromValue(1));
					break;
				default:
					throw new NotImplementedException("This suffix has not been implemented.");
			}
		}

		return value;
	}

	/**
	 * Parse an Or operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Or parseToken(final xill.lang.xill.impl.OrImpl token) throws XillParsingException {
		Or orExpression = new Or(parse(token.getLeft()),
			parse(token.getRight()));

		return orExpression;
	}

	/**
	 * Parse an And operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	And parseToken(final xill.lang.xill.impl.AndImpl token)
			throws XillParsingException {
		And andExpression = new And(parse(token.getLeft()),
			parse(token.getRight()));

		return andExpression;
	}

	/**
	 * Parse an Equals operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.EqualityImpl token) throws XillParsingException {
		switch (token.getOp()) {
			case "==":
				return new Equals(parse(token.getLeft()), parse(token.getRight()));
			case "!=":
				return new NotEquals(parse(token.getLeft()), parse(token.getRight()));
			default:
				CodePosition pos = pos(token);
				throw new XillParsingException("This token has not been implemented.", pos.getLineNumber(), pos.getRobotID());
		}
	}

	/**
	 * Parse an Add-priority operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.AdditionImpl token)
			throws XillParsingException {
		Processable expression;

		switch (token.getOp()) {
			case "+":
				expression = new Add(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "-":
				expression = new Subtract(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "::":
				expression = new Concat(parse(token.getLeft()), parse(token.getRight()));
				break;
			default:
				CodePosition pos = pos(token);
				throw new XillParsingException(
					"This operator has not been implemented.", pos.getLineNumber(), pos.getRobotID());
		}

		return expression;
	}

	/**
	 * Parse an Compare-priority operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.ComparisonImpl token)
			throws XillParsingException {
		Processable expression;

		switch (token.getOp()) {
			case ">":
				expression = new GreaterThan(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "<":
				expression = new SmallerThan(parse(token.getLeft()), parse(token.getRight()));
				break;
			case ">=":
				expression = new GreaterThanOrEquals(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "<=":
				expression = new SmallerThanOrEquals(parse(token.getLeft()), parse(token.getRight()));
				break;
			default:
				CodePosition pos = pos(token);
				throw new XillParsingException(
					"This operator has not been implemented.", pos.getLineNumber(), pos.getRobotID());
		}

		return expression;
	}

	/**
	 * Parse a Multiply-priority operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.MultiplicationImpl token)
			throws XillParsingException {
		Processable expression;

		switch (token.getOp()) {
			case "*":
				expression = new Multiply(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "/":
				expression = new Divide(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "%":
				expression = new Modulo(parse(token.getLeft()), parse(token.getRight()));
				break;
			case "^":
				expression = new Power(parse(token.getLeft()), parse(token.getRight()));
				break;
			default:
				CodePosition pos = pos(token);
				throw new XillParsingException(
					"This operator has not been implemented.", pos.getLineNumber(), pos.getRobotID());
		}

		return expression;
	}

	/**
	 * Parse a Assignment-priority operation
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Assign parseToken(final xill.lang.xill.impl.AssignmentImpl token)
			throws XillParsingException {
		Assign expression;
		Target target = getTarget(token.getLeft());
		VariableDeclaration declaration = variables.get(target);
		List<Processable> path = getPath(token.getLeft().getExpression());

		switch (token.getOp()) {
			case "=":
				expression = new Assign(declaration, path, parse(token.getRight()));
				break;
			case "+=":
				expression = new Assign(declaration, path, new Add(new VariableAccessExpression(declaration), parse(token.getRight())));
				break;
			case "-=":
				expression = new Assign(declaration, path, new Subtract(new VariableAccessExpression(declaration), parse(token.getRight())));
				break;
			case "*=":
				expression = new Assign(declaration, path, new Multiply(new VariableAccessExpression(declaration), parse(token.getRight())));
				break;
			case "::=":
				expression = new Assign(declaration, path, new Concat(new VariableAccessExpression(declaration), parse(token.getRight())));
				break;
			case "/=":
				expression = new Assign(declaration, path, new Divide(new VariableAccessExpression(declaration), parse(token.getRight())));
				break;
			default:
				CodePosition pos = pos(token);
				throw new XillParsingException(
					"This operator has not been implemented.", pos.getLineNumber(), pos.getRobotID());
		}

		return expression;
	}

	/**
	 * Get the target of an assignment
	 *
	 * @param token
	 * @return
	 */
	private static Target getTarget(final EObject start) {
		EObject currentObject = start;

		while (currentObject != null && !(currentObject instanceof Target)) {
			if (currentObject instanceof Variable) {
				currentObject = ((Variable) currentObject).getTarget();
			} else if (currentObject instanceof ListExtraction) {
				currentObject = ((ListExtraction) currentObject).getValue();
			} else if (currentObject instanceof Expression) {
				currentObject = ((Expression) currentObject).getExpression();
			} else {
				currentObject = null;
			}
		}

		return (Target) currentObject;
	}

	/**
	 * Construct a list that represents the path into a variable
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	private List<Processable> getPath(final EObject start) throws XillParsingException {
		List<Processable> result = new ArrayList<>();
		if (!(start instanceof ListExtraction)) {
			return result;
		}

		ListExtraction extraction = (ListExtraction) start;

		while (extraction != null) {
			if (extraction.getIndex() != null) {
				result.add(parse(extraction.getIndex()));
			} else if (extraction.getChild() != null) {
				result.add(Literal.fromValue(extraction.getChild()));
			} else {
				result.add(new Add(parse(extraction.getValue()), Literal.fromValue(0)));
			}

			if (extraction.getValue() instanceof ListExtraction) {
				extraction = (ListExtraction) extraction.getValue();
			} else {
				extraction = null;
			}
		}

		return result;
	}

	/**
	 * Parse a list extraction
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.ListExtractionImpl token) throws XillParsingException {
		Processable expression = parse(token.getValue());
		if (token.getIndex() != null) {
			// We used brackets
			Processable index = parse(token.getIndex());

			return new FromList(expression, index);
		}

		if (token.getChild() != null) {
			// We used dot-notation
			return new FromList(expression, Literal.fromValue(token.getChild()));
		}

		// We used neither: listVariable[]. Interpret as listVariable[listVariable + 0]
		return new FromList(expression, new Add(expression, Literal.fromValue(0)));
	}

	/**
	 * Parse a written list
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.ListExpressionImpl token) throws XillParsingException {
		List<Processable> expressions = new ArrayList<>(token.getValues().size());

		for (Expression exp : token.getValues()) {
			expressions.add(parse(exp));
		}

		return new ListExpression(expressions);
	}

	/**
	 * Parse a written object
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.impl.ObjectExpressionImpl token) throws XillParsingException {
		Iterator<Expression> keys = token.getNames().iterator();
		Iterator<Expression> values = token.getValues().iterator();
		Map<Processable, Processable> object = new HashMap<>(token.getNames().size());

		while (keys.hasNext() && values.hasNext()) {
			object.put(parse(keys.next()), parse(values.next()));
		}

		return new ObjectExpression(object);

	}

	/**
	 * Parse a variable
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.Variable token)
			throws XillParsingException {
		VariableDeclaration declaration = variables.get(token.getTarget());

		if (declaration == null) {
			CodePosition pos = pos(token);
			throw new XillParsingException("No such variable found: "
					+ token.getTarget().getName(), pos.getLineNumber(), pos.getRobotID());
		}

		return new VariableAccessExpression(declaration);
	}

	/**
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.FunctionCall token)
			throws XillParsingException {

		FunctionCall callExpression = new FunctionCall();

		functionCalls.put(token, callExpression);

		// Parse the arguments
		List<Processable> arguments = new ArrayList<>(token.getArgumentBlock().getParameters().size());

		for (Expression expression : token.getArgumentBlock().getParameters()) {
			arguments.add(parse(expression));
		}

		functionCallArguments.put(token, arguments);

		return callExpression;
	}

	/**
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.ConstructCall token)
			throws XillParsingException {

		PluginPackage pluginPackage = useStatements.get(token.getPackage());

		CodePosition pos = pos(token);

		if (pluginPackage == null) {
			String pluginName = token.getPackage().getPlugin();
			if (pluginName == null) {
				pluginName = token.getPackage().getName();
			}

			throw new XillParsingException("Could not resolve package `" + pluginName + "`", pos.getLineNumber(), pos.getRobotID());

		}
		Construct construct = pluginPackage.getConstruct(token.getFunction());

		if (construct == null) {
			throw new XillParsingException("The construct " + token.getFunction() + " does not exist in package " + token.getPackage().getName(), pos.getLineNumber(), pos.getRobotID());
		}

		// Parse the arguments
		List<Processable> arguments = new ArrayList<>(token.getArgumentBlock().getParameters().size());

		for (Expression argument : token.getArgumentBlock().getParameters()) {
			arguments.add(parse(argument));
		}

		// Check argument count by mocking the input
		ConstructProcessor testProcessor = construct.prepareProcess(new ConstructContext(robotID.get(token.eResource()), rootRobot, construct));
		for (int i = 0; i < arguments.size(); i++) {
			testProcessor.setArgument(i, Literal.NULL);
		}

		// Throw exception if count is incorrect (i.e. We're either missing an argument or provided too many)
		if (testProcessor.getMissingArgument().isPresent() || testProcessor.getNumberOfArguments() < arguments.size()) {
			throw new XillParsingException("Argument count mismatch in " + testProcessor.toString(construct.getName()), pos.getLineNumber(), pos.getRobotID());
		}

		return new ConstructCall(construct, arguments, new ConstructContext(robotID.get(token.eResource()), rootRobot, construct));
	}

	/**
	 * To fix the call -> declaration order problem this method will be called for all function calls after parsing the whole robot
	 *
	 * @param token
	 * @param declaration
	 * @throws XillParsingException
	 */
	private void parseToken(final xill.lang.xill.FunctionCall token,
			final FunctionCall declaration) throws XillParsingException {

		// Parse the assignments
		List<Assign> assignments = new ArrayList<>();
		Iterator<Processable> argumentValue = functionCallArguments.get(token).iterator();
		Iterator<Target> argumentTargets = token.getName().getParameters().iterator();

		while (argumentTargets.hasNext() && argumentValue.hasNext()) {
			VariableDeclaration target = variables.get(argumentTargets.next());
			assignments.add(new Assign(target, new ArrayList<>(), argumentValue.next()));
		}

		// Push the arguments
		declaration.setArguments(assignments);

		FunctionDeclaration functionDeclaration = functions.get(token.getName());

		if (functionDeclaration == null) {
			CodePosition pos = pos(token);
			throw new XillParsingException("Could not find function " + token.getName().getName() + " did you forget to include " + token.getName().eResource().getURI().toFileString() + "?",
				pos.getLineNumber(), pos.getRobotID());
		}

		// Push the function
		declaration.setFunction(functions.get(token.getName()));
	}

	/**
	 * Parse a {@link CallbotExpression}
	 *
	 * @param token
	 * @return
	 * @throws XillParsingException
	 */
	Processable parseToken(final xill.lang.xill.CallbotExpression token) throws XillParsingException {
		Processable path = parse(token.getPath());

		return new CallbotExpression(path, robotID.get(token.eResource()), pluginLoader);
	}

	/**
	 * Parse a {@link BooleanLiteral}
	 *
	 * @param token
	 * @return
	 */
	Processable parseToken(final xill.lang.xill.BooleanLiteral token) {
		return Literal.fromValue(Boolean.parseBoolean(token.getValue()));
	}

	/**
	 * Parse a {@link NullLiteral}
	 *
	 * @param token
	 * @return
	 */
	Processable parseToken(final xill.lang.xill.NullLiteral token) {
		return Literal.NULL;
	}

	/**
	 * Parse an {@link IntegerLiteral}
	 *
	 * @param token
	 * @return
	 */
	Processable parseToken(final xill.lang.xill.IntegerLiteral token) {
		return Literal.fromValue(token.getValue());
	}

	/**
	 * Parse an {@link IntegerLiteral}
	 *
	 * @param token
	 * @return
	 */
	Processable parseToken(final xill.lang.xill.DecimalLiteral token) {
		return Literal.fromValue(token.getValue() + token.getDecimal() / 10.0);
	}

	/**
	 * Parse an {@link IntegerLiteral}
	 *
	 * @param token
	 * @return
	 */
	Processable parseToken(final xill.lang.xill.StringLiteral token) {
		return Literal.fromValue(token.getValue());
	}

	private CodePosition pos(final EObject object) {
		INode node = NodeModelUtils.getNode(object);
		RobotID id = robotID.get(object.eResource());
		return new CodePosition(id, node.getStartLine());
	}

	/**
	 * @return the debugger
	 */
	public Debugger getDebugger() {
		return debugger;
	}
}
