package com.escodro.category.presentation.detail

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escodro.category.mapper.CategoryMapper
import com.escodro.category.model.Category
import com.escodro.core.extension.toStringColor
import com.escodro.domain.usecase.category.LoadCategory
import com.escodro.domain.usecase.category.UpsertCategory
import kotlinx.coroutines.launch

/**
 * [ViewModel] responsible to provide information to [CategoryDetailFragment].
 */
internal class CategoryDetailViewModel(
    private val loadCategoryUseCase: LoadCategory,
    private val upsertCategoryUseCase: UpsertCategory,
    private val categoryMapper: CategoryMapper
) : ViewModel() {

    val newCategory = MediatorLiveData<String>()

    private val categoryData = MutableLiveData<Category>()

    init {
        newCategory.addSource(categoryData) { newCategory.value = it.name }
    }

    /**
     * Loads the categoryId based on the given id.
     *
     * @param categoryId categoryId id
     */
    fun loadCategory(categoryId: Long, onLoadCategory: (color: String) -> Unit) =
        viewModelScope.launch {
            val category = loadCategoryUseCase(categoryId)
            val view = category?.let { categoryMapper.toView(it) }
            categoryData.value = view
            category?.color?.let { onLoadCategory(it) }
        }

    /**
     * Adds or updates a category.
     */
    fun saveCategory(
        onEmptyField: () -> Unit,
        getCategoryColor: () -> Int?,
        onCategoryAdded: () -> Unit
    ) {
        val name = newCategory.value
        val color = getCategoryColor()?.toStringColor() ?: return

        if (name == null || TextUtils.isEmpty(name)) {
            onEmptyField()
            return
        }

        val category = if (isEditMode()) {
            getCurrentCategory(name, color)
        } else {
            getNewCategory(name, color)
        }

        viewModelScope.launch {
            upsertCategoryUseCase(categoryMapper.toDomain(category))
            onCategoryAdded()
        }
    }

    private fun isEditMode() = categoryData.value != null

    private fun getNewCategory(name: String, color: String): Category =
        Category(name = name, color = color)

    private fun getCurrentCategory(name: String, color: String): Category =
        categoryData.value?.copy(name = name, color = color)
            ?: getNewCategory(name, color)
}
