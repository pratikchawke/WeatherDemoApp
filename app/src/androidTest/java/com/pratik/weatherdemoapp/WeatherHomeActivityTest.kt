package com.pratik.weatherdemoapp

import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import com.pratik.weatherdemoapp.retrofit.RetrofitModule
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WeatherHomeActivityTest {
    private var device: UiDevice? = null

    @get:Rule
    var weatherHomeActivityTestRule = ActivityTestRule(WeatherHomeActivity::class.java)
    var weatherHomeActivity: WeatherHomeActivity? = null

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(WeatherHomeActivity::class.java)


    @Before
    fun setUp() {
        this.weatherHomeActivity = weatherHomeActivityTestRule.activity
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testLaunch() {
        val view = weatherHomeActivity?.findViewById<TextView>(R.id.temperatureTextView)
        assertNotNull(view)
    }

    @Test
    fun testFeedbackLocationPermissionDenied() {
        val denyButton = this.device?.findObject(UiSelector().text("DENY"))
        val permissionDeniedMessage =
            this.device?.findObject(UiSelector().text("Permission denied"))
        denyButton!!.click()
        assert(permissionDeniedMessage!!.exists())
    }

    @Test
    fun testFeedbackLocationPermissionAllowed() {
        val allowButton = this.device?.findObject(UiSelector().text("ALLOW"))
        var permissionAllowedMessage =
            this.device?.findObject(UiSelector().text("Permission allowed"))
        allowButton!!.click()
        assert(permissionAllowedMessage!!.exists())
    }

    @Test
    fun test_getWeatherReport() {
        val module =  RetrofitModule()
        val apiRequest: ApiRequest =
            module.createApiRequest(module.getRetrofit())
        val call: Call<WeatherReport> = apiRequest.getWeatherReport(
            AppConstants.API_KEY,
            AppConstants.LAT,
            AppConstants.LONG,
            AppConstants.UNITS
        )
        try {
            val response: Response<WeatherReport> = call.execute()
            val authResponse: WeatherReport? = response.body()
            assertTrue(response.isSuccessful() && authResponse.toString().length > 1) // Applied condition if length of json response length is greater than 1
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm revoke ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} android.permission.ACCESS_FINE_LOCATION")
        weatherHomeActivity = null
    }


}