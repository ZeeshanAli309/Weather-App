package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.search.SearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//08b4078f8a38377a90e2e369f8abc9e9
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData("Attock")
        searcCity()
    }

    private fun searcCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!=null){
                    fetchData(query)

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }
        })
    }

    private fun fetchData(city:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(IApi::class.java)
        val response=retrofit.getWeatherData(city,"08b4078f8a38377a90e2e369f8abc9e9","metric")
        response.enqueue(object :Callback<weatherfile>{
            override fun onResponse(call: Call<weatherfile>, response: Response<weatherfile>) {
                val responseBody=response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temprature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val wind=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min

                    binding.temp.text="$temprature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp : $maxTemp °C"
                    binding.minTemp.text="Min Temp : $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$wind m/s"
                    binding.sea.text="$seaLevel hPa"
                    binding.sunrise.text="${getTime(sunRise)}"
                    binding.sunset.text="${getTime(sunset)}"
                    binding.cityName.text="$city"
                    binding.day.text=getDay()
                    binding.date.text=getDate()
                    changeImgAccordingToWeather(condition)

                }
            }

            override fun onFailure(call: Call<weatherfile>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeImgAccordingToWeather(condition: String) {
        when(condition){
            "Clear","Clear Sky","Sunny" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Clouds","Overcast","Mist","Foggy","Partly Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drile","Moderate Rain","Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun getDate(): String {
val sdf=SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getDay(): String {
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun getTime(timeStamp:Long): String {
        val sdf=SimpleDateFormat("HH:MM", Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))
    }
}