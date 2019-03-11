package com.shigure.qi.testapp.presentation.contract;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import java.util.List;


public interface MainScreen {
    abstract class View extends AppCompatActivity {
        public abstract void setState(State state);
    }

    interface Presenter {

        void init();

        void startRound();

        void coinClicked(@IdRes int coinId, @DrawableRes int coinType);

        void endGameClicked();

        void subscribe(View view);

        void unsubscribe();
    }

    abstract class State {
        public static class OnCreate extends State {
            public List<Integer> positions;

            public OnCreate(List<Integer> positions) {
                this.positions = positions;
            }
        }

        public static class OnShow extends State {
            @IdRes
            public int positionId;

            @DrawableRes
            public int colorDrawable;

            public int score;

            public OnShow(int coinId, int colorDrawable, int score) {
                this.positionId = coinId;
                this.colorDrawable = colorDrawable;
                this.score = score;

            }
        }

        public static class OnDispose extends State {
            public int score;

            public OnDispose(int score) {
                this.score = score;
            }
        }

        public static class OnFail extends State {
            public int score;

            public OnFail(int score) {
                this.score = score;
            }
        }
    }
}
