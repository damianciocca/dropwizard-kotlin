package com.etermax.tindercrack.server.category.repository

import com.etermax.tindercrack.server.category.model.Category
import mu.KLogging
import java.io.File

class CategoryRepository(val categoriesFolderPath: String, val metacategoriesFilePath: String) {

    companion object : KLogging()

    private val categories = HashMap<String, Category>()
    private val maxFileSize = 100_000

    init {

        if (!File(categoriesFolderPath).exists() || !File(categoriesFolderPath).isDirectory()) {
            throw Exception("Invalid categoriesFolderPath value '$categoriesFolderPath' in config.yml")
        }

        if (!File(metacategoriesFilePath).exists() || !File(metacategoriesFilePath).isFile()) {
            throw Exception("Invalid metacategoriesFilePath value '$metacategoriesFilePath' in config.yml")
        }

        loadCategories()
    }

    fun loadCategories() {

        categories.clear()

        val categoriesFile = File(categoriesFolderPath)

        for (file in categoriesFile.listFiles()) {
            if (file.isDirectory) {

                val images = file.listFiles({ _, name -> name.endsWith(".jpg") }).filter { image -> image.length() < maxFileSize }.toList()

                if (images.isEmpty())
                    continue

                var categoryName = file.name //
                        .substring(4) //skip "XXX." start
                        .replace("-", " ")
                        .replace("101", "")
                        .trim()

                val category = Category(categoryName, file.absolutePath, images, listOf())

                categories[category.name] = category
            }
        }

        loadMetaCategories()
    }

    private fun loadMetaCategories() {

        val metacategoriesFile = File(metacategoriesFilePath)

        val lines = metacategoriesFile.readLines()

        val linesAndRows = splitInRows(lines)

        if (linesAndRows.isEmpty())
            return

        val colsDescription = linesAndRows.first()

        for (colNumber in 1 until colsDescription.size) {

            val metaCategoryName = colsDescription[colNumber]

            if (categories[metaCategoryName] != null) {
                logger.error("Duplicated category name $metaCategoryName in $metacategoriesFilePath")
                continue
            }

            val subcategories = mutableListOf<Category>()

            for (rows in linesAndRows.subList(1, linesAndRows.size)) {

                if (rows[colNumber].isEmpty())
                    continue

                val subcategoryName = rows.first()
                val subcategory = categories[subcategoryName]

                if (subcategory == null) {
                    logger.error("Category $subcategoryName required by $metacategoriesFilePath not found")
                    continue
                }

                subcategories.add(subcategory)
            }

            if (subcategories.isEmpty()) {
                logger.error("Ignoring empty meta category $metaCategoryName in $metacategoriesFilePath")
                continue
            }

            val metaCategory = Category(metaCategoryName, "", listOf(), subcategories)
            categories[metaCategory.name] = metaCategory
        }
    }

    private fun splitInRows(lines: List<String>): List<List<String>> {
        val linesAndRows = mutableListOf<List<String>>()

        for (line in lines) {

            val rows = line.split(",").map { x -> x.trim() }

            if (rows.size > 1 && rows[0].isNotEmpty()) {

                if (linesAndRows.size > 0 && rows.size != linesAndRows.first().size)
                    continue

                linesAndRows.add(rows)
            }
        }

        return linesAndRows
    }

    fun getCategories(): Collection<Category> {
        return categories.values
    }

    fun getCategory(id: String): Category? {
        return categories[id]
    }

}