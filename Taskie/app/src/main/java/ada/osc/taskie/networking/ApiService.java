package ada.osc.taskie.networking;

import ada.osc.taskie.model.LoginResponse;
import ada.osc.taskie.model.RegistrationToken;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/register/")
    Call<RegistrationToken> registerUser(@Body RegistrationToken registrationToken);

    @POST("api/login/")
    Call<LoginResponse> loginUser(@Body RegistrationToken registrationToken);

    @POST("api/note/")
    Call<Task> postNewTask(@Header("authorization") String header, @Body Task task);

    @POST("api/note/favorite/")
    Call<Task> postFavoriteTask(@Header("authorization") String header, @Query("noteId") String taskId);

    @GET("api/note/")
    Call<TaskList> getTasks(@Header("authorization") String header);

    @GET("api/note/")
    Call<TaskList> getTasksPaginated(@Header("authorization") String header, @Query("noteId") int pageNumber);

    @GET("api/note/favorite/")
    Call<TaskList> getTasksFavorite(@Header("authorization") String header);

    @POST("api/note/delete")
    Call<TaskList> deleteTask(@Header("authorization") String header, @Query ("noteId") String taskId);
}
