package com.etermax.tindercrack.server.healthcheck


import com.codahale.metrics.health.HealthCheck

class DefaultHealthCheck : HealthCheck() {
    override fun check(): HealthCheck.Result {
        return HealthCheck.Result.healthy()
    }
}
