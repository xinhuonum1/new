package com.ypr.whb.game;

import com.ypr.whb.connect.MySocket;
import com.ypr.whb.data.GameCenter;
import com.ypr.whb.data.Player;
import com.ypr.whb.view.MyDialog;
import com.ypr.whb.view.UserPanel;

/**
 * 联机对战类
 *
 * @author chaos
 */
public class GameOnline {

    private GameOnline() {
    }

    public static void reStart() {
        GameCenter.reStart();
        GameCenter.setMode(GameCenter.MODE_ONLINE);
    }

    /**
     * 启动监控线程(监控对方是否下线)
     */
    public static void monitor() {
        // 用于检测游戏是否结束
        new Thread(() -> {
            while (MySocket.isStart) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            MyDialog.monitor();
            System.out.println("在线监控线程 已终止！");
        }).start();
    }

    /**
     * 相互发送接收玩家信息
     */
    public static void getOtherAddress() {
        // 用于检测游戏是否结束
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (MySocket.isStart) {
                    Player.otherPlayer.setAddress(MySocket.peAddress);
                    UserPanel.setUserInfo(Player.otherPlayer, UserPanel.right);
                }
            } catch (InterruptedException e) {
            }

        }).start();
    }

}
