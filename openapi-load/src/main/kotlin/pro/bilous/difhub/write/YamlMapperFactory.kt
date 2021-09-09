package pro.bilous.difhub.write

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.swagger.v3.core.jackson.SchemaSerializer
import io.swagger.v3.core.jackson.mixin.*
import io.swagger.v3.core.util.DeserializationModule
import io.swagger.v3.oas.models.*
import io.swagger.v3.oas.models.callbacks.Callback
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.links.Link
import io.swagger.v3.oas.models.links.LinkParameter
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.servers.ServerVariable
import io.swagger.v3.oas.models.servers.ServerVariables
import io.swagger.v3.oas.models.tags.Tag

class YamlMapperFactory {

	fun createYaml(): ObjectMapper {

		val builder = YAMLFactory.builder().stringQuotingChecker(YamlStringChecker())

		val factory = builder.build()
		factory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
		factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
		factory.enable(YAMLGenerator.Feature.SPLIT_LINES)
		factory.enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
		return create(factory)
	}

	private fun create(jsonFactory: JsonFactory?): ObjectMapper {
		val mapper = if (jsonFactory == null) ObjectMapper() else ObjectMapper(jsonFactory)

		// handle ref schema serialization skipping all other props
		mapper.registerModule(object : SimpleModule() {
			override fun setupModule(context: SetupContext) {
				super.setupModule(context)
				context.addBeanSerializerModifier(object : BeanSerializerModifier() {
					override fun modifySerializer(
						config: SerializationConfig, desc: BeanDescription, serializer: JsonSerializer<*>?
					): JsonSerializer<*>? {
						return if (Schema::class.java.isAssignableFrom(desc.beanClass)) {
							SchemaSerializer(serializer as JsonSerializer<Any?>?)
						} else serializer
					}
				})
			}
		})
		val deserializerModule: Module = DeserializationModule()
		mapper.registerModule(deserializerModule)
		mapper.registerModule(JavaTimeModule())
		val sourceMixins: MutableMap<Class<*>, Class<*>> = LinkedHashMap()
		sourceMixins[ApiResponses::class.java] = ExtensionsMixin::class.java
		sourceMixins[ApiResponse::class.java] = ExtensionsMixin::class.java
		sourceMixins[Callback::class.java] = ExtensionsMixin::class.java
		sourceMixins[Components::class.java] = ComponentsMixin::class.java
		sourceMixins[Contact::class.java] = ExtensionsMixin::class.java
		sourceMixins[Encoding::class.java] = ExtensionsMixin::class.java
		sourceMixins[EncodingProperty::class.java] = ExtensionsMixin::class.java
		sourceMixins[Example::class.java] = ExtensionsMixin::class.java
		sourceMixins[ExternalDocumentation::class.java] = ExtensionsMixin::class.java
		sourceMixins[Header::class.java] = ExtensionsMixin::class.java
		sourceMixins[Info::class.java] = ExtensionsMixin::class.java
		sourceMixins[License::class.java] = ExtensionsMixin::class.java
		sourceMixins[Link::class.java] = ExtensionsMixin::class.java
		sourceMixins[LinkParameter::class.java] = ExtensionsMixin::class.java
		sourceMixins[MediaType::class.java] = ExtensionsMixin::class.java
		sourceMixins[OAuthFlow::class.java] = ExtensionsMixin::class.java
		sourceMixins[OAuthFlows::class.java] = ExtensionsMixin::class.java
		sourceMixins[OpenAPI::class.java] = OpenAPIMixin::class.java
		sourceMixins[Operation::class.java] = OperationMixin::class.java
		sourceMixins[Parameter::class.java] = ExtensionsMixin::class.java
		sourceMixins[PathItem::class.java] = ExtensionsMixin::class.java
		sourceMixins[Paths::class.java] = ExtensionsMixin::class.java
		sourceMixins[RequestBody::class.java] = ExtensionsMixin::class.java
		sourceMixins[Scopes::class.java] = ExtensionsMixin::class.java
		sourceMixins[SecurityScheme::class.java] = ExtensionsMixin::class.java
		sourceMixins[Server::class.java] = ExtensionsMixin::class.java
		sourceMixins[ServerVariable::class.java] = ExtensionsMixin::class.java
		sourceMixins[ServerVariables::class.java] = ExtensionsMixin::class.java
		sourceMixins[Tag::class.java] = ExtensionsMixin::class.java
		sourceMixins[XML::class.java] = ExtensionsMixin::class.java
		sourceMixins[Schema::class.java] = SchemaMixin::class.java
		sourceMixins[DateSchema::class.java] = DateSchemaMixin::class.java
		mapper.setMixIns(sourceMixins)
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
		mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true)
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
		return mapper
	}
}
