package com.blog.blog_app.services.impl;

import com.blog.blog_app.dto.CategoryDto;
import com.blog.blog_app.entities.Category;
import com.blog.blog_app.exceptions.ResourceNotFoundException;
import com.blog.blog_app.repositories.CategoryRepository;
import com.blog.blog_app.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@EnableCaching
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
     Category cat=   this.modelMapper.map(categoryDto, Category.class);
     Category addCategory=this.categoryRepository.save(cat);
     return this.modelMapper.map(addCategory,CategoryDto.class);
    }

    @Override
    @CachePut(value = "categories", key = "#categoryId")
    public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {
        Category cat = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category ", "Category Id", categoryId));

        cat.setCategoryTitle(categoryDto.getCategoryTitle());
        cat.setCategoryDescription(categoryDto.getCategoryDescription());

        Category updatedcat = this.categoryRepository.save(cat);

        return this.modelMapper.map(updatedcat, CategoryDto.class);
    }

    @Override
    @CacheEvict(value = "categories", key = "#categoryId")
    public void deleteCategory(Integer categoryId) {
        Category cat = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category ", "category id", categoryId));
        this.categoryRepository.delete(cat);
    }

    @Override
    @Cacheable(value = "categories", key = "#categoryId")
    public CategoryDto getCategory(Integer categoryId) {
        log.info("Fetching category with id {} from database", categoryId);  // Log before DB access
        Category cat = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        return this.modelMapper.map(cat, CategoryDto.class);
    }



    @Override
    @Cacheable(value = "allCategories")
    public List<CategoryDto> getCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        List<CategoryDto> catDtos = categories.stream().map((cat) -> this.modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
        return catDtos;
    }
}
