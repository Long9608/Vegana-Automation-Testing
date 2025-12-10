package com.java.automation.tests.user;

import com.java.automation.base.BaseTest;
import com.java.automation.pages.ShopPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for Shop/Products listing functionality
 */
public class ShopTest extends BaseTest {

    @Test(priority = 1, description = "Test xem danh sách sản phẩm")
    public void testViewProductsList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        Assert.assertTrue(shopPage.isOnShopPage(), 
            "Không ở trang danh sách sản phẩm");
        
        int productCount = shopPage.getProductCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng sản phẩm: " + productCount);
        
        Assert.assertTrue(productCount > 0, 
            "Không có sản phẩm nào được hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách sản phẩm thành công");
    }

    @Test(priority = 2, description = "Test tìm kiếm sản phẩm")
    public void testSearchProduct() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test tìm kiếm sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.searchProduct("test");
        
        Assert.assertTrue(shopPage.isOnShopPage(), 
            "Không ở trang kết quả tìm kiếm");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Tìm kiếm sản phẩm thành công");
    }

    @Test(priority = 3, description = "Test xem chi tiết sản phẩm")
    public void testViewProductDetail() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem chi tiết sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        int productCount = shopPage.getProductCount();
        Assert.assertTrue(productCount > 0, 
            "Không có sản phẩm để xem chi tiết");
        
        shopPage.clickFirstProduct();
        
        // Verify navigated to product detail
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("productDetail"), 
            "Không chuyển đến trang chi tiết sản phẩm");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem chi tiết sản phẩm thành công");
    }
}

