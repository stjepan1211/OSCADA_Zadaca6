package ada.osc.taskie.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskList;
import ada.osc.taskie.networking.ApiService;
import ada.osc.taskie.networking.RetrofitUtil;
import ada.osc.taskie.util.NetworkUtil;
import ada.osc.taskie.util.SharedPrefsUtil;
import ada.osc.taskie.view.FavoriteTaskClickListener;
import ada.osc.taskie.view.TaskAdapter;
import ada.osc.taskie.view.TaskClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaginatorTasksFragment extends Fragment {

    @BindView(R.id.tasks)
    RecyclerView tasks;

    private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        tasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasks.setItemAnimator(new DefaultItemAnimator());


        taskAdapter = new TaskAdapter(new TaskClickListener() {
            @Override
            public void onClick(Task task) {

            }

            @Override
            public void onLongClick(Task task) {
                if(!NetworkUtil.isOnline(getActivity().getApplicationContext())){
                    toastCallbackStatus("Not connected");
                    return;
                }

                Retrofit retrofit = RetrofitUtil.createRetrofit(getActivity().getApplicationContext());
                ApiService apiService = retrofit.create(ApiService.class);

                Call postNewTaskCall = apiService
                        .deleteTask(SharedPrefsUtil.getPreferencesField(getActivity().getApplicationContext(),
                                SharedPrefsUtil.TOKEN), task.getmId());

                postNewTaskCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            toastCallbackStatus("Success.");
                            getTasksFromServer();
                        }
                        else {
                            toastCallbackStatus("Error.");
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        toastCallbackStatus("Error.");
                    }
                });
            }
        }, new FavoriteTaskClickListener() {
            @Override
            public void onClick(Task task) {
                if(!NetworkUtil.isOnline(getActivity().getApplicationContext())){
                    toastCallbackStatus("Not connected");
                    return;
                }
                Retrofit retrofit = RetrofitUtil.createRetrofit(getActivity().getApplicationContext());
                ApiService apiService = retrofit.create(ApiService.class);

                Call postNewTaskCall = apiService
                        .postFavoriteTask(SharedPrefsUtil.getPreferencesField(getActivity().getApplicationContext(),
                                SharedPrefsUtil.TOKEN), task.getmId());

                postNewTaskCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            toastCallbackStatus("Success.");
                        }
                        else {
                            toastCallbackStatus("Error.");
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        toastCallbackStatus("Error.");
                    }
                });
            }
        });

        tasks.setAdapter(taskAdapter);

        getTasksFromServer();
    }

    private void toastCallbackStatus(String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getTasksFromServer() {
        if(!NetworkUtil.isOnline(getActivity().getApplicationContext())){
            toastCallbackStatus("Not connected");
            return;
        }

        Retrofit retrofit = RetrofitUtil.createRetrofit(getActivity().getApplicationContext());
        ApiService apiService = retrofit.create(ApiService.class);
        ViewPager viewPager = getActivity().findViewById(R.id.fragmentContainer);

        Call<TaskList> taskListCall = apiService
                .getTasksPaginated(SharedPrefsUtil.getPreferencesField(getActivity()
                        , SharedPrefsUtil.TOKEN), viewPager.getCurrentItem());

        taskListCall.enqueue(new Callback<TaskList>() {
            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                if (response.isSuccessful()) {
                    updateTasksDisplay(response.body().mTaskList);
                }
                else {
                    toastCallbackStatus("Error.");
                }
            }

            @Override
            public void onFailure(Call<TaskList> call, Throwable t) {

            }
        });
    }



    private void updateTasksDisplay(List<Task> taskList) {
        taskAdapter.updateTasks(taskList);
        for (Task t : taskList) {
            Log.d("taskovi", t.getTitle());
        }
    }

}
