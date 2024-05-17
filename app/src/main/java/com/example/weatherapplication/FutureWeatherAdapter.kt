import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.FutureWeatherData
import com.example.weatherapplication.R

class FutureWeatherAdapter(private var weatherList: List<FutureWeatherData>) : RecyclerView.Adapter<FutureWeatherAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.tvDate)
        val temperature: TextView = view.findViewById(R.id.tvTemperature)
        val windSpeed: TextView = view.findViewById(R.id.tvWindSpeed)
        val humidity: TextView = view.findViewById(R.id.tvHumidity)
        val description: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_future_weather_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.date.text = weather.date
        holder.temperature.text = "${weather.temperature} ºC"
        holder.windSpeed.text = "${weather.windSpeed} km/h"
        holder.humidity.text = "${weather.humidity} %"
        holder.description.text = weather.description
    }

    override fun getItemCount() = weatherList.size

    // Method to update the data in the adapter
    fun updateData(newWeatherList: List<FutureWeatherData>) {
        weatherList = newWeatherList
        notifyDataSetChanged()
    }
}
