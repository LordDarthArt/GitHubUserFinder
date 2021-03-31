package tk.lorddarthart.githubuserfinder.di

import androidx.lifecycle.ViewModelProvider
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tk.lorddarthart.githubuserfinder.common.constants.UrlConstants
import tk.lorddarthart.githubuserfinder.common.network.GithubApi
import tk.lorddarthart.githubuserfinder.domain.local.Session
import tk.lorddarthart.githubuserfinder.domain.repository.auth.AuthRepository
import tk.lorddarthart.githubuserfinder.domain.repository.auth.AuthRepositoryImpl
import tk.lorddarthart.githubuserfinder.domain.repository.profile.ProfileRepository
import tk.lorddarthart.githubuserfinder.domain.repository.profile.ProfileRepositoryImpl
import tk.lorddarthart.githubuserfinder.domain.repository.search.SearchRepository
import tk.lorddarthart.githubuserfinder.domain.repository.search.SearchRepositoryImpl
import tk.lorddarthart.githubuserfinder.view.activity.MainActivityViewModel
import tk.lorddarthart.githubuserfinder.view.auth.AuthViewModel
import tk.lorddarthart.githubuserfinder.view.auth.additional.AuthBoxViewModel
import tk.lorddarthart.githubuserfinder.view.main.favourite.FavouriteFragment
import tk.lorddarthart.githubuserfinder.view.main.favourite.FavouriteViewModel
import tk.lorddarthart.githubuserfinder.view.main.profile.ProfileViewModel
import tk.lorddarthart.githubuserfinder.view.main.search.SearchViewModel
import java.util.concurrent.TimeUnit

val utilityModule = DI.Module("utility") {
    bind<Session>() with singleton { Session() }
    bind<HttpLoggingInterceptor>() with singleton { HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY } }
    bind<Dispatcher>() with singleton { Dispatcher().apply { maxRequests = 1 } }
    bind<OkHttpClient>() with singleton { OkHttpClient.Builder().dispatcher(instance()).addInterceptor(instance() as HttpLoggingInterceptor).callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build() }
    bind<Retrofit>() with singleton { Retrofit.Builder().baseUrl(UrlConstants.BASE_URL).client(instance()).addCallAdapterFactory(FlowCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build() }
    bind<GithubApi>() with singleton { (instance() as Retrofit).create(GithubApi::class.java) }
}

val repositoryModule = DI.Module("repository") {
    bind<AuthRepository>() with singleton { AuthRepositoryImpl() }
    bind<SearchRepository>() with singleton { SearchRepositoryImpl(instance()) }
    bind<ProfileRepository>() with singleton { ProfileRepositoryImpl() }
}

val viewModelsModule = DI.Module("viewModels") {
    bind<ViewModelProvider.Factory>() with singleton { ViewModelFactory(this) }
    bindViewModel<MainActivityViewModel>() with  provider { MainActivityViewModel() }
    bindViewModel<AuthViewModel>() with provider { AuthViewModel() }
    bindViewModel<SearchViewModel>() with provider { SearchViewModel(instance()) }
    bindViewModel<ProfileViewModel>() with provider { ProfileViewModel() }
    bindViewModel<AuthBoxViewModel>() with provider { AuthBoxViewModel() }
    bindViewModel<FavouriteViewModel>() with provider { FavouriteViewModel() }
}