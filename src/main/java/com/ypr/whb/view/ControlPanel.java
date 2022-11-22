package com.ypr.whb.view;

import com.ypr.whb.connect.MySocket;
import com.ypr.whb.data.GameCenter;
import com.ypr.whb.data.Spot;
import com.ypr.whb.game.AutoChess;
import com.ypr.whb.game.GameCoupe;
import com.ypr.whb.game.GameOnline;
import com.ypr.whb.game.GameRobot;

import javax.swing.*;
import java.awt.*;

/**
 * 主界面右边的控制按钮面板
 * @author 帝睿
 */
class ControlPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    static ControlPanel my;
    private JButton btnShowChess, btnAgain, btnChessAi, btnTest, btnCoupe, btnOnline, btnRobot;

    ControlPanel() {


        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnAgain = new JButton("重新游戏");
        btnCoupe = new JButton("双人对战");
        btnRobot = new JButton("人机对战");
        btnOnline = new JButton("联机对战");
        btnChessAi = new JButton("智能AI下棋");
        btnShowChess = new JButton("显示所有棋子");
        btnTest = new JButton("备用测试按钮");

        this.add(btnAgain);
        this.add(btnCoupe);
        this.add(btnRobot);
        this.add(btnOnline);
        this.add(btnChessAi);
        this.add(btnShowChess);
        this.add(btnTest);
        my = this;

        addListener();
    }

    static void init() {
        my.repaint();
    }

    /**
     * 添加监听事件
     */
    private void addListener() {
        btnShowChess.addActionListener(event -> GameCenter.showChess());

        btnAgain.addActionListener(event -> {
            GameCenter.reStart();
            ChessBroad.myBroad.repaint();
            MySocket.close();
            btnCoupe.setEnabled(true);
            btnRobot.setEnabled(true);
            btnOnline.setEnabled(true);
            btnChessAi.setEnabled(true);
            try {
                if(MySocket.socket!=null && MySocket.socket.isConnected()){
                    MySocket.socket.close();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnChessAi.addActionListener(event -> {
            if (GameCenter.isEnd()) {
                JOptionPane.showMessageDialog(null, "游戏未开始，无法使用AI下棋", "提示信息", JOptionPane.WARNING_MESSAGE);
            } else {
                new AutoChess();
            }
        });

        btnTest.addActionListener(event -> {
            ChessBroad.myBroad.repaint();
            System.out.println("----------黑棋权值-------");
            GameCenter.showWeight(Spot.blackChess);

            System.out.println("----------白棋权值-------");
            GameCenter.showWeight(Spot.whiteChess);
        });

        btnCoupe.addActionListener(event -> {
            GameCoupe.reStart();
            btnCoupe.setEnabled(false);
            btnRobot.setEnabled(false);
            btnOnline.setEnabled(false);
        });

        btnRobot.addActionListener(event -> {
            GameRobot.reStart();
            btnCoupe.setEnabled(false);
            btnRobot.setEnabled(false);
            btnOnline.setEnabled(false);
        });

        btnOnline.addActionListener(event -> {
            // new 一定要在前面，否则数据被重置！
            GameOnline.reStart();
            MyDialog.online();

            btnCoupe.setEnabled(false);
            btnRobot.setEnabled(false);
            btnOnline.setEnabled(false);
            btnChessAi.setEnabled(false);
        });
    }
}
