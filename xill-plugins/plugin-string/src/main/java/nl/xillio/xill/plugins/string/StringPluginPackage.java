package nl.xillio.xill.plugins.string;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.string.constructs.AbsoluteURLConstruct;
import nl.xillio.xill.plugins.string.constructs.AllMatchesConstruct;
import nl.xillio.xill.plugins.string.constructs.AmpersandDecodeConstruct;
import nl.xillio.xill.plugins.string.constructs.AmpersandEncodeConstruct;
import nl.xillio.xill.plugins.string.constructs.Base64DecodeConstruct;
import nl.xillio.xill.plugins.string.constructs.Base64EncodeConstruct;
import nl.xillio.xill.plugins.string.constructs.ContainsConstruct;
import nl.xillio.xill.plugins.string.constructs.EndsWithConstruct;
import nl.xillio.xill.plugins.string.constructs.FormatConstruct;
import nl.xillio.xill.plugins.string.constructs.IndexOfConstruct;
import nl.xillio.xill.plugins.string.constructs.JoinConstruct;
import nl.xillio.xill.plugins.string.constructs.LengthConstruct;
import nl.xillio.xill.plugins.string.constructs.LowerCaseConstruct;
import nl.xillio.xill.plugins.string.constructs.MD5Construct;
import nl.xillio.xill.plugins.string.constructs.MatchesConstruct;
import nl.xillio.xill.plugins.string.constructs.RegexConstruct;
import nl.xillio.xill.plugins.string.constructs.RepeatConstruct;
import nl.xillio.xill.plugins.string.constructs.ReplaceConstruct;
import nl.xillio.xill.plugins.string.constructs.SplitConstruct;
import nl.xillio.xill.plugins.string.constructs.StartsWithConstruct;
import nl.xillio.xill.plugins.string.constructs.SubstringConstruct;
import nl.xillio.xill.plugins.string.constructs.TrimConstruct;
import nl.xillio.xill.plugins.string.constructs.UpperCaseConstruct;
import nl.xillio.xill.plugins.string.constructs.WordDistanceConstruct;
import nl.xillio.xill.plugins.string.constructs.WrapConstruct;

/**
 * This package includes all Text constructs
 */
public class StringPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		RegexConstruct regex = new RegexConstruct();

		add(
			new AbsoluteURLConstruct(),
			new AllMatchesConstruct(regex),
			new AmpersandDecodeConstruct(),
			new AmpersandEncodeConstruct(),
			new Base64DecodeConstruct(),
			new Base64EncodeConstruct(),
			new ContainsConstruct(),
			new EndsWithConstruct(),
			new FormatConstruct(regex),
			new IndexOfConstruct(),
			new JoinConstruct(),
			new LengthConstruct(),
			new LowerCaseConstruct(),
			new MatchesConstruct(regex),
			new MD5Construct(),
			regex,
			new RepeatConstruct(),
			new ReplaceConstruct(regex),
			new SplitConstruct(),
			new StartsWithConstruct(),
			new SubstringConstruct(),
			new TrimConstruct(),
			new UpperCaseConstruct(),
			new WordDistanceConstruct(),
			new WrapConstruct());
	}

	@Override
	public String getName() {
		return "String";
	}

}
