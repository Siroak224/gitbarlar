package com.SpringBoot.Controller;

import java.util.List;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import com.SpringBoot.entity.Food;
import com.SpringBoot.service.FoodService;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "http://localhost:5173") // React port
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @PostMapping("/add")
    public ResponseEntity<Food> addFood(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Food food = new Food();
        food.setName(name);
        food.setDescription(description);
        food.setPrice(price);
        food.setPhotoData(file.getBytes()); // store file bytes

        Food saved = foodService.addFood(food);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Food food = foodService.getFoodById(id);
        if (food == null || food.getPhotoData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or IMAGE_PNG if your files are PNG
                .body(food.getPhotoData());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        boolean deleted = foodService.deleteFood(id); // implement in service
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
}
    @PutMapping("/edit/{id}")
    public ResponseEntity<Food> editFood(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        Food updated = foodService.updateFood(id, name, description, price, file);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

}