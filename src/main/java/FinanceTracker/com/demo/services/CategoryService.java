package FinanceTracker.com.demo.services;

import FinanceTracker.com.demo.dto.CategoryResponseDto;
import FinanceTracker.com.demo.entities.Category;
import FinanceTracker.com.demo.entities.User;
import FinanceTracker.com.demo.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserService userService, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
    }

    public CategoryResponseDto createCategory(CategoryResponseDto categoryDto) {
        User currentUser = getCurrentUser();
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setUser(currentUser);

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponseDto.class);
    }

    public List<CategoryResponseDto> getAllUserCategories() {
        User currentUser = getCurrentUser();
        List<Category> categories = categoryRepository.findAllByUserId(currentUser.getId());

        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseDto.class))
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id) {
        User currentUser = getCurrentUser();
        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or access denied."));

        return modelMapper.map(category, CategoryResponseDto.class);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryResponseDto categoryDto) {
        User currentUser = getCurrentUser();
        Category existingCategory = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or access denied."));

        existingCategory.setName(categoryDto.getName());
        existingCategory.setType(categoryDto.getType());
        existingCategory.setColor(categoryDto.getColor());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryResponseDto.class);
    }

    public void deleteCategory(Long id) {
        User currentUser = getCurrentUser();
        Category existingCategory = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or access denied."));

        categoryRepository.delete(existingCategory);
    }

    // ✅ Fixed: returns actual Category entity from DB instead of null
    public Category getCategoryEntityById(Long categoryId) {
        User currentUser = getCurrentUser();
        return categoryRepository.findByIdAndUserId(categoryId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or access denied."));
    }
}