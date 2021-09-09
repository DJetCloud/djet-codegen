package pro.bilous.difhub.write

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker

class YamlStringChecker : StringQuotingChecker() {

	override fun needToQuoteName(name: String?): Boolean {
		return isReservedKeyword(name) || looksLikeYAMLNumber(name)
	}

	override fun needToQuoteValue(value: String?): Boolean {
		return isReservedKeyword(value) || valueHasQuotableChar(value)
	}

	override fun valueHasQuotableChar(inputStr: String?): Boolean {
		return false
	}
}
