package app.example.screens

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class Effect

abstract class AppStateScreenModel<S>(initialState: S) : ScreenModel {

    //region StateScreenModel
    val mutableState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> = mutableState.asStateFlow()
    //endregion

    private val sideEffect = MutableSharedFlow<Effect>()
    fun observeSideEffect(): Flow<Effect> = sideEffect

    suspend fun emitSideEffect(effect: Effect) {
        sideEffect.emit(effect)
    }
}
