package daluobo.insplash.net.api;

import android.arch.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import daluobo.insplash.base.arch.ApiResponse;
import daluobo.insplash.model.Collection;
import daluobo.insplash.model.Photo;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by daluobo on 2017/11/27.
 */

public interface CollectionsApi {

    @GET("/collections")
    LiveData<ApiResponse<List<Collection>>> collections(@Query("page") int page);

    @GET("/collections/featured")
    LiveData<ApiResponse<List<Collection>>> featured(@Query("page") int page);

    @GET("/collections/curated")
    LiveData<ApiResponse<List<Collection>>> curated(@Query("page") int page);

    @GET("/collections/{id}")
    LiveData<ApiResponse<Collection>> collection(@Path("id") String id);

    @GET("/collections/{id}/photos")
    LiveData<ApiResponse<List<Photo>>> photos(@Path("id") String id, @Query("page") int page);

    @FormUrlEncoded
    @POST("/collections")
    LiveData<ApiResponse<Collection>> createCollection(@FieldMap Map<String, Object> param);

    @FormUrlEncoded
    @POST("/collections/{collection_id}/add")
    LiveData<ApiResponse<Photo>> addToCollection(@Path("collection_id") String id, @Field("photo_id") String collectionId, @Field("photo_id") String photoId);
}
