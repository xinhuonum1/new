package com.ypr.whb.game;

import com.ypr.whb.data.*;
import com.ypr.whb.view.ChessBroad;
import com.ypr.whb.view.UserPanel;

/**
 * 人机对战
 *
 * @author chaos
 */
public class GameRobot {
    private GameRobot() {
    }

    public static void reStart() {
        GameCenter.reStart();
        GameCenter.setMode(GameCenter.MODE_ROBOT);
        Player.myPlayer.start(Spot.blackChess);
        final PlayerAI playerAI = new PlayerAI();
        playerAI.start(Spot.whiteChess);
        playerAI.setGrade(Player.otherPlayer.getGrade());

        // 用户面板，更新用户信息
        UserPanel.setUserInfo(Player.myPlayer, UserPanel.left);
        UserPanel.setUserInfo(playerAI, UserPanel.right);


        new Thread(() -> {
            String color = playerAI.getColor();
            while (!GameCenter.isEnd()) {
                //避免性能损耗太多
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("robotThread 睡眠异常！");
                }
                if (TableData.getNowColor().equals(color)) {
                    Spot spot = playerAI.getBestChess(color);
                    ChessBroad.submitPaint(spot);
                }
            }
            System.out.println("电脑下棋线程已停止");
        }).start();
    }
}
