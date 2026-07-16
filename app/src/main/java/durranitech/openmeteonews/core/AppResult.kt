package durranitech.openmeteonews.core

sealed interface AppResult<out T> {

	data class Success<T>(val data: T) : AppResult<T>
	data class Error(val message: String, val throwable: Throwable? = null) : AppResult<Nothing>
	data object Loading : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
	is AppResult.Success -> AppResult.Success(transform(data))
	is AppResult.Loading -> this
	is AppResult.Error -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T>{
	if (this is AppResult.Success) action(data)
	return this
}

inline fun <T> AppResult<T>.onError(action: (AppResult.Error) -> Unit): AppResult<T> {
	if (this is AppResult.Error) action(this)
	return this
}