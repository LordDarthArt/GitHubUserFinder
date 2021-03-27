package tk.lorddarthart.githubuserfinder.common.network

import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tk.lorddarthart.githubuserfinder.common.constants.UrlConstants.BASE_URL

class HttpServiceHelper {
    private val mRetrofit: Retrofit

    val jsonApi: JSONPlaceHolderApi
        get() = mRetrofit.create(JSONPlaceHolderApi::class.java)

    init {
        val interceptor = HttpLoggingInterceptor()
            .apply { this.level = HttpLoggingInterceptor.Level.BODY }

        val dispatcher = Dispatcher().apply {
            maxRequests = 1
        }

        val client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(interceptor)
            .build()

        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        private var _instance: HttpServiceHelper? = null
        val instance: HttpServiceHelper?
            get() {
                if (_instance == null) {
                    _instance = HttpServiceHelper()
                }
                return _instance
            }
    }
}