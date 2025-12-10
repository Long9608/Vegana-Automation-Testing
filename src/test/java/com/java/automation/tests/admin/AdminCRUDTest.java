package com.java.automation.tests.admin;

import com.java.automation.base.BaseTest;
import com.java.automation.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for Admin CRUD operations
 */
public class AdminCRUDTest extends BaseTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123123";

    private void loginAsAdmin() {
        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    // ==================== PRODUCTS CRUD ====================

    @Test(priority = 1, description = "Test xem danh sách sản phẩm trong admin")
    public void testViewProductsList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách sản phẩm admin");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        Assert.assertTrue(productsPage.isOnProductsPage(), 
            "Không ở trang Products Management");
        Assert.assertTrue(productsPage.isProductsTableDisplayed(), 
            "Bảng sản phẩm không hiển thị");
        
        int productCount = productsPage.getProductCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng sản phẩm: " + productCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách sản phẩm thành công");
    }

    @Test(priority = 2, description = "Test nút Add Product có hiển thị")
    public void testAddProductButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Product");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        Assert.assertTrue(productsPage.isAddProductButtonDisplayed(), 
            "Nút Add Product không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Product hiển thị đúng");
    }

    // ==================== ORDERS CRUD ====================

    @Test(priority = 3, description = "Test xem danh sách đơn hàng")
    public void testViewOrdersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách đơn hàng");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        Assert.assertTrue(ordersPage.isOnOrdersPage(), 
            "Không ở trang Orders Management");
        Assert.assertTrue(ordersPage.isOrdersTableDisplayed(), 
            "Bảng đơn hàng không hiển thị");
        
        int orderCount = ordersPage.getOrderCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng đơn hàng: " + orderCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách đơn hàng thành công");
    }

    @Test(priority = 4, description = "Test link Export To Excel có hiển thị")
    public void testExportToExcelLinkDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra link Export To Excel");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        Assert.assertTrue(ordersPage.isExportToExcelLinkDisplayed(), 
            "Link Export To Excel không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Link Export To Excel hiển thị đúng");
    }

    // ==================== CUSTOMERS CRUD ====================

    @Test(priority = 5, description = "Test xem danh sách khách hàng")
    public void testViewCustomersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách khách hàng");

        loginAsAdmin();
        
        CustomersPage customersPage = new CustomersPage(driver);
        customersPage.navigateToCustomersPage();
        
        Assert.assertTrue(customersPage.isOnCustomersPage(), 
            "Không ở trang Customers Management");
        Assert.assertTrue(customersPage.isCustomersTableDisplayed(), 
            "Bảng khách hàng không hiển thị");
        
        int customerCount = customersPage.getCustomerCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng khách hàng: " + customerCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách khách hàng thành công");
    }

    // ==================== CATEGORIES CRUD ====================

    @Test(priority = 6, description = "Test xem danh sách danh mục")
    public void testViewCategoriesList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách danh mục");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        Assert.assertTrue(categoriesPage.isOnCategoriesPage(), 
            "Không ở trang Categories Management");
        Assert.assertTrue(categoriesPage.isCategoriesTableDisplayed(), 
            "Bảng danh mục không hiển thị");
        
        int categoryCount = categoriesPage.getCategoryCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng danh mục: " + categoryCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách danh mục thành công");
    }

    @Test(priority = 7, description = "Test nút Add Category có hiển thị")
    public void testAddCategoryButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Category");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        Assert.assertTrue(categoriesPage.isAddCategoryButtonDisplayed(), 
            "Nút Add Category không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Category hiển thị đúng");
    }

    // ==================== SUPPLIERS CRUD ====================

    @Test(priority = 8, description = "Test xem danh sách nhà cung cấp")
    public void testViewSuppliersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách nhà cung cấp");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        Assert.assertTrue(suppliersPage.isOnSuppliersPage(), 
            "Không ở trang Suppliers Management");
        Assert.assertTrue(suppliersPage.isSuppliersTableDisplayed(), 
            "Bảng nhà cung cấp không hiển thị");
        
        int supplierCount = suppliersPage.getSupplierCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng nhà cung cấp: " + supplierCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách nhà cung cấp thành công");
    }

    @Test(priority = 9, description = "Test nút Add Supplier có hiển thị")
    public void testAddSupplierButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Supplier");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        Assert.assertTrue(suppliersPage.isAddSupplierButtonDisplayed(), 
            "Nút Add Supplier không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Supplier hiển thị đúng");
    }
}

