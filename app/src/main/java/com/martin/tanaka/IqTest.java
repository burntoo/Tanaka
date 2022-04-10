package com.martin.tanaka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.martin.tanaka.Adapter.QuestionsAdapter;
import com.martin.tanaka.Model.QuestionsModel;
import com.martin.tanaka.databinding.ActivityIqTestBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IqTest extends AppCompatActivity {

    ActivityIqTestBinding binding;

    private QuestionsAdapter mAdapter;

    String choice;

    private List<QuestionsModel> questionsList = new ArrayList<>();

    String iqQuestions = "{\"quiz\":[{\"question\":\"Which answer expresses the meaning of the word reassuring?\",\"choice1\":\"compassionate\",\"choice2\":\"comforting\",\"choice3\":\"explanatory\",\"choice4\":\"meddlesome\",\"ans\":\"comforting\"},{\"question\":\"Which number logically follows this series?  4     6     9     6     14     6\",\"choice1\":\"6\",\"choice2\":\"17\",\"choice3\":\"19\",\"choice4\":\"21\",\"ans\":\"19\"},{\"question\":\"Tom has a new set of golf clubs. Using a club 8, the ball travels an average distance of 100 meters. Using a club 7, the ball travels an average distance of 108 meters. Using a club 6, the ball travels an average distance of 114 meters. How far will the ball go if Tom uses a club 5?\",\"choice1\":\"122 meters\",\"choice2\":\"120 meters\",\"choice3\":\"118 meters\",\"choice4\":\"116 meters\",\"ans\":\"118 meters\"},{\"question\":\"Which answer expresses the meaning opposite of that of word tough?\",\"choice1\":\"cowardly\",\"choice2\":\"tender\",\"choice3\":\"starch\",\"choice4\":\"masculine\",\"ans\":\"tender\"},{\"question\":\"Which conclusion follows from this statements with absolute certainty? 1). None of the stamp collectors is an architect. 2). All the drones are stamp collectors.\",\"choice1\":\"all stamp collectors are architects\",\"choice2\":\"architects are not drones\",\"choice3\":\"no stamp collectors are drones\",\"choice4\":\"some drones are architects\",\"ans\":\"architects are not drones\"}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIqTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBar.toolTitle.setText("IQ Test");

        binding.appBar.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IqTest.this, Login.class);
                startActivity(intent);
            }
        });

        mAdapter = new QuestionsAdapter(IqTest.this, questionsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(IqTest.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), linearLayoutManager.getOrientation());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        binding.recyclerView.setAdapter(mAdapter);

        try {
            JSONObject obj = new JSONObject(iqQuestions);

            Log.d("MyApp", obj.toString());

            JSONArray array = new JSONArray(obj.getString("quiz"));

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                final boolean[] isDismissed = {false};
                AlertDialog alert = new AlertDialog.Builder(IqTest.this)
                        .setTitle(object.getString("question"))
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isDismissed[0] = true;
                                Log.e("TAG", "click: No");

                            }
                        })
                        .create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();

                while(!isDismissed[0]) {
                    //do nothing
                }

                QuestionsModel questionsModel = new QuestionsModel(
                        object.getString("question"),
                        object.getString("choice1"),
                        object.getString("choice2"),
                        object.getString("choice3"),
                        object.getString("choice4"),
                        object.getString("ans"));
                questionsList.add(questionsModel);
            }

            mAdapter.notifyDataSetChanged();
            binding.recyclerView.setAdapter(mAdapter);

        } catch (Throwable t) {
            Log.e("MyApp", "Could not parse malformed JSON: " + iqQuestions);
        }

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}