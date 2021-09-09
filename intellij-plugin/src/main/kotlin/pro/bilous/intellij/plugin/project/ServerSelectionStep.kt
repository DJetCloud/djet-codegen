package pro.bilous.intellij.plugin.project

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.TextFieldWithHistory
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import org.apache.commons.lang3.StringUtils
import pro.bilous.difhub.config.ConfigReader
import pro.bilous.difhub.load.DifHubLoader
import pro.bilous.difhub.load.ModelLoader
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import javax.swing.JComponent

class ServerSelectionStep(private val moduleBuilder: ProjectModuleBuilder) : ModuleWizardStep() {

    private val userField = JBTextField()
    private val passwordField = JBTextField()
    private val organizationField = TextFieldWithHistory()

    val difhub = ConfigReader.loadConfig().difhub

    override fun updateDataModel() {
		moduleBuilder.request.username = userField.text
		moduleBuilder.request.password = passwordField.text
		moduleBuilder.request.organization = organizationField.text

		val username = moduleBuilder.request.username ?: return
		val password = moduleBuilder.request.password ?: return
		val organization = moduleBuilder.request.organization
		val config = ConfigReader.loadConfig(organization)
		moduleBuilder.config = config
		moduleBuilder.modelLoader = ModelLoader(DifHubLoader(username, password, config))
    }

    override fun getComponent(): JComponent {
        return panel {
            titledRow("DifHub Credentials") {
                row {
                    cell {
                        label("Username:")
                        userField()
                    }
                }.enabled = true
                row {
                    cell {
                        label("Password: ")
                        passwordField()
                    }
                }.enabled = true
            }
            titledRow("Configure DifHub Organization") {
                row {
                    cell {
                        label("Organization Name")
                        organizationField()
                    }
                }.enabled = true
            }
        }
    }

    override fun validate(): Boolean {
        if (userField.text.isEmpty()) {
            throw ConfigurationException("Username must be set")
        }
        if (passwordField.text.isEmpty()) {
            throw ConfigurationException("Password must be set")
        }

        val serverUrl = "${difhub.api}/${URLEncoder.encode(organizationField.text, "utf-8")}"

        if (StringUtils.isEmpty(serverUrl)) {
            throw ConfigurationException("Organization Name must be set")
        }
        try {
            URI(serverUrl)
            return true
        } catch (e: URISyntaxException) {
            throw ConfigurationException("Server URL must be a valid url")
        }
    }

}
