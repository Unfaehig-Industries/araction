package tech.tooz.bto.toozifier.examples

data class Resource(
    val state: ViewState,
    val errorMessage: String? = null
)

enum class ViewState {
    SUCCESS, ERROR, LOADING
}