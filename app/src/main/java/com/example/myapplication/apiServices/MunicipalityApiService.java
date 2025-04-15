package com.example.myapplication.apiServices;

public class MunicipalityApiService {
    @POST("StatFin/synt/statfin_synt_pxt_12dy.px")
    Call<JsonObject> getPopulationData(@Body JsonObject query);

    @POST("StatFin/tyokay/statfin_tyokay_pxt_125s.px")
    Call<JsonObject> getJobSelfReliance(@Body JsonObject query);

    @POST("StatFin/tyokay/statfin_tyokay_pxt_115x.px")
    Call<JsonObject> getEmploymentRate(@Body JsonObject query);
}
