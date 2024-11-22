package org.ecolight.models

data class Consumption(
    var id: String? = null,
    var name: String? = null,
    var timeUsed: String? = null,
    var deviceId: String? = null,
    var deviceName: String? = null
)
