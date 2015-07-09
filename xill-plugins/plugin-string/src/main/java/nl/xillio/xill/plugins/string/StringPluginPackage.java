package nl.xillio.xill.plugins.string;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all Text constructs
 */
public class StringPluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
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
			new WrapConstruct()
			);
			
	}

	@Override
	public String getName() {
		return "String";
	}

}
