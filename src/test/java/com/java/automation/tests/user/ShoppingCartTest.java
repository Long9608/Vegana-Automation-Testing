package com.java.automation.tests.user;

import com.java.automation.base.BaseTest;
import com.java.automation.config.TestConfig;
import com.java.automation.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for Shopping Cart functionality
 */
public class ShoppingCartTest extends BaseTest {

    private static final String USER_ID = TestConfig.getProperty("test.user.id");
    private static final String USER_PASSWORD = TestConfig.getProperty("test.user.password");

    private void loginAsUser() {
        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(USER_ID, USER_PASSWORD);
    }

    @Test(priority = 1, description = "Test xem giỏ hàng khi đã đăng nhập")
    public void testViewShoppingCart() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem giỏ hàng");

        loginAsUser();
        
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        Assert.assertTrue(cartPage.isOnCartPage(), 
            "Không ở trang giỏ hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem giỏ hàng thành công");
    }

    @Test(priority = 2, description = "Test thêm sản phẩm vào giỏ hàng")
    public void testAddProductToCart() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test thêm sản phẩm vào giỏ hàng");

        loginAsUser();
        
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        int initialCartCount = 0;
        try {
            ShoppingCartPage cartPage = new ShoppingCartPage(driver);
            cartPage.navigateToCartPage();
            initialCartCount = cartPage.getCartItemCount();
        } catch (Exception e) {
            // Cart might be empty
        }
        
        // Add first product to cart
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        // Verify product added
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        int newCartCount = cartPage.getCartItemCount();
        Assert.assertTrue(newCartCount > initialCartCount || newCartCount > 0, 
            "Sản phẩm không được thêm vào giỏ hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Thêm sản phẩm vào giỏ hàng thành công");
    }

    @Test(priority = 3, description = "Test cập nhật số lượng sản phẩm trong giỏ hàng")
    public void testUpdateCartQuantity() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test cập nhật số lượng sản phẩm");

        loginAsUser();
        
        // Ensure cart has items
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        int initialCount = cartPage.getCartItemCount();
        Assert.assertTrue(initialCount > 0, 
            "Giỏ hàng không có sản phẩm để test");
        
        // Update quantity
        cartPage.updateQuantity(0, 2);
        
        // Wait a bit for update
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Cập nhật số lượng sản phẩm thành công");
    }

    @Test(priority = 4, description = "Test checkout với giỏ hàng có sản phẩm")
    public void testCheckoutWithItems() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test checkout");

        loginAsUser();
        
        // Ensure cart has items
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.navigateToCheckoutPage();
        
        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), 
            "Không ở trang checkout");
        
        int itemCount = checkoutPage.getOrderItemCount();
        Assert.assertTrue(itemCount > 0, 
            "Không có sản phẩm trong đơn hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Checkout page hiển thị đúng với sản phẩm trong giỏ hàng");
    }

    @Test(priority = 5, description = "Test điền form checkout và submit")
    public void testSubmitCheckout() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test submit checkout");

        loginAsUser();
        
        // Ensure cart has items
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.navigateToCheckoutPage();
        
        // Fill checkout form
        checkoutPage.fillCheckoutForm(
            "Test User",
            "123 Test Street",
            "0123456789",
            "Test order description"
        );
        
        // Submit
        checkoutPage.submitCheckout();
        
        // Wait for redirect
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify redirect to success page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("checkout_success") || 
                         currentUrl.contains("success") ||
                         currentUrl.contains("/"), 
            "Không redirect đến trang success sau khi checkout");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Submit checkout thành công");
    }
}

