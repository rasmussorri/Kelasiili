package com.example.myapplication.apiServices;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


// Käytetään Retrofit-kirjastoa, joka tekee API-kutsut ja hoitaa JSON parsimiset
// Ei tarvitse omaa API luokkaa tms niin kuin aiemmassa viikkotehtävässä
public interface MunicipalityApiService {
    @Headers("Content-Type: application/json")
    @POST("StatFin/synt/statfin_synt_pxt_12dy.px")
    Call<JsonObject> getPopulationAndChange(@Body JsonObject query);

    @POST("StatFin/tyokay/statfin_tyokay_pxt_125s.px")
    Call<JsonObject> getJobSelfReliance(@Body JsonObject query);

    @POST("StatFin/tyokay/statfin_tyokay_pxt_115x.px")
    Call<JsonObject> getEmploymentRate(@Body JsonObject query);
}
