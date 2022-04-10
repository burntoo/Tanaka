package com.martin.tanaka.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martin.tanaka.Model.QuestionsModel;
import com.martin.tanaka.R;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

    private List<QuestionsModel> list;
    private Context context;

    public QuestionsAdapter(Context context, List<QuestionsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.questions_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionsModel questionsModel = list.get(position);
        holder.question.setText(questionsModel.getQuestion());
        holder.choice1.setText(questionsModel.getChoice1());
        holder.choice2.setText(questionsModel.getChoice2());
        holder.choice3.setText(questionsModel.getChoice3());
        holder.choice4.setText(questionsModel.getChoice4());

        String correctAnswer = questionsModel.getAnswer();
        final String[] answer = {""};

        holder.choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                answer[0] = radioButton.getText().toString();

                if(correctAnswer.equals(answer[0])){
                    Log.d("clicked", "CORRECT");
                }
                else{
                    Log.d("clicked", "InCORRECT");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView question;
        RadioButton choice1, choice2, choice3, choice4;

        RadioGroup choice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.txt_question);
            choice = itemView.findViewById(R.id.rg_group_radio);
            choice1 = itemView.findViewById(R.id.ans1);
            choice2 = itemView.findViewById(R.id.ans2);
            choice3 = itemView.findViewById(R.id.ans3);
            choice4 = itemView.findViewById(R.id.ans4);
        }
    }
}
