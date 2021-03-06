package com.amir.todone.Domain.Category;

import android.content.Context;

import androidx.annotation.NonNull;

import com.amir.todone.DA.Da;
import com.amir.todone.Domain.Task.Task;

import java.util.List;

public class CategoryManager {

    private final Context context;

    private static CategoryManager categoryManager;
    private CategoryManager(Context context) {
        this.context = context;
    }
    public static CategoryManager getInstance(Context context){
        if (categoryManager==null)
            categoryManager = new CategoryManager(context);
        return categoryManager;
    }

    public List<Category> getAllCategories(){
        return Da.getInstance(context).getAllCategories();
    }

    public int getCategoriesCount(){
        return Da.getInstance(context).getCategoriesCount();
    }

    public Category getCategoryById(String id){
        return Da.getInstance(context).getCategoryById(id);
    }

    public void aTaskLeftThisCategory(String id){
        Da.getInstance(context).aTaskLeftTHisCategory(id);
    }

    public List<Task> getCategoryTasks(Category category){
        return Da.getInstance(context).getCategoryTasks(category);
    }

    public void deleteCategory(Category category , boolean deleteItsTasks){
        Da.getInstance(context).deleteCategory(category,deleteItsTasks);

    }
    public void createCategory(@NonNull Category category){
        category.setId(Da.getInstance(context).createCategory(category));
    }

    public void editCategoryName(@NonNull Category category,String newName){
        category.editName(newName);
        Da.getInstance(context).editCategoryName(category,newName);
    }

}
