package ada.osc.taskie.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class FavoriteTasksFragment extends Fragment {

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

            }
        }, new FavoriteTaskClickListener() {
            @Override
            public void onClick(Task task) {

            }
        });

        tasks.setAdapter(taskAdapter);

        getTasksFromServer();
    }

    private void getTasksFromServer() {
        if(!NetworkUtil.isOnline(getActivity().getApplicationContext())){
            toastMessage("Not connected");
            return;
        }

        Retrofit retrofit = RetrofitUtil.createRetrofit(getActivity().getApplicationContext());
        ApiService apiService = retrofit.create(ApiService.class);

        Call<TaskList> taskListCall = apiService
                .getTasksFavorite(SharedPrefsUtil.getPreferencesField(getActivity()
                        , SharedPrefsUtil.TOKEN));

        taskListCall.enqueue(new Callback<TaskList>() {
            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                if (response.isSuccessful()) {
                    updateTasksDisplay(response.body().mTaskList);
                }
            }

            @Override
            public void onFailure(Call<TaskList> call, Throwable t) {

            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateTasksDisplay(List<Task> taskList) {
        taskAdapter.updateTasks(taskList);
        for (Task t : taskList) {
            Log.d("taskovi", t.getTitle());
        }
    }

}
