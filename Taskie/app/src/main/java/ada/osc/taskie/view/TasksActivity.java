package ada.osc.taskie.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.TaskRepository;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskList;
import ada.osc.taskie.networking.ApiService;
import ada.osc.taskie.networking.RetrofitUtil;
import ada.osc.taskie.util.NetworkUtil;
import ada.osc.taskie.util.SharedPrefsUtil;
import ada.osc.taskie.view.fragments.PaginatorTasksFragment;
import ada.osc.taskie.view.fragments.FavoriteTasksFragment;
import ada.osc.taskie.view.fragments.TasksPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.getSimpleName();
    private static final int REQUEST_NEW_TASK = 10;
    public static final String EXTRA_TASK = "task";

    TaskRepository mRepository = TaskRepository.getInstance();
    TaskAdapter mTaskAdapter;

    @BindView(R.id.fab_tasks_addNew)
    FloatingActionButton mNewTask;

    @BindView(R.id.fragmentContainer)
    ViewPager viewPager;

    private TasksPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ButterKnife.bind(this);
        adapter = new TasksPagerAdapter(getSupportFragmentManager());

        final List<Fragment> pages = new ArrayList<>();

        //get tasks list size
        //bad example but workaround to know list size to properly set
        //dynamic number of tabs
        //five tasks per page will be displayed
        if(!NetworkUtil.isOnline(getApplicationContext())){
            toastCallbackStatus("Not connected");
            return;
        }

        Retrofit retrofit = RetrofitUtil.createRetrofit(getApplicationContext());
        ApiService apiService = retrofit.create(ApiService.class);

        Call<TaskList> taskListCall = apiService
                .getTasks(SharedPrefsUtil.getPreferencesField(getApplicationContext()
                        , SharedPrefsUtil.TOKEN));

        taskListCall.enqueue(new Callback<TaskList>() {
            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                if (response.isSuccessful()) {
                    int numberOfTasks = response.body().mTaskList.size();
                    int numberOfPages = 0;

                    if(numberOfTasks % 5 == 0){
                        numberOfPages = numberOfTasks / 5;
                    }
                    else {
                        numberOfPages = (numberOfTasks / 5) + 1;
                    }

                    for (int i = 0; i < numberOfPages; i++){
                        pages.add(new PaginatorTasksFragment());
                    }
                    pages.add(new FavoriteTasksFragment());
                    adapter.setItems(pages);
                    viewPager.setAdapter(adapter);
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

    private void toastTask(Task task) {
        Toast.makeText(
                this,
                task.getTitle() + "\n" + task.getDescription(),
                Toast.LENGTH_SHORT
        ).show();
    }

    @OnClick(R.id.fab_tasks_addNew)
    public void startNewTaskActivity() {
        Intent newTask = new Intent();
        newTask.setClass(this, NewTaskActivity.class);
        startActivityForResult(newTask, REQUEST_NEW_TASK);
    }

    private void toastCallbackStatus(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
