package com.hyunec.cosmicbaseballinit.domain.baseball.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hyunec.cosmicbaseballinit.domain.baseball.model.Batting.BALL;
import static com.hyunec.cosmicbaseballinit.domain.baseball.model.Batting.STRIKE;
import com.hyunec.cosmicbaseballinit.domain.baseball.exception.ExceptionMessage;

@Component
public class PlateAppearances {

    private final List<Batting> battings = new ArrayList<>();
    private final Map<String, String> player = new HashMap<String, String>();

    private static PlateAppearances pa;
    Integer totOut = 0;
    boolean bPlaying = false;

    public PlateAppearances() {
        player.put("1player", "N"); //N 게임 미진행, U: 게임진행중, E: 게임끝
        //player.put("2player", "N"); //N 게임 미진행, U: 게임진행중, E: 게임끝
        //player.put("3player", "N"); //N 게임 미진행, U: 게임진행중, E: 게임끝
    }

    public static synchronized PlateAppearances getInstance() {
        if(pa == null) {
            pa = new PlateAppearances();
        }
        return pa;
    }

    public boolean getPlaying() {
        return this.bPlaying;
    }

    public void setPlaying(boolean is) {
        this.bPlaying = is;
    }

    public Integer getTotOutCount() {
        return this.totOut;
    }

    public void setTotOutCount() {
        this.totOut++;
    }

    public void batting(final Batting batting) {
        setPlaying(true);
        battings.add(batting);
    }

    public Integer strikeCount() {
        return Math.toIntExact(battings.stream().filter(STRIKE::equals).count());
    }

    public Integer ballCount() {
        return Math.toIntExact(battings.stream().filter(BALL::equals).count());
    }

    public BattingResult result() {
        if (strikeCount().equals(BattingResult.OUT.getValue())) {
            setPlaying(false);
            return BattingResult.OUT;
        }

        if (ballCount().equals(BattingResult.FOUR_BALL.getValue())) {
            setPlaying(false);
            return BattingResult.FOUR_BALL;
        }

        return BattingResult.of(battings.get(battings.size() - 1));
    }

    public void clear() {
        battings.clear();
    }

    //1플레이어 OUT 체크
    public boolean getPlayCheck() {
        if (bPlaying) {
            return true;
        }
        return false;
    }

    public boolean fourBallCheck() {
        boolean isFourBallorOut = false;
        if(!(pa.strikeCount() == 0 && pa.ballCount() == 0)) {
            if(pa.result().equals(BattingResult.FOUR_BALL) || pa.result().equals(BattingResult.OUT)) {
                isFourBallorOut = true;
            }
        }
        return isFourBallorOut;
    }

    public void batting () {
        setPlaying(true);

        if(fourBallCheck()){
            setTotOutCount();
            battings.clear();
            setPlaying(false);

            //3OUT - NEW CHANGE GAME
            if(getTotOutCount() == 3) {
                this.totOut = 0;
            }
            return;
        }
        pa.batting(Batting.generate());	//4볼이나 OUT이 아니면 리스트에 계속 담는다 > 플레이어 끝
        batting();
    }
    public void newGame(String str) {
        if("force".equals(str)) {
            totOut = 0;
            battings.clear();
        }
    }

    public String newGame() {

        if(fourBallCheck()) {
            totOut = 0;
            battings.clear();
//            throw new IllegalStateException(ExceptionMessage.CANNOT_PROCEED_ATBAT);
            return ExceptionMessage.NEW_GAME_START;

        } else {
//            throw new IllegalStateException(ExceptionMessage.NEW_GAME_START);
            return ExceptionMessage.CANNOT_PROCEED_ATBAT;
        }

    }

}
