package com.truecaller.assignment.views

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truecaller.assignment.models.ApiService
import com.truecaller.assignment.R
import com.truecaller.assignment.helpers.Constants.TRUECALLER_URL
import com.truecaller.assignment.helpers.click
import com.truecaller.assignment.helpers.gone
import com.truecaller.assignment.helpers.show
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.helper.StringUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {


    private var recyclerView: RecyclerView? = null
    private var occurrences = HashMap<String, Int>()
    private var wordsAdapter: WordsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.word_list)
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val okHttpClient = OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(TRUECALLER_URL)
                .client(okHttpClient).build()
        action_btn.click {
            action_btn.text = getString(R.string.making_api_call_text)
            makeApiCall(retrofit)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayTenthChar(responseString: String) {
        val char = responseString.toCharArray()
        tenth_character?.text = getString(R.string.text_tenth_char) +" "+ char[9].toString()
    }

    @SuppressLint("SetTextI18n")
    private fun displayEveryTenthChar(responseString: String) {
        val length = responseString.length
        val char = responseString.toCharArray()
        var myString = ""
        for (i in 0..length.minus(1)) {
            if (i % 10 == 0 && i != 0) {
                myString = myString + " " + char[i - 1]
            }
        }
        every_tenth_character?.text = getString(R.string.text_every_tenth_char) + myString
    }

    private fun createHashMap(responseString: String?) {
        val localResponseString: String? = responseString?.replace("[^a-zA-Z0-9]".toRegex(), " ")

        val splitWords = localResponseString?.split(" +".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()

        if (splitWords != null) {
            for (word in splitWords) {

                if (StringUtil.isNumeric(word)) {
                    continue
                }

                var oldCount = occurrences[word]
                if (oldCount == null) {
                    oldCount = 0
                }
                occurrences[word] = oldCount + 1
            }
        }
        occurrences = sortByValueDesc(occurrences)
        wordsAdapter = WordsAdapter(this, occurrences)
        recyclerView?.adapter = wordsAdapter

        action_btn.gone()
        empty_screen.gone()
        empty_text_view.gone()
        tenth_character.show()
        every_tenth_character.show()
        word_list.show()
    }

    private fun makeApiCall(retrofit: Retrofit) {
        val apiService = retrofit.create(ApiService::class.java)
        val stringCall = apiService.stringResponse
        stringCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {

                    var responseString = response.body()
                    val doc = Jsoup.parse(responseString)
                    responseString = doc.text()
                    Timber.d("Response String: $responseString")
                    displayTenthChar(responseString)
                    displayEveryTenthChar(responseString)
                    createHashMap(responseString)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }

    companion object {

        fun sortByValueDesc(map: Map<String, Int>): HashMap<String, Int> {
            val list = LinkedList(map.entries)
            list.sortWith(Comparator { o1, o2 -> o2.value.compareTo(o1.value) })

            val result = LinkedHashMap<String, Int>()
            for ((key, value) in list) {
                result[key] = value
            }
            return result
        }
    }
}
