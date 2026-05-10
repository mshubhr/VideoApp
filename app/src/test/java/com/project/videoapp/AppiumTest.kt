package com.project.videoapp

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration

class AppiumTest {

    @Test
    fun testVideoApp() {

        val options = UiAutomator2Options()

        options.setPlatformName("Android")
        options.setDeviceName("Android Device")
        options.setAutomationName("UIAutomator2")

        options.setCapability(
            "appium:androidHome",
            "/Users/mshubhr/Library/Android/sdk"
        )

        options.setAppPackage("com.project.videoapp")
        options.setAppActivity("com.project.videoapp.MainActivity")

        val driver = AndroidDriver(
            URL("http://127.0.0.1:4723"),
            options
        )

        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        val searchView = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.id("com.project.videoapp:id/search")
            )
        )

        searchView.click()

        Thread.sleep(1000)

        searchView.sendKeys("Nature")

        val thumbnail = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.id("com.project.videoapp:id/thumbnail")
            )
        )

        thumbnail.click()

        Thread.sleep(5000)

        driver.quit()
    }
}