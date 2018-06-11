package com.etermax.tindercrack.server.category.resource.response

import com.etermax.tindercrack.server.category.model.Category

class CategoryResponse(category: Category) {

    val name = category.name
    val imagesCount = category.images.size
    val subcategoriesNames = category.subcategories.map { x -> x.name }

}