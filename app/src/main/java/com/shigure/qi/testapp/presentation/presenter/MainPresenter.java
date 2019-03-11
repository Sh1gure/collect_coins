package com.shigure.qi.testapp.presentation.presenter;

import android.os.Handler;
import android.support.annotation.DrawableRes;

import com.shigure.qi.testapp.R;
import com.shigure.qi.testapp.presentation.contract.MainScreen;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class MainPresenter implements MainScreen.Presenter {

    private final static int TIK_TIME = 1000;
    private final static int SILVER_VALUE = 1;
    private final static int GOLD_VALUE = 2;
    private final static int RED_VALUE = -5;

    private WeakReference<MainScreen.View> view;
    private Handler gameHandler;
    private Random random = new Random();

    private int currentColor;
    private int currentCoinId;
    private int currentScore = 0;
    private int updateCount = 0;

    @DrawableRes
    private final int silverCoin = R.drawable.silver;

    @DrawableRes
    private final int goldCoin = R.drawable.gold;

    @DrawableRes
    private final int redCoin = R.drawable.red;

    private List<Integer> colors = Arrays.asList(
            silverCoin,
            goldCoin,
            redCoin
    );

    private List<Integer> coins = Arrays.asList(
            R.id.pos0,
            R.id.pos1,
            R.id.pos2,
            R.id.pos3,
            R.id.pos4,
            R.id.pos5,
            R.id.pos6,
            R.id.pos7,
            R.id.pos8
    );

    @Override
    public void init() {
        gameHandler = new Handler();
        view.get().setState(
                new MainScreen.State.OnCreate(coins)
        );
    }


    private Runnable handleRound = () -> {
        updateCount++;

        if (view.get() != null) {
            if (updateCount < 2) {

                currentCoinId =
                        coins.get(
                                random.nextInt(coins.size())
                        );

                currentColor =
                        colors.get(
                                random.nextInt(colors.size())
                        );

                view.get().setState(
                        new MainScreen.State.OnShow(currentCoinId, currentColor, currentScore)
                );
            } else {
                updateCount = 0;
                currentColor = -1;
                currentCoinId = -1;

                view.get().setState(
                        new MainScreen.State.OnDispose(currentScore)
                );
            }
        }
    };

    @Override
    public void startRound() { // Тут запускаем раунд
        gameHandler.postDelayed(handleRound, TIK_TIME);
    }

    @Override
    public void coinClicked(int coinId, @DrawableRes int coinType) {
        if (coinId == currentCoinId && currentScore >= -2) {
            updateCount = 0;

            switch (coinType) {
                case silverCoin:
                    currentScore += SILVER_VALUE;
                    break;
                case goldCoin:
                    currentScore += GOLD_VALUE;
                    break;
                case redCoin:
                    currentScore += RED_VALUE;
                    break;
            }

            gameHandler.removeCallbacks(handleRound);

            if (currentScore < -2) {
                view.get().setState(
                        new MainScreen.State.OnFail(currentScore)
                );
            } else {
                view.get().setState(
                        new MainScreen.State.OnDispose(currentScore)
                );
            }
            currentCoinId = -1;
        }
    }

    @Override
    public void endGameClicked() {
        currentScore = 0;
    }

    @Override
    public void subscribe(MainScreen.View view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public void unsubscribe() {
        view.clear();
    }
}
