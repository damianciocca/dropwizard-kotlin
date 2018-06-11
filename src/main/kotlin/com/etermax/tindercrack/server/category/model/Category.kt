package com.etermax.tindercrack.server.category.model

import java.io.File

data class Category(val name: String, val fullPath: String, val images: List<File>, val subcategories: List<Category>)