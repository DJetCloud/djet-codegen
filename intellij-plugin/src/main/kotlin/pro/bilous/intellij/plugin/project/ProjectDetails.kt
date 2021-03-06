package pro.bilous.intellij.plugin.project

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.CheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.*
import pro.bilous.difhub.config.DatasetStatus
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

class ProjectDetails(moduleBuilder: ProjectModuleBuilder, wizardContext: WizardContext) {

	var rootPanel: DialogPanel? = null
	val request = moduleBuilder.request
	val difHubData = request.difHubData

	val systemComboBox = ComboBox(DefaultComboBoxModel(difHubData!!.keys.toTypedArray()))

	//    val appComboBoxModel = DefaultComboBoxModel<String>()
//    val appComboBox = ComboBox<String>(appComboBoxModel)
	val databaseComboBox = ComboBox(DefaultComboBoxModel(arrayOf("MySQL", "PostgreSQL")))
	val enableAuthorizationCheckBox = CheckBox(
		"", false,
		"Keycloak configuration will be added to project"
	)
	val defaultDatabaseStringLengthTextBox = JBTextField(request.defaultStringSize)
	val datasetStatusComboBox = ComboBox(DefaultComboBoxModel(DatasetStatus.values().map { it.name.toLowerCase() }.toTypedArray()))

	init {
		datasetStatusComboBox.selectedItem = DatasetStatus.APPROVED.name.toLowerCase()
		systemComboBox.addActionListener {
			request.system = systemComboBox.selectedItem as String
			selectSystemApplications(request.system)
		}
//        appComboBox.addActionListener {
//            if (appComboBox.selectedItem != null) {
//                request.application = appComboBox.selectedItem as String
//                selectApplication(request.application)
//                rootPanel?.reset()
//            }
//        }
		if (difHubData != null) {
			selectSystemApplications(difHubData.keys.first())
		}
		databaseComboBox.addActionListener {
			request.database = databaseComboBox.selectedItem as String
		}
		enableAuthorizationCheckBox.addActionListener {
			request.authorizationEnabled = enableAuthorizationCheckBox.isSelected
		}
		datasetStatusComboBox.addActionListener{
			request.datasetStatus = DatasetStatus.valueOf((datasetStatusComboBox.selectedItem as String).toUpperCase())
		}
	}

	fun fullPanel(): JPanel {
		rootPanel = panel(LCFlags.fillX) {
			titledRow("Select DifHub system") {
				row("Select System:") { systemComboBox() }
//                row("Select Application:") { appComboBox() }
			}
			titledRow("Configure project properties") {
				row("Group Id") { textField(request::groupId) }
				row("Artifact Id") { textField(request::artifactId) }
				row("Version") { textField(request::version) }
				row("Title") { textField(request::title) }
				row("Description") { textField(request::description) }
				row("Base Package") { textField(request::basePackage) }
				row("Database") { databaseComboBox() }
				row("Enable Authorization") { enableAuthorizationCheckBox() }
//                row("DB Name") { textField(request::dbName) }
//                row("Binding Entity") { checkBox("", request::addBindingEntity) }
			}
			titledRow("Advanced parameters") {
				row("Default size of string in database") { defaultDatabaseStringLengthTextBox() }
				row("Dataset status") { datasetStatusComboBox() }
			}
		}
		rootPanel?.preferredSize = Dimension(400, 550)
		return rootPanel!!
	}

	private fun selectSystemApplications(value: String?) {
		if (value == null) {
			return
		}
//        appComboBoxModel.removeAllElements()
		val apps = difHubData?.get(value)
		request.applications.clear()
		request.applications.addAll(apps!!)
//        apps?.forEach {
//            appComboBoxModel.addElement(it)
//        }
//        appComboBox.selectedItem = apps?.first()
		request.system = value
		selectApplication(value)
		rootPanel?.reset()
	}

	private fun selectApplication(appName: String?) {
		requireNotNull(appName)
		val lower = appName.toLowerCase()
		request.groupId = "com.$lower"
		request.artifactId = lower
		request.title = "$appName System"
		request.description = "$appName System"
		request.basePackage = "com.${lower}"
//        request.dbName = "${lower}db"
	}
}
