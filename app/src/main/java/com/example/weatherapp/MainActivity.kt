package com.example.weatherapp

import android.app.DownloadManager.Query
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.show()
      fetchWeatherdata("Delhi")
        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnClickListener {
            searchView.isIconified = false // Expands the search view
            searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    fetchWeatherdata(query!!)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
    }


    private fun fetchWeatherdata(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiInterface::class.java)
        val response= retrofit.getWeatherData(cityName,"818c026733eae9f5e812425af24e198e","metric")
     response.enqueue(object:Callback<weatherapi>{
         override fun onResponse(p0: Call<weatherapi>, response: Response<weatherapi>) {
            val responseBody= response.body()
             if(response.isSuccessful && responseBody!==null){
                 val temperature= responseBody.main.temp.toString()
                 val humidity=responseBody.main.humidity
                 val windspeed=responseBody.wind.speed
                 val sunRise=responseBody.sys.sunrise.toLong()
                 val sunSet=responseBody.sys.sunset.toLong()
                 val sea= responseBody.main.pressure
                 val condition=responseBody.weather.firstOrNull()?.main?: "unknown"
                 val maxTemp=responseBody.main.temp_max
                 val minTemp=responseBody.main.temp_min

               //  Log.d("tag",temperature.toString())
                 binding.temprature.text= "$temperature°C"
                 binding.humidity.text=humidity.toString()
                 binding.windSpeed.text="$windspeed m/s"
                 binding.sunRise.text= sunRise.toString()
                 binding.sunSet.text=sunSet.toString()
                 binding.condition.text=condition
                 binding.seaLevel.text=sea.toString()
                 binding.tempratures.text= "$temperature °C"
                 binding.location.text=cityName
                 binding.days.text=dayName(System.currentTimeMillis())
                     binding.date.text=time()
                 changeImageAcordingToWeatherCondition(condition)

             }
         }
         fun changeImageAcordingToWeatherCondition(conditions:String){
            when(conditions){
                "Clear Sky","Sunny","Clear"->{
                    binding.root.setBackgroundResource(R.drawable.sunnybackground)
                    binding.imageView2.setImageResource(R.drawable.sunny)
                }
                "haze","Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                    binding.root.setBackgroundResource(R.drawable.cloud)
                    binding.imageView2.setImageResource(R.drawable.smallsnow)

                }
                "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                    binding.root.setBackgroundResource(R.drawable.rain)
                    binding.imageView2.setImageResource(R.drawable.smallrain)}

                "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                    binding.root.setBackgroundResource(R.drawable.snow)
                    binding.imageView2.setImageResource(R.drawable.cloud_with_fog)

                }

            }
         }
         fun dayName(timestamp:Long):String{
             val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
             return sdf.format((Date()))
         }
         fun time():String{
             val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
             return sdf.format((Date()))
         }

         override fun onFailure(p0: Call<weatherapi>, p1: Throwable) {
             TODO("Not yet implemented")
         }

     })

    }

}