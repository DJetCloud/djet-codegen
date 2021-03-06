package pro.bilous.intellij.plugin.project

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.ui.Messages.showErrorDialog
import com.intellij.ui.ScrollPaneFactory.createScrollPane
import com.intellij.ui.components.JBLoadingPanel
import pro.bilous.difhub.config.DatasetStatus
import java.awt.BorderLayout
import java.lang.Exception
import javax.swing.JComponent
import javax.swing.SwingUtilities.invokeLater

class ProjectDetailsStep(
	val moduleBuilder: ProjectModuleBuilder,
	val wizardContext: WizardContext
) : ModuleWizardStep(), Disposable {

	private val loadingPanel = JBLoadingPanel(BorderLayout(), this, 100)
	private var loadInProgress = false
	private val difHubDataLoader = DifHubDataLoader()
	private val request = moduleBuilder.request
	private var detailsForm: ProjectDetails? = null

	override fun _init() {
		if (request.getMetadata() != null) {
			return
		}
		val modelLoader = moduleBuilder.modelLoader ?: return
		val config = moduleBuilder.config ?: return

		// load systems and applications
		loadingPanel.contentPanel.removeAll()
		loadingPanel.startLoading()
		loadInProgress = true

		getApplication().executeOnPooledThread {
			try {
				val difHubData = difHubDataLoader.loadAllSystemsAndApps(modelLoader, config)
				request.difHubData = difHubData
				invokeLater {
					detailsForm = ProjectDetails(moduleBuilder, wizardContext).apply {
						loadingPanel.add(createScrollPane(fullPanel(), true), "North")
					}
				}
			} catch (e: Exception) {
				invokeLater {
					showErrorDialog(
						"Error while fetching metadata from DifHub server " +
								"\nPlease check URL, network and proxy settings.\n\nError message:\n" + e
							.message, "Fetch Error"
					)
				}
			} finally {
				invokeLater {
					loadingPanel.stopLoading()
					loadingPanel.revalidate()
				}
				loadInProgress = false
			}
		}
	}

	override fun updateDataModel() {
		detailsForm?.apply {
			request.defaultStringSize = defaultDatabaseStringLengthTextBox.text
			request.datasetStatus = DatasetStatus.valueOf((datasetStatusComboBox.selectedItem as String).toUpperCase())
		}
	}

	override fun getComponent(): JComponent {
		return loadingPanel
	}

	override fun dispose() {
	}
}
