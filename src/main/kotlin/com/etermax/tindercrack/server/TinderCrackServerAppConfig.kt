package com.etermax.tindercrack.server

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.Valid
import javax.validation.constraints.NotNull


class TinderCrackServerAppConfig : Configuration() {

    @NotEmpty
    var categoriesFolderPath: String = ""

    @NotEmpty
    var metacategoriesFilePath: String = ""

    @NotEmpty
    var cacheFilesPath: String = ""

    var useSvgExport: Boolean = false

    @Valid
    @NotNull
    private var database = DataSourceFactory()

    @JsonProperty("database")
    fun setDataSourceFactory(factory: DataSourceFactory) {
        this.database = factory
    }

    @JsonProperty("database")
    fun getDataSourceFactory(): DataSourceFactory {
        return database
    }
}
