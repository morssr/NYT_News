package com.mls.mor.nytnews.ui.common

sealed class StateProductionError {
    object NoInternet : StateProductionError()
    object EndpointError : StateProductionError()
    object Unauthorized : StateProductionError()
    object Unknown : StateProductionError()
}