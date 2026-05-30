package com.example.foodie.config;

import com.example.foodie.farmer.internal.Farmer;
import com.example.foodie.farmer.internal.FarmerRepository;
import com.example.foodie.product.internal.Category;
import com.example.foodie.product.internal.Product;
import com.example.foodie.product.internal.ProductRepository;
import com.example.foodie.user.internal.User;
import com.example.foodie.user.internal.UserRepository;
import com.example.foodie.user.roletype.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")                  // only runs on dev profile
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {
        seedUsers();
        seedFarmers();
        seedProducts();
        log.info("Dev data seeding completed");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        User john = new User();
        john.setFullName("John Consumer");
        john.setEmail("john@example.com");
        john.setPassword(passwordEncoder.encode("password123"));
        john.setPhone("6901234567");
        john.setAddress("Athens, Greece");
        john.setRole(Role.CONSUMER);
        userRepository.save(john);

        User maria = new User();
        maria.setFullName("Maria Consumer");
        maria.setEmail("maria@example.com");
        maria.setPassword(passwordEncoder.encode("password123"));
        maria.setPhone("6907654321");
        maria.setAddress("Thessaloniki, Greece");
        maria.setRole(Role.CONSUMER);
        userRepository.save(maria);

        User admin = new User();
        admin.setFullName("Admin User");
        admin.setEmail("admin@foodie.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("6900000000");
        admin.setAddress("Athens, Greece");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("Seeded {} users", userRepository.count());
    }

    private void seedFarmers() {
        if (farmerRepository.count() > 0) return;

        Farmer nikos = new Farmer();
        nikos.setFullName("Nikos Farmer");
        nikos.setEmail("nikos@farm.com");
        nikos.setPassword(passwordEncoder.encode("password123"));
        nikos.setPhone("6911111111");
        nikos.setFarmName("Nikos Fresh Farm");
        nikos.setFarmLocation("Crete, Greece");
        nikos.setDescription("Fresh organic vegetables from Crete");
        nikos.setVerified(true);
        farmerRepository.save(nikos);

        Farmer elena = new Farmer();
        elena.setFullName("Elena Farmer");
        elena.setEmail("elena@farm.com");
        elena.setPassword(passwordEncoder.encode("password123"));
        elena.setPhone("6922222222");
        elena.setFarmName("Elena Dairy Farm");
        elena.setFarmLocation("Epirus, Greece");
        elena.setDescription("Best dairy products from Epirus mountains");
        elena.setVerified(true);
        farmerRepository.save(elena);

        Farmer kostas = new Farmer();
        kostas.setFullName("Kostas Farmer");
        kostas.setEmail("kostas@farm.com");
        kostas.setPassword(passwordEncoder.encode("password123"));
        kostas.setPhone("6933333333");
        kostas.setFarmName("Kostas Honey Farm");
        kostas.setFarmLocation("Thessaly, Greece");
        kostas.setDescription("Pure thyme honey from Thessaly");
        kostas.setVerified(false);
        farmerRepository.save(kostas);

        log.info("Seeded {} farmers", farmerRepository.count());
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;

        String nikosId = farmerRepository.findByEmail("nikos@farm.com").get().getId();
        String elenaId = farmerRepository.findByEmail("elena@farm.com").get().getId();
        String kostasId = farmerRepository.findByEmail("kostas@farm.com").get().getId();

        // Nikos products
        saveProduct("Organic Tomatoes", "Fresh organic tomatoes from Crete", new BigDecimal("2.50"), "kg", 100, Category.VEGETABLES, nikosId);
        saveProduct("Cucumber", "Crispy fresh cucumbers", new BigDecimal("1.80"), "kg", 80, Category.VEGETABLES, nikosId);
        saveProduct("Zucchini", "Farm fresh zucchini", new BigDecimal("2.00"), "kg", 60, Category.VEGETABLES, nikosId);

        // Elena products
        saveProduct("Feta Cheese", "Traditional Greek feta cheese", new BigDecimal("8.00"), "kg", 40, Category.DAIRY, elenaId);
        saveProduct("Fresh Milk", "Full fat fresh goat milk", new BigDecimal("1.50"), "litre", 50, Category.DAIRY, elenaId);
        saveProduct("Greek Yogurt", "Thick creamy Greek yogurt", new BigDecimal("3.00"), "kg", 30, Category.DAIRY, elenaId);

        // Kostas products
        saveProduct("Thyme Honey", "Pure thyme honey from Thessaly mountains", new BigDecimal("12.00"), "kg", 20, Category.HONEY, kostasId);
        saveProduct("Pine Honey", "Dark rich pine honey", new BigDecimal("10.00"), "kg", 15, Category.HONEY, kostasId);

        log.info("Seeded {} products", productRepository.count());
    }

    private void saveProduct(String name, String description, BigDecimal price,
                             String unit, int stock, Category category, String farmerId) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setUnit(unit);
        p.setStockQuantity(stock);
        p.setCategory(category);
        p.setFarmerId(farmerId);
        p.setAvailable(true);
        productRepository.save(p);
    }
}