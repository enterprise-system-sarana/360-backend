package com.saranaresturantsystem.config;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.PermissionGroup;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.repository.users.PermissionGroupRepository;
import com.saranaresturantsystem.repository.users.PermissionRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Value("${app.seeder.enabled:true}")
    private boolean seederEnabled;

    @Value("${app.seeder.force:false}")
    private boolean forceSeed;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seederEnabled) {
            log.info("Database seeder is disabled by configuration (app.seeder.enabled=false). Skipping.");
            return;
        }

        if (!forceSeed && isAlreadySeeded()) {
            log.info("Database has already been seeded. Skipping database seeder (seeds only one time).");
            return;
        }

        log.info("Starting one-time database seeding for permissions, roles, and users...");

        // Ensure tbl_invoice_sequences table exists for atomic sequence generation
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS tbl_invoice_sequences (
                        sequence_key VARCHAR(50) NOT NULL,
                        last_value BIGINT NOT NULL DEFAULT 0,
                        CONSTRAINT pk_invoice_sequences PRIMARY KEY (sequence_key)
                    );
                """);
                log.info("Ensured tbl_invoice_sequences table exists in PostgreSQL.");
            }
        } catch (Exception e) {
            log.warn("Could not ensure tbl_invoice_sequences table via JdbcTemplate: {}", e.getMessage());
        }

        // Ensure tbl_bank_transactions has bank_id, purchase_id, sale_id, and audit columns
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.execute("""
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS bank_id BIGINT;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS purchase_id BIGINT;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS sale_id BIGINT;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE;
                    ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);
                """);
                log.info("Ensured tbl_bank_transactions columns exist in PostgreSQL.");
            }
        } catch (Exception e) {
            log.warn("Could not ensure tbl_bank_transactions columns via JdbcTemplate: {}", e.getMessage());
        }

        // 1. Define standard Permission Groups from all controllers
        // Dashboard
        PermissionGroup groupDashboard = getOrCreateGroup("Dashboard", "Dashboard", "Permissions related to dashboard analytics");

        // User & Security Management
        PermissionGroup groupUser = getOrCreateGroup("Users", "Users", "Permissions related to User management");
        PermissionGroup groupRole = getOrCreateGroup("Role", "Role", "Permissions related to Role operations");
        PermissionGroup groupPermission = getOrCreateGroup("Permission", "Permission", "Permissions related to Permission operations");
        PermissionGroup groupPermissionGroup = getOrCreateGroup("PermissionGroup", "PermissionGroup", "Permissions related to PermissionGroup operations");

        // Catalog & Products
        PermissionGroup groupBrand = getOrCreateGroup("Brand", "Brand", "Permissions related to brand operations");
        PermissionGroup groupCategory = getOrCreateGroup("Category", "Category", "Permissions related to category operations");
        PermissionGroup groupModel = getOrCreateGroup("Model", "Model", "Permissions related to model operations");
        PermissionGroup groupProduct = getOrCreateGroup("Product", "Product", "Permissions related to product operations");
        PermissionGroup groupProductSerial = getOrCreateGroup("ProductSerial", "Product Serial", "Permissions related to product serial tracking");
        PermissionGroup groupVariantType = getOrCreateGroup("VariantType", "Variant Type", "Permissions related to variant type operations");
        PermissionGroup groupVariantValue = getOrCreateGroup("VariantValue", "Variant Value", "Permissions related to variant value operations");

        // Customer & Finances
        PermissionGroup groupCustomer = getOrCreateGroup("Customer", "Customer", "Permissions related to customer operations");
        PermissionGroup groupBank = getOrCreateGroup("Bank", "Bank", "Permissions related to bank operations");
        PermissionGroup groupCurrency = getOrCreateGroup("Currency", "Currency", "Permissions related to currency operations");

        // Inventory
        PermissionGroup groupStore = getOrCreateGroup("Store", "Store", "Permissions related to store operations");
        PermissionGroup groupStock = getOrCreateGroup("Stock", "Stock", "Permissions related to stock operations");
        PermissionGroup groupAdjustment = getOrCreateGroup("Adjustment", "Adjustment", "Permissions related to adjustment operations");
        PermissionGroup groupTransfer = getOrCreateGroup("Transfer", "Transfer", "Permissions related to transfer operations");

        // Purchase & Expenses
        PermissionGroup groupSupplier = getOrCreateGroup("Supplier", "Supplier", "Permissions related to supplier operations");
        PermissionGroup groupPurchase = getOrCreateGroup("Purchase", "Purchase", "Permissions related to purchase operations");
        PermissionGroup groupExpenseType = getOrCreateGroup("ExpenseType", "Expense Type", "Permissions related to expense type operations");
        PermissionGroup groupExpense = getOrCreateGroup("Expense", "Expense", "Permissions related to expense operations");

        // Sales, Quotes & Cash Register
        PermissionGroup groupSale = getOrCreateGroup("Sale", "Sale", "Permissions related to sale operations");
        PermissionGroup groupCashRegister = getOrCreateGroup("CashRegister", "Cash Register", "Permissions related to cash register shifts");
        PermissionGroup groupQuote = getOrCreateGroup("Quote", "Quote", "Permissions related to quote operations");
        PermissionGroup groupPayment = getOrCreateGroup("Payment", "Payment", "Permissions related to payment operations");

        // Reports & Files
        PermissionGroup groupReport = getOrCreateGroup("Report", "Report", "Permissions related to financial, sales, inventory, and expense reports");
        PermissionGroup groupFile = getOrCreateGroup("File", "File", "Permissions related to file management and storage");

        // 2. Create all permissions and collect them
        Set<Permission> allPermissions = new HashSet<>();

        // User & Security Management (UserController & AuthController)
        allPermissions.add(getOrCreatePermission("user:read", "Read Users", "Ability to view users list and details", groupUser));
        allPermissions.add(getOrCreatePermission("user:create", "Create Users", "Ability to create new users", groupUser));
        allPermissions.add(getOrCreatePermission("user:update", "Update Users", "Ability to update existing users", groupUser));
        allPermissions.add(getOrCreatePermission("user:delete", "Delete Users", "Ability to delete users", groupUser));

        // Role Management (RoleController)
        allPermissions.add(getOrCreatePermission("role:read", "Read Roles", "Ability to view roles and role permissions", groupRole));
        allPermissions.add(getOrCreatePermission("role:create", "Create Roles", "Ability to create new roles", groupRole));
        allPermissions.add(getOrCreatePermission("role:update", "Update Roles", "Ability to update existing roles and permissions", groupRole));
        allPermissions.add(getOrCreatePermission("role:delete", "Delete Roles", "Ability to delete roles", groupRole));

        // Permission Management (PermissionController)
        allPermissions.add(getOrCreatePermission("permission:read", "Read Permissions", "Ability to view permissions", groupPermission));
        allPermissions.add(getOrCreatePermission("permission:create", "Create Permissions", "Ability to create new permissions", groupPermission));
        allPermissions.add(getOrCreatePermission("permission:update", "Update Permissions", "Ability to update existing permissions", groupPermission));
        allPermissions.add(getOrCreatePermission("permission:delete", "Delete Permissions", "Ability to delete permissions", groupPermission));

        // Permission Group Management (PermissionGroupController)
        allPermissions.add(getOrCreatePermission("permissionGroup:read", "Read Permission Groups", "Ability to view permission groups", groupPermissionGroup));
        allPermissions.add(getOrCreatePermission("permissionGroup:create", "Create Permission Groups", "Ability to create permission groups", groupPermissionGroup));
        allPermissions.add(getOrCreatePermission("permissionGroup:update", "Update Permission Groups", "Ability to update permission groups", groupPermissionGroup));
        allPermissions.add(getOrCreatePermission("permissionGroup:delete", "Delete Permission Groups", "Ability to delete permission groups", groupPermissionGroup));

        // Catalog - Brand (BrandController)
        allPermissions.add(getOrCreatePermission("brand:read", "Read Brands", "Ability to view brands", groupBrand));
        allPermissions.add(getOrCreatePermission("brand:create", "Create Brands", "Ability to create new brands", groupBrand));
        allPermissions.add(getOrCreatePermission("brand:update", "Update Brands", "Ability to update existing brands", groupBrand));
        allPermissions.add(getOrCreatePermission("brand:delete", "Delete Brands", "Ability to delete brands", groupBrand));

        // Catalog - Category (CategoryController)
        allPermissions.add(getOrCreatePermission("category:read", "Read Categories", "Ability to view categories", groupCategory));
        allPermissions.add(getOrCreatePermission("category:create", "Create Categories", "Ability to create new categories", groupCategory));
        allPermissions.add(getOrCreatePermission("category:update", "Update Categories", "Ability to update existing categories", groupCategory));
        allPermissions.add(getOrCreatePermission("category:delete", "Delete Categories", "Ability to delete categories", groupCategory));

        // Catalog - Model (ModelController)
        allPermissions.add(getOrCreatePermission("model:read", "Read Models", "Ability to view models", groupModel));
        allPermissions.add(getOrCreatePermission("model:create", "Create Models", "Ability to create new models", groupModel));
        allPermissions.add(getOrCreatePermission("model:update", "Update Models", "Ability to update existing models", groupModel));
        allPermissions.add(getOrCreatePermission("model:delete", "Delete Models", "Ability to delete models", groupModel));

        // Catalog - Product (ProductController)
        allPermissions.add(getOrCreatePermission("product:read", "Read Products", "Ability to view products and product details", groupProduct));
        allPermissions.add(getOrCreatePermission("product:create", "Create Products", "Ability to create new products", groupProduct));
        allPermissions.add(getOrCreatePermission("product:update", "Update Products", "Ability to update existing products", groupProduct));
        allPermissions.add(getOrCreatePermission("product:delete", "Delete Products", "Ability to delete products", groupProduct));

        // Catalog - Product Serial (ProductSerialController)
        allPermissions.add(getOrCreatePermission("productSerial:read", "Read Product Serials", "Ability to view product serial numbers", groupProductSerial));
        allPermissions.add(getOrCreatePermission("productSerial:create", "Create Product Serials", "Ability to create product serial numbers", groupProductSerial));
        allPermissions.add(getOrCreatePermission("productSerial:update", "Update Product Serials", "Ability to update product serial numbers", groupProductSerial));
        allPermissions.add(getOrCreatePermission("productSerial:delete", "Delete Product Serials", "Ability to delete product serial numbers", groupProductSerial));

        // Catalog - Variant Type (VariantTypeController)
        allPermissions.add(getOrCreatePermission("variantType:read", "Read Variant Types", "Ability to view variant types", groupVariantType));
        allPermissions.add(getOrCreatePermission("variantType:create", "Create Variant Types", "Ability to create variant types", groupVariantType));
        allPermissions.add(getOrCreatePermission("variantType:update", "Update Variant Types", "Ability to update variant types", groupVariantType));
        allPermissions.add(getOrCreatePermission("variantType:delete", "Delete Variant Types", "Ability to delete variant types", groupVariantType));

        // Catalog - Variant Value (VariantValueController)
        allPermissions.add(getOrCreatePermission("variantValue:read", "Read Variant Values", "Ability to view variant values", groupVariantValue));
        allPermissions.add(getOrCreatePermission("variantValue:create", "Create Variant Values", "Ability to create variant values", groupVariantValue));
        allPermissions.add(getOrCreatePermission("variantValue:update", "Update Variant Values", "Ability to update variant values", groupVariantValue));
        allPermissions.add(getOrCreatePermission("variantValue:delete", "Delete Variant Values", "Ability to delete variant values", groupVariantValue));

        // Customer & Finances - Customer (CustomerController)
        allPermissions.add(getOrCreatePermission("customer:read", "Read Customers", "Ability to view customer profiles", groupCustomer));
        allPermissions.add(getOrCreatePermission("customer:create", "Create Customers", "Ability to create new customer profiles", groupCustomer));
        allPermissions.add(getOrCreatePermission("customer:update", "Update Customers", "Ability to update customer profiles", groupCustomer));
        allPermissions.add(getOrCreatePermission("customer:delete", "Delete Customers", "Ability to delete customer profiles", groupCustomer));

        // Customer & Finances - Bank (BankController)
        allPermissions.add(getOrCreatePermission("bank:read", "Read Banks", "Ability to view banks list and details", groupBank));
        allPermissions.add(getOrCreatePermission("bank:create", "Create Banks", "Ability to create new banks", groupBank));
        allPermissions.add(getOrCreatePermission("bank:update", "Update Banks", "Ability to update existing banks", groupBank));
        allPermissions.add(getOrCreatePermission("bank:delete", "Delete Banks", "Ability to delete banks", groupBank));

        // Customer & Finances - Currency (CurrencyController)
        allPermissions.add(getOrCreatePermission("currency:read", "Read Currencies", "Ability to view currencies list and details", groupCurrency));
        allPermissions.add(getOrCreatePermission("currency:create", "Create Currencies", "Ability to create new currencies", groupCurrency));
        allPermissions.add(getOrCreatePermission("currency:update", "Update Currencies", "Ability to update existing currencies", groupCurrency));
        allPermissions.add(getOrCreatePermission("currency:delete", "Delete Currencies", "Ability to delete currencies", groupCurrency));

        // Inventory - Store (StoreController)
        allPermissions.add(getOrCreatePermission("store:read", "Read Stores", "Ability to view stores", groupStore));
        allPermissions.add(getOrCreatePermission("store:create", "Create Stores", "Ability to create stores", groupStore));
        allPermissions.add(getOrCreatePermission("store:update", "Update Stores", "Ability to update stores", groupStore));
        allPermissions.add(getOrCreatePermission("store:delete", "Delete Stores", "Ability to delete stores", groupStore));

        // Inventory - Stock (StockController & ProductSerialController)
        allPermissions.add(getOrCreatePermission("stock:read", "Read Stocks", "Ability to view stock levels", groupStock));
        allPermissions.add(getOrCreatePermission("stock:create", "Create Stocks", "Ability to record stock levels", groupStock));
        allPermissions.add(getOrCreatePermission("stock:update", "Update Stocks", "Ability to update stock levels", groupStock));
        allPermissions.add(getOrCreatePermission("stock:delete", "Delete Stocks", "Ability to delete stock records", groupStock));

        // Inventory - Adjustment & Transfer
        allPermissions.add(getOrCreatePermission("adjustment:read", "Read Adjustments", "Ability to view stock adjustments", groupAdjustment));
        allPermissions.add(getOrCreatePermission("adjustment:create", "Create Adjustments", "Ability to create stock adjustments", groupAdjustment));
        allPermissions.add(getOrCreatePermission("adjustment:update", "Update Adjustments", "Ability to update stock adjustments", groupAdjustment));
        allPermissions.add(getOrCreatePermission("adjustment:delete", "Delete Adjustments", "Ability to delete stock adjustments", groupAdjustment));

        allPermissions.add(getOrCreatePermission("transfer:read", "Read Transfers", "Ability to view store transfers", groupTransfer));
        allPermissions.add(getOrCreatePermission("transfer:create", "Create Transfers", "Ability to create store transfers", groupTransfer));
        allPermissions.add(getOrCreatePermission("transfer:update", "Update Transfers", "Ability to update store transfers", groupTransfer));
        allPermissions.add(getOrCreatePermission("transfer:delete", "Delete Transfers", "Ability to delete store transfers", groupTransfer));

        // Purchase & Expenses - Expense Type (ExpenseTypeController)
        allPermissions.add(getOrCreatePermission("expensesType:read", "Read Expenses Types", "Ability to view expenses types", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expensesType:create", "Create Expenses Types", "Ability to create new expenses types", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expensesType:update", "Update Expenses Types", "Ability to update existing expenses types", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expensesType:delete", "Delete Expenses Types", "Ability to delete expenses types", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expenseType:read", "Read Expense Types", "Ability to view expense types (alias)", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expenseType:create", "Create Expense Types", "Ability to create expense types (alias)", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expenseType:update", "Update Expense Types", "Ability to update expense types (alias)", groupExpenseType));
        allPermissions.add(getOrCreatePermission("expenseType:delete", "Delete Expense Types", "Ability to delete expense types (alias)", groupExpenseType));

        // Purchase & Expenses - Expense (ExpenseController)
        allPermissions.add(getOrCreatePermission("expense:read", "Read Expenses", "Ability to view expenses", groupExpense));
        allPermissions.add(getOrCreatePermission("expense:create", "Create Expenses", "Ability to record expenses", groupExpense));
        allPermissions.add(getOrCreatePermission("expense:update", "Update Expenses", "Ability to update expenses", groupExpense));
        allPermissions.add(getOrCreatePermission("expense:delete", "Delete Expenses", "Ability to delete expenses", groupExpense));

        // Purchase & Expenses - Supplier (SupplierController)
        allPermissions.add(getOrCreatePermission("supplier:read", "Read Suppliers", "Ability to view suppliers", groupSupplier));
        allPermissions.add(getOrCreatePermission("supplier:create", "Create Suppliers", "Ability to create new suppliers", groupSupplier));
        allPermissions.add(getOrCreatePermission("supplier:update", "Update Suppliers", "Ability to update existing suppliers", groupSupplier));
        allPermissions.add(getOrCreatePermission("supplier:delete", "Delete Suppliers", "Ability to delete suppliers", groupSupplier));

        // Purchase & Expenses - Purchase (PurchaseController)
        allPermissions.add(getOrCreatePermission("purchase:read", "Read Purchase", "Ability to view purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:create", "Create Purchase", "Ability to create purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:update", "Update Purchase", "Ability to update purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:delete", "Delete Purchase", "Ability to delete purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:approve", "Approve Purchase", "Ability to approve purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:completed", "Complete Purchase", "Ability to complete purchase orders", groupPurchase));
        allPermissions.add(getOrCreatePermission("purchase:cancel", "Cancel Purchase", "Ability to cancel purchase orders", groupPurchase));

        // Sales & Quotes - Quote (QuoteController)
        allPermissions.add(getOrCreatePermission("quote:read", "Read Quotes", "Ability to view quotes", groupQuote));
        allPermissions.add(getOrCreatePermission("quote:create", "Create Quotes", "Ability to create quotes", groupQuote));
        allPermissions.add(getOrCreatePermission("quote:update", "Update Quotes", "Ability to update quotes", groupQuote));
        allPermissions.add(getOrCreatePermission("quote:delete", "Delete Quotes", "Ability to delete quotes", groupQuote));

        // Sales & Quotes - Sale (SaleController)
        allPermissions.add(getOrCreatePermission("sale:read", "Read Sales", "Ability to view sales transactions", groupSale));
        allPermissions.add(getOrCreatePermission("sale:create", "Create Sales", "Ability to create sales transactions", groupSale));
        allPermissions.add(getOrCreatePermission("sale:update", "Update Sales", "Ability to update sales transactions", groupSale));
        allPermissions.add(getOrCreatePermission("sale:delete", "Delete Sales", "Ability to delete sales transactions", groupSale));

        // Sales & Quotes - Cash Register (CashRegisterController)
        allPermissions.add(getOrCreatePermission("cashRegister:read", "Read Cash Registers", "Ability to view cash register shifts", groupCashRegister));
        allPermissions.add(getOrCreatePermission("cashRegister:create", "Open Cash Register", "Ability to open cash register shifts", groupCashRegister));
        allPermissions.add(getOrCreatePermission("cashRegister:update", "Close Cash Register", "Ability to close cash register shifts", groupCashRegister));
        allPermissions.add(getOrCreatePermission("cashRegister:delete", "Delete Cash Register", "Ability to delete cash register shifts", groupCashRegister));
        allPermissions.add(getOrCreatePermission("cashRegister:open", "Open Cash Register Action", "Ability to open cash register", groupCashRegister));
        allPermissions.add(getOrCreatePermission("cashRegister:close", "Close Cash Register Action", "Ability to close cash register", groupCashRegister));

        // Sales & Quotes - Payment (PaymentController)
        allPermissions.add(getOrCreatePermission("payment:read", "Read Payments", "Ability to view payments", groupPayment));
        allPermissions.add(getOrCreatePermission("payment:create", "Create Payments", "Ability to create payments", groupPayment));
        allPermissions.add(getOrCreatePermission("payment:update", "Update Payments", "Ability to update payments", groupPayment));
        allPermissions.add(getOrCreatePermission("payment:delete", "Delete Payments", "Ability to delete payments", groupPayment));

        // Reports (Report Controllers: Sale, Purchase, Expense, Payment, ProductSerial, ProfitLoss)
        allPermissions.add(getOrCreatePermission("report:read", "Read Reports", "Ability to view financial, sales, inventory, and expense reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:sales", "Read Sales Reports", "Ability to view sales and sales items reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:purchases", "Read Purchase Reports", "Ability to view purchase and purchase items reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:expenses", "Read Expense Reports", "Ability to view expense reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:payments", "Read Payment Reports", "Ability to view payment reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:serials", "Read Serial Reports", "Ability to view product serial tracking reports", groupReport));
        allPermissions.add(getOrCreatePermission("report:profitLoss", "Read Profit & Loss Report", "Ability to view profit and loss reports", groupReport));

        // Storage & Files (S3FileController)
        allPermissions.add(getOrCreatePermission("file:read", "Read Files", "Ability to view and download files", groupFile));
        allPermissions.add(getOrCreatePermission("file:upload", "Upload Files", "Ability to upload files to storage", groupFile));
        allPermissions.add(getOrCreatePermission("file:delete", "Delete Files", "Ability to delete files from storage", groupFile));

        // Dashboard
        allPermissions.add(getOrCreatePermission("dashboard:read", "Read Dashboard", "Ability to view dashboard analytics and KPI metrics", groupDashboard));

        // 3. Create or Update Roles
        Role superAdminRole = roleRepository.findByCode("ROLE_SUPER_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setCode("ROLE_SUPER_ADMIN");
            role.setName("Super Administrator");
            role.setDescription("Super Administrator role with all permissions");
            role.setStatus(Constants.STATUS_ACTIVE);
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByCode("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setCode("ROLE_ADMIN");
            role.setName("Admin");
            role.setDescription("Admin role with operational and management permissions");
            role.setStatus(Constants.STATUS_ACTIVE);
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByCode("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setCode("ROLE_USER");
            role.setName("User");
            role.setDescription("Standard user role with basic operational access");
            role.setStatus(Constants.STATUS_ACTIVE);
            return roleRepository.save(role);
        });

        // 4. Assign / Update Permissions to Roles
        // SUPER_ADMIN gets 100% FULL permissions across all entities
        List<Permission> allDbPermissions = permissionRepository.findAll();
        superAdminRole.setPermissions(new HashSet<>(allDbPermissions));
        roleRepository.save(superAdminRole);

        // ADMIN gets all operational and report permissions (excluding user/role/permission alteration)
        Set<Permission> adminPermissions = allPermissions.stream()
                .filter(p -> !p.getCode().startsWith("role:") && !p.getCode().startsWith("permission:") && !p.getCode().startsWith("permissionGroup:"))
                .collect(Collectors.toSet());
        adminRole.setPermissions(adminPermissions);
        roleRepository.save(adminRole);

        // USER gets read permissions + common operational actions
        Set<Permission> userPermissions = allPermissions.stream()
                .filter(p -> p.getCode().endsWith(":read")
                        || p.getCode().startsWith("report:")
                        || p.getCode().equals("dashboard:read")
                        || p.getCode().equals("sale:create")
                        || p.getCode().equals("sale:update")
                        || p.getCode().equals("quote:create")
                        || p.getCode().equals("payment:create")
                        || p.getCode().equals("customer:create")
                        || p.getCode().equals("cashRegister:create")
                        || p.getCode().equals("cashRegister:update")
                        || p.getCode().equals("cashRegister:open")
                        || p.getCode().equals("cashRegister:close")
                        || p.getCode().equals("file:upload"))
                .collect(Collectors.toSet());
        userRole.setPermissions(userPermissions);
        roleRepository.save(userRole);

        // Ensure all super admin users in the database have their role updated with full permissions
        userRepository.findAll().forEach(u -> {
            if (u.getRoles() != null && u.getRoles().stream().anyMatch(r -> "ROLE_SUPER_ADMIN".equals(r.getCode()))) {
                u.getRoles().removeIf(r -> "ROLE_SUPER_ADMIN".equals(r.getCode()));
                u.getRoles().add(superAdminRole);
                userRepository.save(u);
            }
        });

        log.info("Synchronized full {} permissions to SUPER_ADMIN, {} to ADMIN, {} to USER",
                allDbPermissions.size(), adminPermissions.size(), userPermissions.size());

        // 5. Seed or Update Default Users
        ensureDefaultUser("You", "Nam", "namyou854@gmail.com", "012345678", superAdminRole);
        ensureDefaultUser("Sovan", "SreyNeat", "admin@gmail.com", "012345680", adminRole);
        ensureDefaultUser("Khea", "Vanna", "user@gmail.com", "012345679", userRole);

        markSeedCompleted();
        log.info("Database seeding completed successfully and marked as finished.");
    }

    private boolean isAlreadySeeded() {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS tbl_database_seeder_history (
                        id SERIAL PRIMARY KEY,
                        seed_key VARCHAR(100) UNIQUE NOT NULL,
                        executed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
                    );
                """);
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tbl_database_seeder_history WHERE seed_key = 'INITIAL_SEED'",
                        Integer.class
                );
                if (count != null && count > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("Could not check tbl_database_seeder_history via JdbcTemplate: {}", e.getMessage());
        }

        // Fallback / legacy check: If the default roles, permissions, and users are already present
        boolean superAdminRoleExists = roleRepository.findByCode("ROLE_SUPER_ADMIN").isPresent();
        boolean hasPermissions = permissionRepository.count() > 0;
        boolean hasUsers = userRepository.count() > 0;

        if (superAdminRoleExists && hasPermissions && hasUsers) {
            log.info("Default seed roles and users already exist in the database. Recording seed marker.");
            markSeedCompleted();
            return true;
        }

        return false;
    }

    private void markSeedCompleted() {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS tbl_database_seeder_history (
                        id SERIAL PRIMARY KEY,
                        seed_key VARCHAR(100) UNIQUE NOT NULL,
                        executed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
                    );
                    INSERT INTO tbl_database_seeder_history (seed_key, executed_at)
                    VALUES ('INITIAL_SEED', NOW())
                    ON CONFLICT (seed_key) DO NOTHING;
                """);
            }
        } catch (Exception e) {
            log.warn("Could not record seed completion in tbl_database_seeder_history: {}", e.getMessage());
        }
    }

    private void ensureDefaultUser(String firstName, String lastName, String email, String phone, Role role) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    if (user.getRoles() == null) {
                        user.setRoles(new HashSet<>());
                    }
                    if (user.getRoles().stream().noneMatch(r -> r.getCode().equals(role.getCode()))) {
                        user.getRoles().add(role);
                        userRepository.save(user);
                        log.info("User '{}' assigned role '{}'", email, role.getCode());
                    }
                },
                () -> {
                    createDefaultUser(firstName, lastName, email, phone, role);
                }
        );
    }

    private void createDefaultUser(String firstName, String lastName, String email, String phone, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(firstName.toLowerCase() + lastName.toLowerCase());
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode("admin@123"));
        user.setIsActive(Constants.STATUS_ACTIVE);
        user.setIsVerified(true);
        user.setIsLocked(false);
        user.setFailedLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
        log.info("User '{}' created and seeded with role '{}'", email, role.getCode());
    }

    private PermissionGroup getOrCreateGroup(String code, String name, String description) {
        return permissionGroupRepository.findByCode(code).map(existing -> {
            boolean updated = false;
            if (existing.getName() == null && name != null) {
                existing.setName(name);
                updated = true;
            }
            if (existing.getDescription() == null && description != null) {
                existing.setDescription(description);
                updated = true;
            }
            return updated ? permissionGroupRepository.save(existing) : existing;
        }).orElseGet(() -> {
            PermissionGroup group = new PermissionGroup();
            group.setCode(code);
            group.setName(name);
            group.setDescription(description);
            return permissionGroupRepository.save(group);
        });
    }

    private Permission getOrCreatePermission(String code, String name, String description, PermissionGroup group) {
        return permissionRepository.findByCode(code).map(existing -> {
            boolean updated = false;
            if (existing.getGroup() == null && group != null) {
                existing.setGroup(group);
                updated = true;
            }
            if (existing.getName() == null && name != null) {
                existing.setName(name);
                updated = true;
            }
            if (existing.getDescription() == null && description != null) {
                existing.setDescription(description);
                updated = true;
            }
            return updated ? permissionRepository.save(existing) : existing;
        }).orElseGet(() -> {
            Permission perm = new Permission();
            perm.setCode(code);
            perm.setName(name);
            perm.setDescription(description);
            perm.setGroup(group);
            return permissionRepository.save(perm);
        });
    }
}