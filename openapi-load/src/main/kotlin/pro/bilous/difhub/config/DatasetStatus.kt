package pro.bilous.difhub.config

enum class DatasetStatus(val pathParam: String?) {
	DRAFT(null), APPROVED("Approved")
}
