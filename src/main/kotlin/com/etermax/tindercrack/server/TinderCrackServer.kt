package com.etermax.tindercrack.server

import com.etermax.tindercrack.server.category.repository.CategoryRepository
import com.etermax.tindercrack.server.category.resource.CategoryResource
import com.etermax.tindercrack.server.filter.DiagnosticContextFilter
import com.etermax.tindercrack.server.healthcheck.DefaultHealthCheck
import com.etermax.tindercrack.server.image.ImageCache
import com.etermax.tindercrack.server.image.ImageResource
import com.etermax.tindercrack.server.image.ImageUtils
import com.etermax.tindercrack.server.image.SvgToPngConverter
import com.etermax.tindercrack.server.resource.RootResource
import com.etermax.tindercrack.server.user.repository.UserRepository
import com.etermax.tindercrack.server.user.resource.UserResource
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.setup.Environment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection


class TinderCrackServer : Application<TinderCrackServerAppConfig>() {


    override fun run(config: TinderCrackServerAppConfig, env: Environment) {

        val databaseConfig = config.getDataSourceFactory()

        initDatabase(databaseConfig)

        val userRepository = UserRepository()
        val categoryRepository = CategoryRepository(config.categoriesFolderPath, config.metacategoriesFilePath)

        env.jersey().register(RootResource())
        env.jersey().register(CategoryResource(categoryRepository))
        env.jersey().register(UserResource(userRepository))
        env.jersey().register(ImageResource(ImageCache(config.cacheFilesPath), ImageUtils(SvgToPngConverter(config.useSvgExport))))
        env.jersey().register(DiagnosticContextFilter())
        env.healthChecks().register("default", DefaultHealthCheck())
    }

    private fun initDatabase(databaseConfig: DataSourceFactory) {
        Database.connect(databaseConfig.url, databaseConfig.driverClass, databaseConfig.user, databaseConfig.password)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TinderCrackServer().run(*args)
        }
    }

}

