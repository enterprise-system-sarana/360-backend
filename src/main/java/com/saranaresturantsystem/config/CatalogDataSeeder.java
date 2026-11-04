package com.saranaresturantsystem.config;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.catalog.Brand;
import com.saranaresturantsystem.entities.catalog.Category;
import com.saranaresturantsystem.entities.catalog.Model;
import com.saranaresturantsystem.entities.catalog.Product;
import com.saranaresturantsystem.entities.catalog.VariantType;
import com.saranaresturantsystem.entities.catalog.VariantValue;
import com.saranaresturantsystem.entities.finances.Currency;
import com.saranaresturantsystem.repository.catalog.BrandRepository;
import com.saranaresturantsystem.repository.catalog.CategoryRepository;
import com.saranaresturantsystem.repository.catalog.ModelRepository;
import com.saranaresturantsystem.repository.catalog.ProductRepository;
import com.saranaresturantsystem.repository.catalog.VariantTypeRepository;
import com.saranaresturantsystem.repository.catalog.VariantValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogDataSeeder implements CommandLineRunner {

    private static final String SEED_KEY = "CATALOG_IPAD_PRODUCTS";
    private static final String VARIANT_SEED_KEY = "CATALOG_VARIANT_TYPES";

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final ProductRepository productRepository;
    private final VariantTypeRepository variantTypeRepository;
    private final VariantValueRepository variantValueRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.seeder.enabled:true}")
    private boolean seederEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seederEnabled) {
            log.info("Catalog data seeder is disabled by configuration. Skipping.");
            return;
        }

        ensureSeedHistoryTable();
        seedVariantTypesAndValues();
        seedProductImages();
        if (isSeeded()) {
            log.info("Catalog seed already completed. Skipping.");
            return;
        }

        Category ipad = getOrCreateCategory("Ipad", "IPAD");
        getOrCreateCategory("Iphone", "IPHONE");
        getOrCreateCategory("Android", "ANDROID");
        Brand apple = getOrCreateBrand("Apple");
        getOrCreateBrand("Samsung");

        Map<String, Model> models = new HashMap<>();
        for (String modelName : MODEL_NAMES) {
            models.put(modelName, getOrCreateModel(modelName, apple, ipad));
        }

        seedProducts(models);
        markSeeded();
        log.info("Catalog seed completed: categories, brands, iPad models, and products.");
    }

    private void seedVariantTypesAndValues() {
        if (isSeeded(VARIANT_SEED_KEY)) {
            return;
        }

        seedVariantType("Color", new String[]{"Black", "White", "Silver", "Gold", "Blue", "Purple", "Green"});
        seedVariantType("Storage", new String[]{"64GB", "128GB", "256GB", "512GB", "1TB", "2TB"});
        seedVariantType("RAM", new String[]{"4GB", "6GB", "8GB", "12GB", "16GB"});
        seedVariantType("Network", new String[]{"4G", "5G"});
        seedVariantType("SIM Type", new String[]{ "Single SIM + eSIM", "Dual SIM + eSIM", "SIM + WIFI", "eSIM + WIFI" ,"eSIM", "SIM ONLY", "WIFI ONLY"});
        seedVariantType("Condition", new String[]{"New", "Used", "Refurbished"});
        seedVariantType("Region", new String[]{"Global", "USA", "Japan", "China", "Korea"});
        seedVariantType("Warranty", new String[]{"3 Months", "6 Months", "12 Months"});
        seedVariantType("Battery Health", new String[]{"80%", "85%", "90%", "95%", "100%"});

        markSeeded(VARIANT_SEED_KEY);
        log.info("Catalog variant types and values seed completed.");
    }

    private void seedVariantType(String name, String[] values) {
        VariantType variantType = variantTypeRepository.findAll().stream()
                .filter(existing -> name.equalsIgnoreCase(existing.getName()))
                .findFirst()
                .orElseGet(() -> {
                    VariantType created = new VariantType();
                    created.setCode(toCode(name));
                    created.setName(name);
                    created.setStatus(Constants.STATUS_ACTIVE);
                    return variantTypeRepository.save(created);
                });

        for (String value : values) {
            boolean exists = variantValueRepository.findAll().stream()
                    .anyMatch(existing -> value.equalsIgnoreCase(existing.getName())
                            && existing.getVariantType() != null
                            && variantType.getId().equals(existing.getVariantType().getId()));
            if (!exists) {
                VariantValue variantValue = new VariantValue();
                variantValue.setCode(toCode(value));
                variantValue.setName(value);
                variantValue.setVariantType(variantType);
                variantValue.setStatus(Constants.STATUS_ACTIVE);
                variantValueRepository.save(variantValue);
            }
        }
    }

    private void seedProducts(Map<String, Model> models) {
        seedStorageProducts(models, "Ipad Mini5", new String[]{"64GB", "256GB"}, new String[]{"wifi", "sim wifi"});
        seedStorageProducts(models, "Ipad Mini6", new String[]{"64GB", "256GB"}, new String[]{"wifi", "sim wifi"});
        seedStorageProducts(models, "Ipad Mini7", new String[]{"128GB", "256GB"}, new String[]{"wifi", "esim wifi"});

        for (String model : new String[]{"Ipad6th", "Ipad7th", "Ipad8th"}) {
            seedStorageProducts(models, model, new String[]{"32GB", "128GB"}, new String[]{"wifi", "sim wifi"});
        }
        seedStorageProducts(models, "Ipad9th", new String[]{"64GB", "256GB"}, new String[]{"wifi", "sim wifi"});
        seedStorageProducts(models, "Ipad10th", new String[]{"64GB", "256GB"}, new String[]{"wifi", "sim wifi"});
        seedStorageProducts(models, "Ipad11th", new String[]{"128GB", "256GB"}, new String[]{"wifi", "esim wifi"});

        for (String model : new String[]{"Ipad Air3", "Ipad Air4", "Ipad Air5"}) {
            seedStorageProducts(models, model, new String[]{"64GB", "256GB"}, new String[]{"wifi", "sim wifi"});
        }
        for (String model : new String[]{"Ipad AirM2", "Ipad AirM3", "Ipad AirM4"}) {
            seedStorageProducts(models, model, new String[]{"128GB", "256GB"}, new String[]{"wifi", "esim wifi"});
            seedStorageProducts(models, model + " 13", new String[]{"128GB", "256GB"}, new String[]{"wifi", "esim wifi"});
        }

        for (String model : new String[]{"Ipad Pro18", "Ipad Pro20", "Ipad Pro21", "Ipad Pro22"}) {
            seedStorageProducts(models, model, new String[]{"64GB", "256GB", "512GB", "1TB"}, new String[]{"wifi", "sim wifi"});
            seedStorageProducts(models, model + " 12.9", new String[]{"64GB", "256GB", "512GB", "1TB"}, new String[]{"wifi", "sim wifi"});
        }
        for (String model : new String[]{"Ipad ProM4", "Ipad ProM5"}) {
            seedStorageProducts(models, model, new String[]{"256GB", "512GB", "1TB", "2TB"}, new String[]{"wifi", "esim wifi"});
            seedStorageProducts(models, model + " 13", new String[]{"256GB", "512GB", "1TB", "2TB"}, new String[]{"wifi", "esim wifi"});
        }
    }

    private void seedStorageProducts(
            Map<String, Model> models,
            String modelName,
            String[] capacities,
            String[] connections
    ) {
        String baseModelName = modelName.replace(" 13", "").replace(" 12.9", "");
        Model model = models.get(baseModelName);
        for (String capacity : capacities) {
            for (String connection : connections) {
                createProduct(
                        model,
                        modelName + " " + capacity + " " + connection,
                        imageForModel(baseModelName)
                );
            }
        }
    }

    private void createProduct(Model model, String name, String imageUrl) {
        Product existingProduct = productRepository.findAll().stream()
                .filter(existing ->
                name.equalsIgnoreCase(existing.getName())
                        && existing.getModels() != null
                        && model.getId().equals(existing.getModels().getId()))
                .findFirst()
                .orElse(null);
        if (existingProduct != null) {
            if ((existingProduct.getImageUrl() == null || existingProduct.getImageUrl().isBlank())
                    && imageUrl != null && !imageUrl.isBlank()) {
                existingProduct.setImageUrl(imageUrl);
                productRepository.save(existingProduct);
            }
            return;
        }

        Product product = new Product();
        product.setCode(toCode(name));
        product.setName(name);
        product.setModels(model);
        product.setCostPrice(BigDecimal.ZERO);
        product.setSalePrice(BigDecimal.ZERO);
        product.setReorderLevel(0);
        product.setHasSerial(true);
        product.setImageUrl(imageUrl);
        product.setStatus(Constants.STATUS_ACTIVE);
        productRepository.save(product);
    }

    private void seedProductImages() {
        productRepository.findAll().forEach(product -> {
            if (product.getModels() == null) {
                return;
            }

            String imageUrl = imageForModel(product.getModels().getName());
            if (!imageUrl.isBlank() && !imageUrl.equals(product.getImageUrl())) {
                product.setImageUrl(imageUrl);
                productRepository.save(product);
            }
        });
    }

    private String imageForModel(String modelName) {
        return switch (modelName.toLowerCase().replaceAll("[^a-z0-9]", "")) {
            case "ipadmini5" -> "Ipad mini 5.jpg";
            case "ipadmini6" -> "Ipad mini 6.jpg";
            case "ipadmini7" -> "Ipad mini 7.jpg";
            case "ipad6th" -> "Ipad6th.jpg";
            case "ipad7th" -> "Ipad7th.jpg";
            case "ipad8th" -> "Ipad8th.jpg";
            case "ipad9th" -> "Ipad9th.jpg";
            case "ipad10th" -> "Ipad10th.jpg";
            case "ipad11th" -> "Ipad11th.jpg";
            case "ipadair3" -> "Ipad Air3.jpg";
            case "ipadair4" -> "Ipad Air4.jpg";
            case "ipadair5" -> "Ipad Air5.jpg";
            case "ipadairm2" -> "Ipad Air M2.jpg";
            case "ipadairm3" -> "Ipad Air M3.jpg";
            case "ipadairm4" -> "Ipad Air M4.jpg";
            case "ipadprom4" -> "Ipad M4.jpg";
            case "ipadprom5" -> "Ipad M5.jpg";
            case "ipadpro18" -> "Ipad Pro18.jpg";
            case "ipadpro20" -> "Ipad Pro20.jpg";
            case "ipadpro21" -> "Ipad Pro21.jpg";
            case "ipadpro22" -> "Ipad Pro22.jpg";
            default -> "";
        };
    }


    public  Currency getCurrencyByCode(String code) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM tbl_currency WHERE code = ?",
                (rs, rowNum) -> {
                    Currency currency = new Currency();
                    currency.setId(rs.getLong("id"));
                    currency.setCode(rs.getString("code"));
                    currency.setName(rs.getString("name"));
                    currency.setSymbol(rs.getString("symbol"));
                    currency.setStatus(rs.getString("status"));
                    return currency;
                },
                code
        );
    }

    

    private Category getOrCreateCategory(String name, String code) {
        return categoryRepository.findAll().stream()
                .filter(category -> code.equalsIgnoreCase(category.getCode()))
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setCode(code);
                    category.setStatus(Constants.STATUS_ACTIVE);
                    return categoryRepository.save(category);
                });
    }

    private Brand getOrCreateBrand(String name) {
        return brandRepository.findAll().stream()
                .filter(brand -> name.equalsIgnoreCase(brand.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Brand brand = new Brand();
                    brand.setName(name);
                    brand.setStatus(Constants.STATUS_ACTIVE);
                    return brandRepository.save(brand);
                });
    }

    private Model getOrCreateModel(String name, Brand brand, Category category) {
        return modelRepository.findAll().stream()
                .filter(model -> name.equalsIgnoreCase(model.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Model model = new Model();
                    model.setName(name);
                    model.setBrand(brand);
                    model.setCategory(category);
                    model.setStatus(Constants.STATUS_ACTIVE);
                    return modelRepository.save(model);
                });
    }

    private String toCode(String name) {
        return name.toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private void ensureSeedHistoryTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS tbl_database_seeder_history (
                    id SERIAL PRIMARY KEY,
                    seed_key VARCHAR(100) UNIQUE NOT NULL,
                    executed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
                )
                """);
    }

    private boolean isSeeded() {
        return isSeeded(SEED_KEY);
    }

    private boolean isSeeded(String seedKey) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tbl_database_seeder_history WHERE seed_key = ?",
                Integer.class,
                seedKey
        );
        return count != null && count > 0;
    }

    private void markSeeded() {
        markSeeded(SEED_KEY);
    }

    private void markSeeded(String seedKey) {
        jdbcTemplate.update("""
                INSERT INTO tbl_database_seeder_history (seed_key, executed_at)
                VALUES (?, NOW())
                ON CONFLICT (seed_key) DO NOTHING
                """, seedKey);
    }

    private static final String[] MODEL_NAMES = {
            "Ipad Mini5", "Ipad Mini6", "Ipad Mini7",
            "Ipad6th", "Ipad7th", "Ipad8th", "Ipad9th", "Ipad10th", "Ipad11th",
            "Ipad Air3", "Ipad Air4", "Ipad Air5", "Ipad AirM2", "Ipad AirM3", "Ipad AirM4",
            "Ipad Pro18", "Ipad Pro20", "Ipad Pro21", "Ipad Pro22", "Ipad ProM4", "Ipad ProM5"
    };
}
