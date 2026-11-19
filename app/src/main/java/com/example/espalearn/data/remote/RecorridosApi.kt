package com.example.espalearn.data.remote

import com.example.espalearn.domain.ev3.*
import com.example.espalearn.model.Etapa
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.UUID
import java.util.concurrent.TimeUnit

/** Función: declarar la API propia; Android nunca recibe credenciales SQL ni la clave Groq. */
interface RecorridosApi {
    // Lecturas: 204 en activo significa ausencia; errores HTTP no se convierten en null.
    @GET("api/v1/aprendices/{aprendiz}/recorridos/activo")
    suspend fun activo(@Path("aprendiz") aprendiz: String): Response<RecorridoDto>
    @GET("api/v1/aprendices/{aprendiz}/recorridos/{id}")
    suspend fun obtener(@Path("aprendiz") aprendiz: String, @Path("id") id: String): RecorridoDto
    @GET("api/v1/aprendices/{aprendiz}/progreso")
    suspend fun progreso(@Path("aprendiz") aprendiz: String, @Query("version") version: String): ProgresoDto
    @GET("api/v1/catalogo")
    suspend fun catalogo(@Query("version") version: String): CatalogoDto

    // Escrituras: UUID estable para resolver el resultado de una petición repetida o incierta.
    @POST("api/v1/aprendices/{aprendiz}/recorridos")
    suspend fun crear(@Path("aprendiz") aprendiz: String, @Body entrada: InicioDto): RecorridoDto
    @PUT("api/v1/aprendices/{aprendiz}/recorridos/{id}/etapas/{etapa}")
    suspend fun cerrar(@Path("aprendiz") aprendiz: String, @Path("id") id: String,
                      @Path("etapa") etapa: String, @Body entrada: CierreDto): RecorridoDto

    companion object {
        // Construcción única: tiempos acotados y reintento sólo por acción explícita del usuario.
        fun crear(baseUrl: String): RecorridosApi = Retrofit.Builder().baseUrl(baseUrl)
            .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).callTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false).build())
            .addConverterFactory(GsonConverterFactory.create()).build().create(RecorridosApi::class.java)
    }
}

/** Función: adaptar JSON no confiable a contratos validados; Gson puede producir campos nulos. */
data class RecorridoDto(val recorridoId: String?, val versionCatalogo: String?, val tipo: TipoRecorrido?,
                        val estado: EstadoRecorrido?, val palabraIds: List<String>?, val etapasConfirmadas: List<String>?) {
    // Mapeo explícito: Etapa.id usa minúsculas en HTTP, no el nombre del enum Kotlin.
    fun validar(): RecorridoEv3 {
        UUID.fromString(requireNotNull(recorridoId))
        return RecorridoEv3(recorridoId, requireNotNull(versionCatalogo), requireNotNull(tipo),
            requireNotNull(estado), requireNotNull(palabraIds).toList(),
            requireNotNull(etapasConfirmadas).map { requireNotNull(Etapa.desdeId(it)) })
    }
}
data class ProgresoDto(val completadasUnicas: Int?, val totalPalabras: Int?, val primerRecorridoCompletado: Boolean?) {
    fun validar() = ProgresoEv3(requireNotNull(completadasUnicas), requireNotNull(totalPalabras), requireNotNull(primerRecorridoCompletado))
}
data class CartaDto(val palabraId: String?, val versionCatalogo: String?, val textoEspanol: String?,
                    val apoyoTurco: String?, val explicacion: String?, val imagenClave: String?,
                    val opciones: List<OpcionUnidadDto>?, val respuestaEsperadaId: String?) {
    // Contenido: la validez del catálogo es independiente de una descarga HTTP exitosa.
    fun validar() = PalabraCatalogo(requireNotNull(palabraId), requireNotNull(versionCatalogo),
        requireNotNull(textoEspanol), requireNotNull(apoyoTurco), requireNotNull(explicacion), requireNotNull(imagenClave),
        requireNotNull(opciones).map {
            val opcion = requireNotNull(it)
            OpcionCatalogo(requireNotNull(opcion.id), requireNotNull(opcion.textoEspanol))
        },
        requireNotNull(respuestaEsperadaId))
}
data class OpcionUnidadDto(val id: String?, val textoEspanol: String?)
data class CatalogoDto(val versionCatalogo: String?, val palabras: List<CartaDto>?)
// Entrada: no se envían notas, nombres, edades, imágenes ni un lote elegido por el cliente.
data class InicioDto(val solicitudId: String, val tipo: String = "NORMAL", val versionCatalogo: String)
data class CierreDto(val solicitudId: String, val versionCatalogo: String, val etapa: String, val resultados: List<ResultadoPalabra>)
