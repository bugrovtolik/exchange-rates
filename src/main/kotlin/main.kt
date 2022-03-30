import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.time.LocalDate

data class ExchangeRate(val rate: Double, val date: LocalDate)

fun main() {
//    saveRates()

    val rates = readRates()
    println("MIN: ${rates.minOf { it.rate }}")
    println("MAX: ${rates.maxOf { it.rate }}")

    rates.groupBy({ it.date.month.name }, { it.rate }).mapValues { it.value.average() }.forEach { (month, avgRate) ->
        println("$month: $avgRate")
    }
}

private fun saveRates() {
    val exchangeUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/%s/currencies/usd/uah.json"
    val startDate = LocalDate.of(2021, 1, 1)
    val finishDate = LocalDate.of(2021, 5, 15)
    mutableListOf<ExchangeRate>()

    val rates = startDate.datesUntil(finishDate).toList().map { date ->
        val response = URL(exchangeUrl.format(date)).readText()
        val rate = JSONObject(response).getDouble("uah")

        ExchangeRate(rate, date)
    }

    File("rates.json").writeText(JSONArray(rates).toString())
}

private fun readRates(): List<ExchangeRate> {
    return JSONArray(File("rates.json").readText()).map { it as JSONObject
        ExchangeRate(rate = it.getDouble("rate"), date = LocalDate.parse(it.getString("date")))
    }
}
