package com.k.sekiro.weatherapp.data.util

import com.k.sekiro.weatherapp.domain.util.NetworkError
import com.k.sekiro.weatherapp.domain.util.Result
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(
    execute:() -> HttpResponse
): Result<T, NetworkError>{

    val response = try {
        execute()
    }catch (ex: UnresolvedAddressException){
        return Result.Error(NetworkError.NO_INTERNET)
    }catch (ex: SerializationException){
        return Result.Error(NetworkError.SERIALIZATION)
    }catch (ex: ContentConvertException){
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (ex: Exception){
        coroutineContext.ensureActive()
        return Result.Error(NetworkError.UNKNOWN)
    }

    return responseToResult(response)
}