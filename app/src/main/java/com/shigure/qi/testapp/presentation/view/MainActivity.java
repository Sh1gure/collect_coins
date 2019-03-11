package com.shigure.qi.testapp.presentation.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shigure.qi.testapp.R;
import com.shigure.qi.testapp.presentation.contract.MainScreen;
import com.shigure.qi.testapp.presentation.contract.MainScreen.State;
import com.shigure.qi.testapp.presentation.contract.MainScreen.State.*;
import com.shigure.qi.testapp.presentation.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Component;

public class MainActivity extends MainScreen.View {

    MainScreen.Presenter presenter = new MainPresenter();

    private List<ImageView> cells;
    private View.OnClickListener coinClickListener;
    private TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreText = findViewById(R.id.scoreText);

        setUpClickListeners();
    }

    private void setUpClickListeners() {
        coinClickListener = v ->
                presenter.coinClicked(v.getId(), ((int) v.getTag()));
    }

    @Override
    public void setState(State state) {
        if (state instanceof OnCreate) {
            cells = new ArrayList<>();

            for (int positionId : ((OnCreate) state).positions) {
                ImageView coin = findViewById(positionId);
                coin.setTag(R.drawable.empty);
                coin.setOnClickListener(coinClickListener);
                cells.add(coin);
            }

            presenter.startRound();
        }

        if (state instanceof OnShow) {
            OnShow currentState = (OnShow) state;
            ImageView coin = findViewById(currentState.positionId);
            Drawable coinDrawable =
                    ContextCompat.getDrawable(this, currentState.colorDrawable);
            String scoreString = currentState.score + "";

            scoreText.setText(scoreString);
            coin.setImageDrawable(coinDrawable);
            coin.setTag(currentState.colorDrawable);

            presenter.startRound();
        }

        if (state instanceof OnDispose) {
            OnDispose currentState = (OnDispose) state;
            Drawable coinDrawable =
                    ContextCompat.getDrawable(this, R.drawable.empty);
            String scoreString = currentState.score + "";

            scoreText.setText(scoreString);
            for (ImageView item : cells) {
                item.setImageDrawable(coinDrawable);
            }

            presenter.startRound();
        }

        if (state instanceof OnFail) {
            OnFail currentState = (OnFail) state;
            String scoreString = currentState.score + "";

            scoreText.setText(scoreString);
            View endGame = findViewById(R.id.end_game_element);
            endGame.setVisibility(View.VISIBLE);
            Button newGame = findViewById(R.id.new_game);
            newGame.setOnClickListener(v -> {
                presenter.endGameClicked();
                endGame.setVisibility(View.GONE);
                Drawable coinDrawable =
                        ContextCompat.getDrawable(this, R.drawable.empty);
                for (ImageView item : cells) {
                    item.setImageDrawable(coinDrawable);
                }
                presenter.startRound();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe(this);
        presenter.init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }
}
