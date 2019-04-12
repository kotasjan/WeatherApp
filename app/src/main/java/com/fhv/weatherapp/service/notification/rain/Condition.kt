package com.fhv.weatherapp.service.notification.rain

class Condition(currentRainProbabilityThreshold: Double) {
    private val conditions: MutableMap<Int, Double> = mutableMapOf()

    init {
        conditions[0] = currentRainProbabilityThreshold
    }

    fun upsertRainProbabilityThresholdIn(hours: Int, value: Double): Double? {
        return conditions.put(hours, value)
    }

    fun getCurrentRainProbabilityThreshold(): Double {
        return conditions[0]!!
    }

    fun getRainProbabilityThresholds(): Map<Int, Double> {
        return conditions.minus(0)
    }
}